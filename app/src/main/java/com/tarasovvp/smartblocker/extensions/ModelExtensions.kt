package com.tarasovvp.smartblocker.extensions

import android.content.Context
import android.database.Cursor
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.provider.CallLog
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import android.util.Log
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants.ASC
import com.tarasovvp.smartblocker.constants.Constants.BLOCKED_CALL
import com.tarasovvp.smartblocker.constants.Constants.CALL_ID
import com.tarasovvp.smartblocker.constants.Constants.COUNTRY_CODE_START
import com.tarasovvp.smartblocker.constants.Constants.DESC
import com.tarasovvp.smartblocker.constants.Constants.LOG_CALL_CALL
import com.tarasovvp.smartblocker.constants.Constants.PLUS_CHAR
import com.tarasovvp.smartblocker.constants.Constants.REJECTED_CALL
import com.tarasovvp.smartblocker.model.*
import com.tarasovvp.smartblocker.repository.BlockedCallRepository
import com.tarasovvp.smartblocker.repository.FilterRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.max

fun Context.systemContactList(result: (Int, Int) -> Unit): ArrayList<Contact> {
    val projection = arrayOf(
        ContactsContract.Data.CONTACT_ID,
        ContactsContract.Contacts.DISPLAY_NAME,
        ContactsContract.Contacts.PHOTO_URI,
        ContactsContract.CommonDataKinds.Contactables.DATA

    )
    val selection =
        "${ContactsContract.Data.MIMETYPE} = '${ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE}'"
    val cursor: Cursor? = this
        .contentResolver
        .query(ContactsContract.Data.CONTENT_URI,
            projection,
            selection,
            null,
            "${ContactsContract.Contacts.DISPLAY_NAME} $ASC")
    val contactList = arrayListOf<Contact>()
    cursor?.use { contactCursor ->
        while (contactCursor.moveToNext()) {
            contactList.add(Contact(
                id = contactCursor.getString(0),
                name = contactCursor.getString(1),
                photoUrl = contactCursor.getString(2),
                number = contactCursor.getString(3),
            ).apply {
                numberData = contactCursor.getString(3).digitsTrimmed()
                CoroutineScope(Dispatchers.IO).launch {
                    filter = FilterRepository.queryFilter(numberData)
                }
            })
            result.invoke(cursor.count, contactList.size)
        }
    }
    return contactList
}

fun Context.systemCallLogCursor(): Cursor? {
    val projection = arrayListOf(
        CallLog.Calls._ID,
        CallLog.Calls.CACHED_NAME,
        CallLog.Calls.NUMBER,
        CallLog.Calls.TYPE,
        CallLog.Calls.DATE,
        CallLog.Calls.CACHED_NORMALIZED_NUMBER,
        CallLog.Calls.COUNTRY_ISO,
        CallLog.Calls.NUMBER_PRESENTATION)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        projection.add(CallLog.Calls.CACHED_PHOTO_URI)
    }
    return this.contentResolver.query(
        Uri.parse(LOG_CALL_CALL),
        projection.toTypedArray(),
        null,
        null,
        "${CallLog.Calls.DATE} $DESC"
    )
}

fun Cursor.createCallObject(isBlockedCall: Boolean): Call {
    val logCall = if (isBlockedCall) BlockedCall() else LogCall()
    logCall.callId = this.getString(0)
    logCall.name = this.getString(1)
    logCall.number = this.getString(2)
    logCall.type = this.getString(3)
    logCall.callDate = this.getString(4)
    logCall.normalizedNumber = this.getString(5)
    logCall.countryIso = this.getString(6)
    logCall.numberPresentation = this.getString(7)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        logCall.photoUrl = this.getString(7)
    }
    logCall.numberData = this.getString(2)
    CoroutineScope(Dispatchers.IO).launch {
        logCall.filter = FilterRepository.queryFilter(logCall.numberData)
    }
    return logCall
}

fun Context.systemLogCallList(result: (Int, Int) -> Unit): ArrayList<LogCall> {
    val logCallList = ArrayList<LogCall>()
    systemCallLogCursor()?.use { callLogCursor ->
        while (callLogCursor.moveToNext()) {
            logCallList.add((callLogCursor.createCallObject(false) as LogCall))
            result.invoke(callLogCursor.count, logCallList.size)
        }
    }
    return logCallList
}

fun Context.deleteLastBlockedCall(number: String, filter: Filter?) {
    systemCallLogCursor()?.use { cursor ->
        while (cursor.moveToNext()) {
            val blockedCall = cursor.createCallObject(true) as BlockedCall
            Log.e("blockTAG",
                "Extensions deleteLastMissedCall id ${blockedCall.id} number ${blockedCall.number} type ${blockedCall.type} time ${blockedCall.callDate} currentTimeMillis ${System.currentTimeMillis()}")
            if (number == blockedCall.number && REJECTED_CALL == blockedCall.type) {
                try {
                    Log.e("blockTAG",
                        "Extensions deleteLastMissedCall phone == number && type == REJECTED_CALL number $number name ${blockedCall.name} time ${blockedCall.callDate} phone ${blockedCall.number} type ${blockedCall.type} id ${blockedCall.callId}")
                    val result = this.contentResolver.delete(Uri.parse(LOG_CALL_CALL),
                        "${CALL_ID}'${blockedCall.callId}'",
                        null)
                    CoroutineScope(Dispatchers.IO).launch {
                        BlockedCallRepository.insertBlockedCall(blockedCall.apply {
                            type = BLOCKED_CALL
                            this.filter = filter
                            name =
                                if (blockedCall.name.isNullOrEmpty()) getString(R.string.number_not_from_contacts) else name
                        })
                    }
                    Log.e("blockTAG",
                        "Extensions delete callId ${blockedCall.callId} result $result")
                    break
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    CoroutineScope(Dispatchers.IO).launch {
                        BlockedCallRepository.insertBlockedCall(blockedCall)
                    }
                    Log.e("blockTAG", "Extensions delete Exception ${e.localizedMessage}")
                }
            }
        }
    }
}

