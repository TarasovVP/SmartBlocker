package com.tarasovvp.smartblocker.utils.extensions

import android.content.Context
import android.database.Cursor
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.provider.CallLog
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import android.text.SpannableString
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseUser
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.entities.db_entities.Contact
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.entities.db_entities.FilteredCall
import com.tarasovvp.smartblocker.domain.entities.db_entities.LogCall
import com.tarasovvp.smartblocker.domain.entities.models.Call
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.APP_LANG_RU
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.APP_LANG_UK
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.ASC
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKED_CALL
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.DESC
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PLUS_CHAR
import com.tarasovvp.smartblocker.presentation.ui_models.CallWithFilterUIModel
import com.tarasovvp.smartblocker.presentation.ui_models.ContactWithFilterUIModel
import com.tarasovvp.smartblocker.presentation.ui_models.FilterWithFilteredNumberUIModel
import com.tarasovvp.smartblocker.presentation.ui_models.NumberDataUIModel
import com.tarasovvp.smartblocker.utils.AppPhoneNumberUtil
import java.util.*
import kotlin.math.max

fun Context.systemContactList(appPhoneNumberUtil: AppPhoneNumberUtil, country: String, result: (Int, Int) -> Unit): ArrayList<Contact> {
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
        .query(
            ContactsContract.Data.CONTENT_URI,
            projection,
            selection,
            null,
            "${ContactsContract.Contacts.DISPLAY_NAME} $ASC"
        )
    val contactList = arrayListOf<Contact>()
    cursor?.use { contactCursor ->
        while (contactCursor.moveToNext()) {
            val contact = Contact(
                contactId = contactCursor.getString(0),
                name = contactCursor.getString(1),
                photoUrl = contactCursor.getString(2),
                number = contactCursor.getString(3),
            ).apply {
                digitsTrimmedNumber = number.digitsTrimmed()
                val phoneNumber = appPhoneNumberUtil.getPhoneNumber(number, country)
                isPhoneNumberValid = appPhoneNumberUtil.isPhoneNumberValid(phoneNumber)
                phoneNumberValue = appPhoneNumberUtil.phoneNumberValue(number, phoneNumber) }
            contactList.add(contact)
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
        CallLog.Calls.DATE
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        projection.add(CallLog.Calls.CACHED_PHOTO_URI)
    }

    return this.contentResolver.query(
        CallLog.Calls.CONTENT_URI,
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
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        logCall.photoUrl = this.getString(5)
    }
    return logCall
}

fun Context.systemLogCallList(appPhoneNumberUtil: AppPhoneNumberUtil, country: String, result: (Int, Int) -> Unit): List<LogCall> {
    val logCallList = ArrayList<LogCall>()
    systemCallLogCursor()?.use { callLogCursor ->
        while (callLogCursor.moveToNext()) {
            val logCall = callLogCursor.createCallObject(false) as LogCall
            logCallList.add(logCall.apply {
                val phoneNumber = appPhoneNumberUtil.getPhoneNumber(number, country)
                isPhoneNumberValid = appPhoneNumberUtil.isPhoneNumberValid(phoneNumber)
                phoneNumberValue = appPhoneNumberUtil.phoneNumberValue(number, phoneNumber) })
            result.invoke(callLogCursor.count, logCallList.size)
        }
    }
    return logCallList
}

fun Context.createFilteredCall(
    number: String,
    filter: Filter,
): FilteredCall? {
    systemCallLogCursor()?.use { cursor ->
        while (cursor.moveToNext()) {
            val filteredCall = cursor.createCallObject(true) as? FilteredCall
            if (number == filteredCall?.number) {
                filteredCall.apply {
                    if (filter.filterType == BLOCKER) {
                        type = BLOCKED_CALL
                    }
                    this.filteredNumber = filter.filter
                    this.filteredConditionType = filter.conditionType
                    this.isFilteredCall = true
                    this.phoneNumberValue = number
                    this.isPhoneNumberValid = true
                }
                return filteredCall
            }
        }
    }
    return null
}

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
    canvas.drawText(
        text,
        (rectSize - textWidth) / 2,
        (rectSize - textHeight) / 2 + textHeight,
        paint
    )
    return BitmapDrawable(resources, initialBitmap)
}

fun String.flagDrawable(): Int {
    return when (this) {
        APP_LANG_UK -> R.drawable.ic_flag_ua
        APP_LANG_RU -> R.drawable.ic_flag_ru
        else -> R.drawable.ic_flag_en
    }
}

fun Context.getUserCountry(): String? {
    try {
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val simCountry = tm.simCountryIso
        when {
            simCountry.isNotNull() && simCountry.length == 2 -> {
                return simCountry.lowercase(Locale.US)
            }
            tm.phoneType notEquals TelephonyManager.PHONE_TYPE_CDMA -> {
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

fun Context.numberDataFilteringText(filterIndexes: ArrayList<Int>): String {
    return if (filterIndexes.isEmpty()) getString(R.string.filter_no_filter) else filterIndexes.joinToString { index ->
        NumberDataFiltering.values().find { it.ordinal == index }?.title()?.let { getString(it) } ?: String.EMPTY
    }
}

fun NumberDataUIModel.highlightedSpanned(filterWithFilteredNumberUIModel: FilterWithFilteredNumberUIModel?, color: Int): SpannableString? {
    return when (this) {
        is CallWithFilterUIModel -> number.highlightedSpanned(filterWithFilteredNumberUIModel?.filter, null, color)
        is FilterWithFilteredNumberUIModel -> filterWithFilteredNumberUIModel?.filter.highlightedSpanned(String.EMPTY, null, color)
        is ContactWithFilterUIModel -> {
            when {
                filterWithFilteredNumberUIModel?.conditionType != FilterCondition.FILTER_CONDITION_CONTAIN.ordinal && isPhoneNumberValid.isTrue() && number.startsWith(PLUS_CHAR).isNotTrue() ->
                    number.highlightedSpanned(filterWithFilteredNumberUIModel?.countryCode?.takeIf { filterWithFilteredNumberUIModel.filter.length > filterWithFilteredNumberUIModel.countryCode.length }?.let { filterWithFilteredNumberUIModel.filter.substring(it.length) }, filterWithFilteredNumberUIModel?.countryCode, color)
                else -> number.highlightedSpanned(filterWithFilteredNumberUIModel?.filter, null, color)
            }
        }
        else ->  null
    }
}

fun FirebaseUser.currentUserEmail(): String {
    return try {
       email ?: String.EMPTY
    } catch (e: AbstractMethodError) {
        String.EMPTY
    }
}