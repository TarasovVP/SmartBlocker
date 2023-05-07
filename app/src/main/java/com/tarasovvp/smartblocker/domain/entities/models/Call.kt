package com.tarasovvp.smartblocker.domain.entities.models

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcelable
import androidx.core.content.ContextCompat
import androidx.room.Ignore
import com.google.firebase.database.Exclude
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKED_CALL
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.DATE_FORMAT
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.DEFAULT_FILTER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.MISSED_CALL
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.IN_COMING_CALL
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.REJECTED_CALL
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.TIME_FORMAT
import com.tarasovvp.smartblocker.infrastructure.prefs.SharedPrefs
import com.tarasovvp.smartblocker.utils.extensions.*
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
open class Call(
    open var callId: Int = 0,
    var callName: String? = String.EMPTY,
    var number: String = String.EMPTY,
    var type: String? = String.EMPTY,
    var callDate: String? = String.EMPTY,
    var photoUrl: String? = String.EMPTY,
    var filter: String? = String.EMPTY,
    var isFilteredCall: Boolean? = false,
    var filteredNumber: String = String.EMPTY,
    var conditionType: Int = DEFAULT_FILTER,
    var phoneNumberValue: String = String.EMPTY,
    var isPhoneNumberValid: Boolean = false
) : Parcelable {

    @IgnoredOnParcel
    @Ignore
    @get:Exclude
    var isCheckedForDelete = false

    @IgnoredOnParcel
    @Ignore
    @get:Exclude
    var isDeleteMode = false

    @IgnoredOnParcel
    @Ignore
    @get:Exclude
    var isExtract = false

    @IgnoredOnParcel
    @Ignore
    @get:Exclude
    var isFilteredCallDetails = false

    @Exclude
    fun isNameEmpty(): Boolean {
        return callName.isNullOrEmpty()
    }

    @Exclude
    fun callIcon(): Int {
        return when (type) {
            IN_COMING_CALL -> if (isCallFiltered()) R.drawable.ic_call_incoming_permitted else R.drawable.ic_call_incoming
            MISSED_CALL -> if (isCallFiltered()) R.drawable.ic_call_missed_permitted else R.drawable.ic_call_missed
            REJECTED_CALL -> if (isCallFiltered()) R.drawable.ic_call_rejected_permitted else R.drawable.ic_call_rejected
            BLOCKED_CALL -> R.drawable.ic_call_blocked
            else -> R.drawable.ic_call_outcoming
        }
    }

    @Exclude
    fun timeFromCallDate(): String? {
        return callDate?.toDateFromMilliseconds(TIME_FORMAT)
    }

    @Exclude
    fun dateFromCallDate(): String? {
        return callDate?.toDateFromMilliseconds(DATE_FORMAT)
    }

    @Exclude
    fun placeHolder(context: Context): Drawable? {
        return if (callName.isNullOrEmpty()) ContextCompat.getDrawable(context,
            R.drawable.ic_call) else if (callName.nameInitial()
                .isEmpty()
        ) ContextCompat.getDrawable(context,
            R.drawable.ic_contact) else context.getInitialDrawable(callName.nameInitial())
    }

    @Exclude
    private fun dateTimeFromCallDate(): String {
        return String.format("%s, %s", dateFromCallDate(), timeFromCallDate())
    }

    @Exclude
    fun isBlockedCall(): Boolean {
        return type == BLOCKED_CALL
    }

    @Exclude
    fun isPermittedCall(): Boolean {
        return isCallFiltered() && isBlockedCall().not()
    }

    @Exclude
    fun isFilterNullOrEmpty(): Boolean {
        return filter.isNullOrEmpty().isTrue()
    }

    @Exclude
    fun isCallFiltered(): Boolean {
        return isFilteredCall.isTrue() || isFilteredCallDetails
    }

    @Exclude
    fun isFilteredCallDelete(): Boolean {
        return (isCallFiltered() || number.isEmpty()) && isDeleteMode
    }

    @Exclude
    fun callFilterValue(): String {
        return when {
            isFilteredCallDetails -> dateTimeFromCallDate()
            isExtract.not() && isCallFiltered() -> filteredNumber
            isExtract && isFilterNullOrEmpty().not() -> filter.orEmpty()
            else -> String.EMPTY
        }
    }

    @Exclude
    fun callFilterTitle(filter: Filter?): Int {
        return if (isExtract && !isFilteredCallDetails) {
            when {
                filter?.isPermission().isTrue() -> R.string.details_number_permit_with_filter
                filter?.isBlocker().isTrue() -> R.string.details_number_block_with_filter
                number.isEmpty() && SharedPrefs.blockHidden.isTrue() -> R.string.details_number_hidden_on
                number.isEmpty() && SharedPrefs.blockHidden.isNotTrue() -> R.string.details_number_hidden_off
                else -> R.string.details_number_contact_without_filter
            }
        } else {
            if (isCallFiltered()) {
                when {
                    number.isEmpty() -> R.string.details_number_blocked_by_settings
                    isPermittedCall() -> R.string.details_number_permitted_by_filter
                    else -> R.string.details_number_blocked_by_filter
                }
            } else {
                R.string.details_number_call_without_filter
            }
        }
    }

    @Exclude
    fun callFilterIcon(): Int? {
        return when {
            isCallFiltered() && filteredNumber.isEmpty() -> R.drawable.ic_settings_small
            isCallFiltered() -> FilterCondition.values()[conditionType].smallIcon(type == BLOCKED_CALL)
            else -> null
        }
    }

    @Exclude
    fun callFilterTint(filter: Filter?): Int {
        return when {
            (isCallFiltered() && isBlockedCall()) || (isExtract && filter?.isBlocker().isTrue()) -> R.color.sunset
            (isCallFiltered() && isPermittedCall()) || (isExtract && filter?.isPermission().isTrue()) -> R.color.islamic_green
            else -> R.color.text_color_grey
        }
    }
}
