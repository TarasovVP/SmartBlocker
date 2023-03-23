package com.tarasovvp.smartblocker.utils.extensions

import android.content.Context
import android.database.Cursor
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.CallLog
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import android.text.SpannableString
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.perf.metrics.AddTrace
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.data.database.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.data.database.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.data.database.entities.*
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.ASC
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKED_CALL
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.CALL_ID
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.COUNTRY_CODE_START
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.DESC
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.LOG_CALL_CALL
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PLUS_CHAR
import com.tarasovvp.smartblocker.domain.models.Call
import com.tarasovvp.smartblocker.domain.models.NumberData
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
            contactList.add(
                Contact(
                id = contactCursor.getString(0),
                name = contactCursor.getString(1),
                photoUrl = contactCursor.getString(2),
                number = contactCursor.getString(3),
            ))
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
        CallLog.Calls.COUNTRY_ISO)
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

fun Cursor.createCallObject(isFilteredCall: Boolean): Call {
    val logCall = if (isFilteredCall) FilteredCall() else LogCall()
    logCall.callId = this.getInt(0)
    logCall.callName = this.getString(1)
    logCall.number = this.getString(2)
    logCall.type = this.getString(3)
    logCall.callDate = this.getString(4)
    logCall.normalizedNumber = this.getString(5)
    logCall.countryIso = this.getString(6)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        logCall.photoUrl = this.getString(7)
    }
    return logCall
}

fun Context.systemLogCallList(result: (Int, Int) -> Unit): ArrayList<LogCall> {
    val logCallList = ArrayList<LogCall>()
    systemCallLogCursor()?.use { callLogCursor ->
        while (callLogCursor.moveToNext()) {
            val logCall = callLogCursor.createCallObject(false) as LogCall
            logCallList.add(logCall)
            result.invoke(callLogCursor.count, logCallList.size)
        }
    }
    return logCallList
}

suspend fun Context.createFilteredCall(
    number: String,
    filter: Filter
) : FilteredCall? {
    systemCallLogCursor()?.use { cursor ->
        while (cursor.moveToNext()) {
            val filteredCall = cursor.createCallObject(true) as? FilteredCall
            if (number == filteredCall?.number) {
                    filteredCall.apply {
                        if (filter.isBlocker()) {
                            type = BLOCKED_CALL
                        }
                        this.filteredNumber = filter.filter
                        this.conditionType = filter.conditionType
                        this.isFilteredCall = true
                    }
                if (filter.isBlocker()) {
                    try {
                        this.contentResolver.delete(Uri.parse(LOG_CALL_CALL),
                            "${CALL_ID}'${filteredCall.callId}'",
                            null)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
                return filteredCall
            }
        }
    }
    return null
}

@AddTrace(name = "getInitialDrawable")
fun Context.getInitialDrawable(text: String): Drawable {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.TRANSPARENT
        textSize = spToPx(18F)
        isFakeBoldText = true
        typeface = ResourcesCompat.getFont(this@getInitialDrawable, R.font.comfortaa_regular)
        textAlign = Paint.Align.LEFT
    }
    val textBound = Rect()
    paint.getTextBounds(text, 0, text.length, textBound)
    val textHeight = textBound.height()
    val textWidth = textBound.width()
    val rectSize = max(textHeight + dpToPx(16F) * 2, textWidth + dpToPx(16F) * 2)
    val initialBitmap = Bitmap.createBitmap(
        rectSize.toInt(), rectSize.toInt(),
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(initialBitmap)
    paint.color = ContextCompat.getColor(this, R.color.avatar_icon_bg)
    paint.style = Paint.Style.FILL
    canvas.drawText(text,
        (rectSize - textWidth) / 2,
        (rectSize - textHeight) / 2 + textHeight,
        paint)
    return BitmapDrawable(resources, initialBitmap)
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
    if (this.isNullOrEmpty()) null
    else if (this.startsWith(PLUS_CHAR)) PhoneNumberUtil.getInstance()
        .parse(this.digitsTrimmed(), String.EMPTY)
    else PhoneNumberUtil.getInstance().parse(this.digitsTrimmed(), country)
} catch (e: Exception) {
    e.printStackTrace()
    null
}

@AddTrace(name = "isValidPhoneNumber")
fun String?.isValidPhoneNumber(country: String): Boolean {
    return try {
        if (getPhoneNumber(country).isNull()) false else PhoneNumberUtil.getInstance()
            .isValidNumberForRegion(getPhoneNumber(country), country)
    } catch (e: Exception) {
        return false
    }
}

fun Phonenumber.PhoneNumber?.isValidPhoneNumber(): Boolean {
    return try {
        if (this.isNull()) false else PhoneNumberUtil.getInstance()
            .isValidNumber(this)
    } catch (e: Exception) {
        return false
    }
}

@AddTrace(name = "filteredNumberDataList")
fun ArrayList<NumberData>.filteredNumberDataList(filter: Filter?, color: Int): ArrayList<NumberData> {
    val filteredList = arrayListOf<NumberData>()
    val supposedFilteredList = arrayListOf<NumberData>()
    forEach { numberData ->
        numberData.highlightedSpanned = numberData.highlightedSpanned(filter,color)
        if (numberData is ContactWithFilter && numberData.contact?.number?.startsWith(PLUS_CHAR).isTrue().not() ) {
            supposedFilteredList.add(numberData)
        } else {
            filteredList.add(numberData)
        }
    }
    filteredList.addAll(supposedFilteredList)
    return filteredList
}

fun NumberData.highlightedSpanned(filter: Filter?, color: Int): SpannableString? {
    when (this) {
        is CallWithFilter -> {
            return when {
                filter?.filter.isNullOrEmpty() -> call?.number.highlightedSpanned(String.EMPTY, null, color)
                else -> call?.number.highlightedSpanned(filter?.filter, null, Color.RED)
            }
        }
        is ContactWithFilter -> {
            return when {
                filter?.filter.isNullOrEmpty() -> contact?.number.highlightedSpanned(String.EMPTY, null, color)
                filter?.isTypeContain().isNotTrue() && contact?.phoneNumber().isValidPhoneNumber() && contact?.number?.startsWith(PLUS_CHAR).isNotTrue() -> contact?.number.highlightedSpanned(filter?.filter, filter?.countryCode, color)
                filter?.isTypeContain().isNotTrue() && contact?.phoneNumber().isValidPhoneNumber() && contact?.number?.startsWith(PLUS_CHAR).isTrue() -> contact?.number.highlightedSpanned(filter?.filter, null, color)
                else -> contact?.number.highlightedSpanned(filter?.filter, null, Color.RED)
            }
        }
        is FilterWithCountryCode -> {
            return filter?.filter.highlightedSpanned(String.EMPTY, null, color)
        }
        else -> return null
    }
}