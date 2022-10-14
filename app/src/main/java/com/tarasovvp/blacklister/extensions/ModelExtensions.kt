package com.tarasovvp.blacklister.extensions

import android.app.AlertDialog
import android.content.Context
import android.database.Cursor
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.provider.CallLog
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import android.util.Log
import androidx.appcompat.view.ContextThemeWrapper
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.BLOCKED_CALL
import com.tarasovvp.blacklister.constants.Constants.CALL_ID
import com.tarasovvp.blacklister.constants.Constants.LOG_CALL_CALL
import com.tarasovvp.blacklister.constants.Constants.PLUS_CHAR
import com.tarasovvp.blacklister.constants.Constants.REJECTED_CALL
import com.tarasovvp.blacklister.enums.Condition
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.model.*
import com.tarasovvp.blacklister.repository.BlockedCallRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.max


fun Context.systemContactList(): ArrayList<Contact> {
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
        .query(ContactsContract.Data.CONTENT_URI, projection, selection, null, null)
    val contactList = arrayListOf<Contact>()
    cursor?.use { contactCursor ->
        while (contactCursor.moveToNext()) {
            contactList.add(Contact(
                id = contactCursor.getString(0),
                name = contactCursor.getString(1),
                photoUrl = contactCursor.getString(2),
                phone = contactCursor.getString(3)
            ))
        }
    }
    contactList.sortBy { it.name }
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
        null
    )
}

fun Cursor.createCallObject(isBlockedCall: Boolean): Call {
    val logCall = if (isBlockedCall) BlockedCall() else LogCall()
    logCall.callId = this.getString(0)
    logCall.name = this.getString(1)
    logCall.number = this.getString(2)
    logCall.type = this.getString(3)
    logCall.time = this.getString(4)
    logCall.normalizedNumber = this.getString(5)
    logCall.countryIso = this.getString(6)
    logCall.numberPresentation = this.getString(7)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        logCall.photoUrl = this.getString(7)
    }
    return logCall
}

fun Context.systemLogCallList(): ArrayList<LogCall> {
    val logCallList = ArrayList<LogCall>()
    systemCallLogCursor()?.use { callLogCursor ->
        while (callLogCursor.moveToNext()) {
            logCallList.add(callLogCursor.createCallObject(false) as LogCall)
        }
    }
    return logCallList
}

fun Context.deleteLastBlockedCall(number: String) {
    systemCallLogCursor()?.use { cursor ->
        while (cursor.moveToNext()) {
            val blockedCall = cursor.createCallObject(true) as BlockedCall
            if (number == blockedCall.number && REJECTED_CALL == blockedCall.type) {
                try {
                    Log.e("blockTAG",
                        "Extensions deleteLastMissedCall phone == number && type == REJECTED_CALL number $number name ${blockedCall.name} time ${blockedCall.time} phone ${blockedCall.number} type ${blockedCall.type} id ${blockedCall.callId}")
                    val result = this.contentResolver.delete(Uri.parse(LOG_CALL_CALL),
                        "${CALL_ID}'${blockedCall.callId}'",
                        null)
                    blockedCall.type = BLOCKED_CALL
                    CoroutineScope(Dispatchers.IO).launch {
                        BlockedCallRepository.insertBlockedCall(blockedCall)
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
        strokeWidth = dpToPx(2F)
        style = Paint.Style.STROKE
        canvas.drawCircle(rectSize / 2, rectSize / 2, rectSize / 2, this)

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

fun PhoneNumberUtil.countryCodeList(): ArrayList<CountryCode> {
    val countryCodeMap = arrayListOf<CountryCode>()
    supportedRegions.sorted().forEach { region ->
        countryCodeMap.add(CountryCode(region, getCountryCodeForRegion(region).toString(), region.flagEmoji()))
    }
    return countryCodeMap
}

fun String?.getPhoneNumber(country: String): Phonenumber.PhoneNumber? = try {
    if (this.digitsTrimmed().startsWith(PLUS_CHAR))
        PhoneNumberUtil.getInstance().parse(this.digitsTrimmed(), String.EMPTY)
    else PhoneNumberUtil.getInstance().parse(this.digitsTrimmed(), country)
} catch (e: Exception) {
    e.printStackTrace()
    null
}

fun String?.isValidPhoneNumber(country: String): Boolean {
    return if (getPhoneNumber(country).isNull()) false else PhoneNumberUtil.getInstance()
        .isValidNumberForRegion(getPhoneNumber(country), country)
}

fun Context.filterDataList(conditionList: ArrayList<Condition>, result: () -> Unit): Boolean {
    val builder =
        AlertDialog.Builder(ContextThemeWrapper(this, R.style.MultiChoiceAlertDialog))
    builder.setTitle(R.string.condition_dialog_title)
    builder.setMultiChoiceItems(conditionList.map { getString(it.title) }.toTypedArray(),
        conditionList.map { it.isSelected }.toBooleanArray()) { _, position, isChecked ->
        conditionList[position].isSelected = isChecked
    }
    builder.setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
    builder.setPositiveButton(R.string.ok) { _, _ ->
        result.invoke()
    }
    builder.show()
    return true
}

fun Context.contactListMap(
    contactList: List<Contact>,
    isBlackFilter: Boolean,
): LinkedHashMap<String, List<Contact>> {
    val title = String.format(getString(R.string.contact_by_filter_list_title), contactList.size)
    val contactListMap = linkedMapOf<String, List<Contact>>(title to listOf())
    val affectedContactList = contactList.filterNot {
        if (isBlackFilter) it.isWhiteFilter() && SharedPreferencesUtil.whiteListPriority else it.isBlackFilter() && SharedPreferencesUtil.whiteListPriority.not()
    }
    if (affectedContactList.isNotEmpty()) {
        val affectedContacts = String.format(getString(R.string.block_add_info),
            if (isBlackFilter) getString(R.string.can_block) else getString(R.string.can_unblock),
            affectedContactList.size)
        contactListMap[affectedContacts] = affectedContactList
    }

    val nonAffectedContactList = contactList.filter {
        if (isBlackFilter) it.isWhiteFilter() && SharedPreferencesUtil.whiteListPriority else it.isBlackFilter() && SharedPreferencesUtil.whiteListPriority.not()
    }
    if (nonAffectedContactList.isNotEmpty()) {
        val nonAffectedContacts = String.format(getString(R.string.not_block_add_info),
            if (isBlackFilter) getString(R.string.can_block) else getString(R.string.can_unblock),
            nonAffectedContactList.size)
        contactListMap[nonAffectedContacts] = nonAffectedContactList
    }
    return contactListMap
}