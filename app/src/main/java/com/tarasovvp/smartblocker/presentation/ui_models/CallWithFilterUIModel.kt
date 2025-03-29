package com.tarasovvp.smartblocker.presentation.ui_models

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcelable
import androidx.core.content.ContextCompat
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.getInitialDrawable
import com.tarasovvp.smartblocker.utils.extensions.isNotTrue
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import com.tarasovvp.smartblocker.utils.extensions.nameInitial
import com.tarasovvp.smartblocker.utils.extensions.toDateFromMilliseconds
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class CallWithFilterUIModel(
    var callId: Int = 0,
    var callName: String = String.EMPTY,
    var number: String = String.EMPTY,
    var type: String = String.EMPTY,
    var callDate: String = String.EMPTY,
    var photoUrl: String = String.EMPTY,
    var isFilteredCall: Boolean = false,
    var filteredNumber: String = String.EMPTY,
    var conditionType: Int = Constants.DEFAULT_FILTER,
    var phoneNumberValue: String = String.EMPTY,
    var isPhoneNumberValid: Boolean = false,
    var filterWithFilteredNumberUIModel: FilterWithFilteredNumberUIModel = FilterWithFilteredNumberUIModel(),
) : Parcelable, NumberDataUIModel() {
    @IgnoredOnParcel
    private val isSystemBlockHidden = false

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

    fun filterTypeIcon(): Int? {
        return if (isEmptyFilter()) null else filterWithFilteredNumberUIModel.filterTypeIcon()
    }

    fun isEmptyFilter(): Boolean {
        return filterWithFilteredNumberUIModel.filter.isEmpty()
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

    fun fullCallDate(): String {
        return String.format("%s, %s", dateFromCallDate(), timeFromCallDate())
    }

    fun placeHolder(context: Context): Drawable? {
        return if (callName.isEmpty()) {
            ContextCompat.getDrawable(
                context,
                R.drawable.ic_call,
            )
        } else if (callName.nameInitial()
                .isEmpty()
        ) {
            ContextCompat.getDrawable(
                context,
                R.drawable.ic_contact,
            )
        } else {
            context.getInitialDrawable(callName.nameInitial())
        }
    }

    private fun isBlockedCall(): Boolean {
        return type == Constants.BLOCKED_CALL
    }

    private fun isPermittedCall(): Boolean {
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
            isFilteredCallDetails -> filteredNumber
            isExtract.not() && isCallFiltered() -> filteredNumber
            isExtract -> filterWithFilteredNumberUIModel.filter
            else -> String.EMPTY
        }
    }

    fun callFilterTitle(): Int {
        return if (isExtract && !isFilteredCallDetails) {
            when {
                filterWithFilteredNumberUIModel.isPermission()
                    .isTrue() -> R.string.details_number_permit_with_filter

                filterWithFilteredNumberUIModel.isBlocker()
                    .isTrue() -> R.string.details_number_block_with_filter

                number.isEmpty() && isSystemBlockHidden -> R.string.details_number_hidden_on
                number.isEmpty() && isSystemBlockHidden.isNotTrue() -> R.string.details_number_hidden_off
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
            isExtract && !isFilteredCallDetails -> filterWithFilteredNumberUIModel.conditionTypeSmallIcon()
            isCallFiltered() && number.isEmpty() -> R.drawable.ic_settings_small
            isCallFiltered() ->
                conditionType.takeIf { it >= 0 }
                    ?.let { FilterCondition.values()[it].smallIcon(type == Constants.BLOCKED_CALL) }

            else -> null
        }
    }

    fun callFilterTint(filter: FilterWithFilteredNumberUIModel?): Int {
        return when {
            (isCallFiltered() && isBlockedCall()) || (
                isExtract &&
                    filter?.isBlocker()
                        .isTrue()
            ) -> R.color.sunset

            (isCallFiltered() && isPermittedCall()) || (
                isExtract &&
                    filter?.isPermission()
                        .isTrue()
            ) -> R.color.islamic_green

            else -> R.color.text_color_grey
        }
    }
}