fun Context.getInitialBitmap(text: String): Bitmap? {
    Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.LTGRAY
        textSize = spToPx(18F)
        typeface = Typeface.DEFAULT
        textAlign = Paint.Align.LEFT
        val textBound = Rect()
        getTextBounds(text, 0, text.length, textBound)
        val textHeight = textBound.height()
        val textWidth = textBound.width()
        val rectSize = max(textHeight + dpToPx(16f) * 2, textWidth + dpToPx(16f) * 2)

        val initialBitmap = Bitmap.createBitmap(
            rectSize.toInt(), rectSize.toInt(),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(initialBitmap)
        val rectF = RectF(0f, 0f, rectSize, rectSize)
        canvas.drawRect(rectF, this)

        color = Color.WHITE
        style = Paint.Style.FILL
        canvas.drawText(
            text, (rectSize - textWidth) / 2, (rectSize - textHeight) / 2 + textHeight, this
        )
        return initialBitmap
    }
}

fun Context.getUserCountry(): String? {
    try {
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val simCountry = tm.simCountryIso
        when {
            simCountry != null && simCountry.length == 2 -> {
                return simCountry.lowercase(Locale.US)
            }
            tm.phoneType != TelephonyManager.PHONE_TYPE_CDMA -> {
                val networkCountry = tm.networkCountryIso
                if (networkCountry.isNotNull() && networkCountry.length == 2) {
                    return networkCountry.lowercase(Locale.US)
                }
            }
        }
    } catch (e: java.lang.Exception) {
        return null
    }
    return null
}

fun PhoneNumberUtil.countryCodeList(result: (Int, Int) -> Unit): ArrayList<CountryCode> {
    val countryCodeMap = arrayListOf<CountryCode>()
    supportedRegions.sorted().forEachIndexed { index, region ->
        val countryCode =
            String.format(COUNTRY_CODE_START, getCountryCodeForRegion(region).toString())
        val numberFormat = try {
            format(getExampleNumberForType(region, PhoneNumberUtil.PhoneNumberType.MOBILE),
                PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL).replace("$countryCode ",
                String.EMPTY)
        } catch (e: Exception) {
            String.EMPTY
        }
        countryCodeMap.add(CountryCode(region, countryCode, region.flagEmoji(), numberFormat))
        result.invoke(supportedRegions.size, index)
    }
    return countryCodeMap
}

fun String?.getPhoneNumber(country: String): Phonenumber.PhoneNumber? = try {
    Log.e("searchTAG", "ModelExtensions getPhoneNumber this $this country $country")
    if (this.isNullOrEmpty()) null
    else if (this.startsWith(PLUS_CHAR)) PhoneNumberUtil.getInstance().parse(this.digitsTrimmed(), String.EMPTY)
    else PhoneNumberUtil.getInstance().parse(this.digitsTrimmed(), country)
} catch (e: Exception) {
    e.printStackTrace()
    null
}

fun String?.isValidPhoneNumber(country: String): Boolean {
    return try {
        if (getPhoneNumber(country).isNull()) false else PhoneNumberUtil.getInstance()
            .isValidNumberForRegion(getPhoneNumber(country), country)
    } catch (e: Exception) {
        return false
    }
}

fun ArrayList<NumberData>.filteredNumberDataList(filter: Filter?): ArrayList<NumberData> {
    val filteredList = arrayListOf<NumberData>()
    val supposedFilteredList = arrayListOf<NumberData>()
    forEach { numberData ->
        if (filter?.isTypeContain().isTrue() && numberData.numberData.digitsTrimmed()
                .contains(filter?.filter.orEmpty()).isTrue()
        ) {
            filteredList.add(numberData.apply {
                searchText = filter?.filter.orEmpty()
                highlightedSpanned = numberData.numberData.highlightedSpanned(filter?.filter, null)
            })
        } else {
            val phoneNumber = numberData.numberData.digitsTrimmed()
                .getPhoneNumber(filter?.countryCode?.country.orEmpty())
            if (numberData.numberData.digitsTrimmed().startsWith(filter?.addFilter().orEmpty()).isTrue())
                filteredList.add(numberData.apply {
                    searchText = filter?.addFilter().orEmpty()
                    highlightedSpanned = numberData.numberData.highlightedSpanned(filter?.addFilter(), null)
                }) else if ((phoneNumber?.nationalNumber.toString()
                    .startsWith(filter?.filterWithoutCountryCode().orEmpty())
                    .isTrue() && String.format(COUNTRY_CODE_START,
                    phoneNumber?.countryCode) == filter?.countryCode?.countryCode)
            )
                supposedFilteredList.add(numberData.apply {
                    searchText = if (filter.filter == filter.addFilter()) filter.filterWithoutCountryCode() else filter.filter
                    highlightedSpanned = numberData.numberData.highlightedSpanned(if (filter.filter == filter.addFilter()) filter.filterWithoutCountryCode() else filter.filter, filter.countryCode.countryCode)
                })
        }
    }
    filteredList.addAll(supposedFilteredList)
    return filteredList
}