package com.tarasovvp.smartblocker.presentation.ui_models

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcelable
import androidx.core.content.ContextCompat
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.utils.extensions.*
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
open class CallWithFilterUIModel(
    var callId: Int = 0,
    var callName: String = String.EMPTY,
    var number: String = String.EMPTY,
    var type: String = String.EMPTY,
    var callDate: String = String.EMPTY,
    var photoUrl: String = String.EMPTY,
    var isFilteredCall: Boolean = false,
    var filteredNumber: String = String.EMPTY,
    var conditionType: Int = Constants.DEFAULT_FILTER,
    var filterUIModel: FilterUIModel? = FilterUIModel()
) : Parcelable, NumberDataUIModel() {

    @IgnoredOnParcel
    private val isBlockHidden = false

    @IgnoredOnParcel
    var isCheckedForDelete = false

    @IgnoredOnParcel
    var isDeleteMode = false

    @IgnoredOnParcel
    var isExtract = false

    @IgnoredOnParcel
    var isFilteredCallDetails = false

    fun isNameEmpty(): Boolean {
        return callName.isEmpty()
    }

    fun callIcon(): Int {
        return when (type) {
            Constants.IN_COMING_CALL -> if (isCallFiltered()) R.drawable.ic_call_incoming_permitted else R.drawable.ic_call_incoming
            Constants.MISSED_CALL -> if (isCallFiltered()) R.drawable.ic_call_missed_permitted else R.drawable.ic_call_missed
            Constants.REJECTED_CALL -> if (isCallFiltered()) R.drawable.ic_call_rejected_permitted else R.drawable.ic_call_rejected
            Constants.BLOCKED_CALL -> R.drawable.ic_call_blocked
            else -> R.drawable.ic_call_outcoming
        }
    }

    fun timeFromCallDate(): String {
        return callDate.toDateFromMilliseconds(Constants.TIME_FORMAT)
    }

    fun dateFromCallDate(): String {
        return callDate.toDateFromMilliseconds(Constants.DATE_FORMAT)
    }

    fun placeHolder(context: Context): Drawable? {
        return if (callName.isEmpty()) ContextCompat.getDrawable(context,
            R.drawable.ic_call) else if (callName.nameInitial()
                .isEmpty()
        ) ContextCompat.getDrawable(context,
            R.drawable.ic_contact) else context.getInitialDrawable(callName.nameInitial())
    }

    private fun dateTimeFromCallDate(): String {
        return String.format("%s, %s", dateFromCallDate(), timeFromCallDate())
    }

    fun isBlockedCall(): Boolean {
        return type == Constants.BLOCKED_CALL
    }

    fun isPermittedCall(): Boolean {
        return isCallFiltered() && isBlockedCall().not()
    }

    fun isCallFiltered(): Boolean {
        return isFilteredCall || isFilteredCallDetails
    }

    fun isFilteredCallDelete(): Boolean {
        return (isCallFiltered() || number.isEmpty()) && isDeleteMode
    }

    fun callFilterValue(): String {
        return when {
            isFilteredCallDetails -> dateTimeFromCallDate()
            isExtract.not() && isCallFiltered() -> filteredNumber
            isExtract && filteredNumber.isNotEmpty() -> filteredNumber
            else -> String.EMPTY
        }
    }

    fun callFilterTitle(filter: FilterUIModel?): Int {
        return if (isExtract && !isFilteredCallDetails) {
            when {
                filter?.isPermission().isTrue() -> R.string.details_number_permit_with_filter
                filter?.isBlocker().isTrue() -> R.string.details_number_block_with_filter
                number.isEmpty() && isBlockHidden -> R.string.details_number_hidden_on
                number.isEmpty() && isBlockHidden.isNotTrue() -> R.string.details_number_hidden_off
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

    fun callFilterIcon(): Int? {
        return when {
            isCallFiltered() && filteredNumber.isEmpty() -> R.drawable.ic_settings_small
            isCallFiltered() -> FilterCondition.values()[conditionType].smallIcon(type == Constants.BLOCKED_CALL)
            else -> null
        }
    }

    fun callFilterTint(filter: FilterUIModel?): Int {
        return when {
            (isCallFiltered() && isBlockedCall()) || (isExtract && filter?.isBlocker().isTrue()) -> R.color.sunset
            (isCallFiltered() && isPermittedCall()) || (isExtract && filter?.isPermission().isTrue()) -> R.color.islamic_green
            else -> R.color.text_color_grey
        }
    }
}

