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
import com.tarasovvp.smartblocker.domain.enums.*
import com.tarasovvp.smartblocker.domain.entities.db_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.entities.db_entities.*
import com.tarasovvp.smartblocker.domain.entities.models.Call
import com.tarasovvp.smartblocker.domain.entities.models.CallWithFilter
import com.tarasovvp.smartblocker.domain.entities.models.NumberData
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.APP_LANG_RU
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.APP_LANG_UK
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.ASC
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKED_CALL
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.CALL_ID
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.COUNTRY_CODE_START
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.DESC
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.LOG_CALL_CALL
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PLUS_CHAR
import java.util.*
import kotlin.collections.ArrayList
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
            contactList.add(
                Contact(
                    id = contactCursor.getString(0),
                    name = contactCursor.getString(1),
                    photoUrl = contactCursor.getString(2),
                    number = contactCursor.getString(3),
                )
            )
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
        CallLog.Calls.COUNTRY_ISO
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
    logCall.normalizedNumber = this.getString(5)
    logCall.countryIso = this.getString(6)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        logCall.photoUrl = this.getString(7)
    }
    return logCall
}

fun Context.systemLogCallList(result: (Int, Int) -> Unit): List<LogCall> {
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

fun Context.createFilteredCall(
    number: String,
    filter: Filter,
): FilteredCall? {
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
                        this.contentResolver.delete(
                            Uri.parse(LOG_CALL_CALL),
                            "${CALL_ID}'${filteredCall.callId}'",
                            null
                        )
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

fun PhoneNumberUtil.countryCodeList(result: (Int, Int) -> Unit): ArrayList<CountryCode> {
    val countryCodeMap = arrayListOf<CountryCode>()
    supportedRegions.sorted().forEachIndexed { index, region ->
        val countryCode =
            String.format(COUNTRY_CODE_START, getCountryCodeForRegion(region).toString())
        val numberFormat = try {
            format(getExampleNumberForType(region, PhoneNumberUtil.PhoneNumberType.MOBILE),
                PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL).replace("$countryCode ", String.EMPTY)
        } catch (e: Exception) {
            String.EMPTY
        }
        countryCodeMap.add(CountryCode(region, countryCode, numberFormat))
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

fun Context.numberDataFilteringText(filterIndexes: ArrayList<Int>): String {
    return if (filterIndexes.isEmpty()) getString(R.string.filter_no_filter) else filterIndexes.joinToString { index ->
        NumberDataFiltering.values().find { it.ordinal == index }?.title()?.let { getString(it) } ?: String.EMPTY
    }
}

fun NumberData.highlightedSpanned(filter: Filter?, color: Int): SpannableString? {
    when (this) {
        is CallWithFilter -> {
            return when {
                filter?.filter.isNullOrEmpty() -> call?.number.highlightedSpanned(
                    String.EMPTY,
                    null,
                    color
                )
                else -> call?.number.highlightedSpanned(filter?.filter, null, Color.RED)
            }
        }
        is ContactWithFilter -> {
            return when {
                filter?.filter.isNullOrEmpty() -> contact?.number.highlightedSpanned(
                    String.EMPTY,
                    null,
                    color
                )
                filter?.isTypeContain().isNotTrue() && contact?.phoneNumber()
                    .isValidPhoneNumber() && contact?.number?.startsWith(PLUS_CHAR)
                    .isNotTrue() -> contact?.number.highlightedSpanned(
                    filter?.filter,
                    filter?.countryCode,
                    color
                )
                filter?.isTypeContain().isNotTrue() && contact?.phoneNumber()
                    .isValidPhoneNumber() && contact?.number?.startsWith(PLUS_CHAR)
                    .isTrue() -> contact?.number.highlightedSpanned(filter?.filter, null, color)
                else -> contact?.number.highlightedSpanned(filter?.filter, null, Color.RED)
            }
        }
        is FilterWithCountryCode -> {
            return filter?.filter.highlightedSpanned(String.EMPTY, null, color)
        }
        else -> return null
    }
}

fun EmptyState.descriptionRes(): Int {
    return when(this) {
        EmptyState.EMPTY_STATE_BLOCKERS -> R.string.empty_state_blockers
        EmptyState.EMPTY_STATE_PERMISSIONS -> R.string.empty_state_permissions
        EmptyState.EMPTY_STATE_CONTACTS -> R.string.empty_state_contacts
        EmptyState.EMPTY_STATE_CALLS -> R.string.empty_state_calls
        EmptyState.EMPTY_STATE_QUERY -> R.string.empty_state_query
        EmptyState.EMPTY_STATE_CREATE_FILTER -> R.string.empty_state_add_filter
        EmptyState.EMPTY_STATE_FILTERS -> R.string.empty_state_filters
        EmptyState.EMPTY_STATE_NUMBERS -> R.string.empty_state_numbers
        EmptyState.EMPTY_STATE_FILTERED_CALLS -> R.string.empty_state_filtered_calls
        EmptyState.EMPTY_STATE_HIDDEN -> R.string.empty_state_hidden
        EmptyState.EMPTY_STATE_ACCOUNT -> R.string.empty_state_account
    }
}

fun Info.titleRes(): Int {
    return when(this) {
        Info.INFO_BLOCKER_LIST -> R.string.list_blocker
        Info.INFO_PERMISSION_LIST -> R.string.list_permission
        Info.INFO_CALL_LIST -> R.string.list_call
        Info.INFO_CONTACT_LIST -> R.string.list_contact
        Info.INFO_CREATE_FILTER_FULL -> R.string.filter_condition_full
        Info.INFO_CREATE_FILTER_START -> R.string.filter_condition_start
        Info.INFO_CREATE_FILTER_CONTAIN -> R.string.filter_condition_contain
        Info.INFO_DETAILS_NUMBER_DATA -> R.string.details_number
        Info.INFO_DETAILS_FILTER -> R.string.details_filter
    }
}

fun Info.descriptionRes(): Int {
    return when(this) {
        Info.INFO_BLOCKER_LIST -> R.string.info_blocker_list
        Info.INFO_PERMISSION_LIST -> R.string.info_permission_list
        Info.INFO_CALL_LIST -> R.string.info_call_list
        Info.INFO_CONTACT_LIST -> R.string.info_contact_list
        Info.INFO_CREATE_FILTER_FULL -> R.string.info_condition_full
        Info.INFO_CREATE_FILTER_START -> R.string.info_condition_start
        Info.INFO_CREATE_FILTER_CONTAIN -> R.string.info_condition_contain
        Info.INFO_DETAILS_NUMBER_DATA -> R.string.info_number_details
        Info.INFO_DETAILS_FILTER -> R.string.info_filter_details
    }
}

fun OnBoarding.descriptionRes(): Int {
    return when(this) {
        OnBoarding.ONBOARDING_INTRO -> R.string.onboarding_intro
        OnBoarding.ONBOARDING_FILTER_CONDITIONS -> R.string.onboarding_filter_conditions
        OnBoarding.ONBOARDING_INFO -> R.string.onboarding_info
        OnBoarding.ONBOARDING_PERMISSIONS -> R.string.onboarding_permissions
    }
}

fun OnBoarding.mainImageRes(): Int {
    return when(this) {
        OnBoarding.ONBOARDING_INTRO -> R.drawable.ic_onboarding_intro
        OnBoarding.ONBOARDING_FILTER_CONDITIONS -> R.drawable.ic_onboarding_filter_conditions
        OnBoarding.ONBOARDING_INFO -> R.drawable.ic_onboarding_info
        OnBoarding.ONBOARDING_PERMISSIONS -> R.drawable.ic_onboarding_permissions
    }
}

fun OnBoarding.tabImageRes(): Int {
    return when(this) {
        OnBoarding.ONBOARDING_INTRO -> R.drawable.ic_tab_first
        OnBoarding.ONBOARDING_FILTER_CONDITIONS -> R.drawable.ic_tab_second
        OnBoarding.ONBOARDING_INFO -> R.drawable.ic_tab_third
        OnBoarding.ONBOARDING_PERMISSIONS -> R.drawable.ic_tab_last
    }
}

fun NumberDataFiltering.titleRes(): Int {
    return when(this) {
        NumberDataFiltering.FILTER_CONDITION_FULL_FILTERING -> R.string.filter_condition_full
        NumberDataFiltering.FILTER_CONDITION_START_FILTERING -> R.string.filter_condition_start
        NumberDataFiltering.FILTER_CONDITION_CONTAIN_FILTERING -> R.string.filter_condition_contain
        NumberDataFiltering.CALL_BLOCKED -> R.string.filter_call_blocked
        NumberDataFiltering.CALL_PERMITTED -> R.string.filter_call_permitted
        NumberDataFiltering.CONTACT_WITH_BLOCKER -> R.string.filter_contact_blocker
        NumberDataFiltering.CONTACT_WITH_PERMISSION -> R.string.filter_contact_permission
    }
}

fun FilterCondition.titleRes(): Int {
    return when(this) {
        FilterCondition.FILTER_CONDITION_FULL -> R.string.filter_condition_full
        FilterCondition.FILTER_CONDITION_START -> R.string.filter_condition_start
        FilterCondition.FILTER_CONDITION_CONTAIN -> R.string.filter_condition_contain
    }
}

fun FilterCondition.mainIconRes(): Int {
    return when(this) {
        FilterCondition.FILTER_CONDITION_FULL -> R.drawable.ic_condition_full
        FilterCondition.FILTER_CONDITION_START -> R.drawable.ic_condition_start
        FilterCondition.FILTER_CONDITION_CONTAIN -> R.drawable.ic_condition_contain
    }
}

fun FilterCondition.smallIconRes(isBlocker: Boolean): Int {
    return when(this) {
        FilterCondition.FILTER_CONDITION_FULL -> if (isBlocker) R.drawable.ic_condition_full_blocker_small else  R.drawable.ic_condition_full_permission_small
        FilterCondition.FILTER_CONDITION_START -> if (isBlocker) R.drawable.ic_condition_start_blocker_small else  R.drawable.ic_condition_start_permission_small
        FilterCondition.FILTER_CONDITION_CONTAIN -> if (isBlocker) R.drawable.ic_condition_contain_blocker_small else  R.drawable.ic_condition_contain_permission_small
    }
}

fun FilterAction.titleRes(): Int {
    return when(this) {
        FilterAction.FILTER_ACTION_INVALID,
        FilterAction.FILTER_ACTION_BLOCKER_CREATE,
        FilterAction.FILTER_ACTION_PERMISSION_CREATE -> R.string.filter_action_create
        FilterAction.FILTER_ACTION_BLOCKER_DELETE,
        FilterAction.FILTER_ACTION_PERMISSION_DELETE -> R.string.filter_action_delete
        FilterAction.FILTER_ACTION_PERMISSION_TRANSFER,
        FilterAction.FILTER_ACTION_BLOCKER_TRANSFER -> R.string.filter_action_transfer
    }
}

fun FilterAction.colorRes(): Int {
    return when(this) {
        FilterAction.FILTER_ACTION_INVALID -> R.color.inactive_bg
        FilterAction.FILTER_ACTION_BLOCKER_CREATE,
        FilterAction.FILTER_ACTION_PERMISSION_CREATE,
        FilterAction.FILTER_ACTION_PERMISSION_TRANSFER,
        FilterAction.FILTER_ACTION_BLOCKER_TRANSFER -> R.color.button_bg
        FilterAction.FILTER_ACTION_BLOCKER_DELETE,
        FilterAction.FILTER_ACTION_PERMISSION_DELETE -> R.color.sunset
    }
}

fun FilterAction.iconRes(): Int {
    return when(this) {
        FilterAction.FILTER_ACTION_INVALID -> R.drawable.ic_blocker_inactive
        FilterAction.FILTER_ACTION_BLOCKER_CREATE,
        FilterAction.FILTER_ACTION_BLOCKER_TRANSFER -> R.drawable.ic_blocker
        FilterAction.FILTER_ACTION_PERMISSION_CREATE,
        FilterAction.FILTER_ACTION_PERMISSION_TRANSFER -> R.drawable.ic_permission
        FilterAction.FILTER_ACTION_BLOCKER_DELETE -> R.drawable.ic_delete
        FilterAction.FILTER_ACTION_PERMISSION_DELETE -> R.drawable.ic_delete
    }
}

fun FilterAction.descriptionRes(): Int {
    return when(this) {
        FilterAction.FILTER_ACTION_INVALID -> R.string.filter_action_create_number_invalid
        FilterAction.FILTER_ACTION_BLOCKER_CREATE -> R.string.filter_action_create_blocker_description
        FilterAction.FILTER_ACTION_PERMISSION_CREATE -> R.string.filter_action_create_permission_description
        FilterAction.FILTER_ACTION_BLOCKER_DELETE -> R.string.filter_action_delete_blocker_description
        FilterAction.FILTER_ACTION_PERMISSION_DELETE -> R.string.filter_action_delete_permission_description
        FilterAction.FILTER_ACTION_PERMISSION_TRANSFER -> R.string.filter_action_transfer_blocker_description
        FilterAction.FILTER_ACTION_BLOCKER_TRANSFER -> R.string.filter_action_transfer_permission_description
    }
}

fun FilterAction.requestRes(): Int {
    return when(this) {
        FilterAction.FILTER_ACTION_INVALID -> 0
        FilterAction.FILTER_ACTION_BLOCKER_CREATE -> R.string.filter_action_create_blocker_request
        FilterAction.FILTER_ACTION_PERMISSION_CREATE -> R.string.filter_action_create_permission_request
        FilterAction.FILTER_ACTION_BLOCKER_DELETE -> R.string.filter_action_delete_blocker_request
        FilterAction.FILTER_ACTION_PERMISSION_DELETE -> R.string.filter_action_delete_permission_request
        FilterAction.FILTER_ACTION_PERMISSION_TRANSFER -> R.string.filter_action_transfer_blocker_request
        FilterAction.FILTER_ACTION_BLOCKER_TRANSFER -> R.string.filter_action_transfer_permission_request
    }
}

fun FilterAction.successRes(): Int {
    return when(this) {
        FilterAction.FILTER_ACTION_INVALID -> 0
        FilterAction.FILTER_ACTION_BLOCKER_CREATE -> R.string.filter_action_create_blocker_success
        FilterAction.FILTER_ACTION_PERMISSION_CREATE -> R.string.filter_action_create_permission_success
        FilterAction.FILTER_ACTION_BLOCKER_DELETE -> R.string.filter_action_delete_blocker_success
        FilterAction.FILTER_ACTION_PERMISSION_DELETE -> R.string.filter_action_delete_permission_success
        FilterAction.FILTER_ACTION_PERMISSION_TRANSFER -> R.string.filter_action_transfer_blocker_success
        FilterAction.FILTER_ACTION_BLOCKER_TRANSFER -> R.string.filter_action_transfer_permission_success
    }
}