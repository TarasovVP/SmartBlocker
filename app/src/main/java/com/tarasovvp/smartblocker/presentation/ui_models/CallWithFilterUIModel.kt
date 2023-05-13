package com.tarasovvp.smartblocker.presentation.ui_models

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcelable
import androidx.core.content.ContextCompat
import androidx.room.Ignore
import com.google.firebase.database.Exclude
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.data.prefs.SharedPrefs
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.utils.extensions.*
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
open class CallWithFilterUIModel(
    var callUIModel: CallUIModel? = CallUIModel(),
    var filterUIModel: FilterUIModel? = FilterUIModel()
) : Parcelable, NumberDataUIModel() {

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
        return callUIModel?.callName.isNullOrEmpty()
    }

    @Exclude
    fun callIcon(): Int {
        return when (callUIModel?.type) {
            Constants.IN_COMING_CALL -> if (isCallFiltered()) R.drawable.ic_call_incoming_permitted else R.drawable.ic_call_incoming
            Constants.MISSED_CALL -> if (isCallFiltered()) R.drawable.ic_call_missed_permitted else R.drawable.ic_call_missed
            Constants.REJECTED_CALL -> if (isCallFiltered()) R.drawable.ic_call_rejected_permitted else R.drawable.ic_call_rejected
            Constants.BLOCKED_CALL -> R.drawable.ic_call_blocked
            else -> R.drawable.ic_call_outcoming
        }
    }

    @Exclude
    fun timeFromCallDate(): String? {
        return callUIModel?.callDate?.toDateFromMilliseconds(Constants.TIME_FORMAT)
    }

    @Exclude
    fun dateFromCallDate(): String? {
        return callUIModel?.callDate?.toDateFromMilliseconds(Constants.DATE_FORMAT)
    }

    @Exclude
    fun placeHolder(context: Context): Drawable? {
        return if (callUIModel?.callName.isNullOrEmpty()) ContextCompat.getDrawable(context,
            R.drawable.ic_call) else if (callUIModel?.callName.nameInitial()
                .isEmpty()
        ) ContextCompat.getDrawable(context,
            R.drawable.ic_contact) else context.getInitialDrawable(callUIModel?.callName.nameInitial())
    }

    @Exclude
    private fun dateTimeFromCallDate(): String {
        return String.format("%s, %s", dateFromCallDate(), timeFromCallDate())
    }

    @Exclude
    fun isBlockedCall(): Boolean {
        return callUIModel?.type == Constants.BLOCKED_CALL
    }

    @Exclude
    fun isPermittedCall(): Boolean {
        return isCallFiltered() && isBlockedCall().not()
    }

    @Exclude
    fun isCallFiltered(): Boolean {
        return callUIModel?.isFilteredCall.isTrue() || isFilteredCallDetails
    }

    @Exclude
    fun isFilteredCallDelete(): Boolean {
        return (isCallFiltered() || callUIModel?.number.orEmpty().isEmpty()) && isDeleteMode
    }

    @Exclude
    fun callFilterValue(): String {
        return when {
            isFilteredCallDetails -> dateTimeFromCallDate()
            isExtract.not() && isCallFiltered() -> callUIModel?.filteredNumber.orEmpty()
            //isExtract && isFilterNullOrEmpty().not() -> filter.orEmpty()
            else -> String.EMPTY
        }
    }

    @Exclude
    fun callFilterTitle(filter: FilterUIModel?): Int {
        return if (isExtract && !isFilteredCallDetails) {
            when {
                filter?.isPermission().isTrue() -> R.string.details_number_permit_with_filter
                filter?.isBlocker().isTrue() -> R.string.details_number_block_with_filter
                callUIModel?.number.orEmpty().isEmpty() && SharedPrefs.blockHidden.isTrue() -> R.string.details_number_hidden_on
                callUIModel?.number.orEmpty().isEmpty() && SharedPrefs.blockHidden.isNotTrue() -> R.string.details_number_hidden_off
                else -> R.string.details_number_contact_without_filter
            }
        } else {
            if (isCallFiltered()) {
                when {
                    callUIModel?.number.orEmpty().isEmpty() -> R.string.details_number_blocked_by_settings
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
            isCallFiltered() && callUIModel?.filteredNumber.orEmpty().isEmpty() -> R.drawable.ic_settings_small
            isCallFiltered() -> FilterCondition.values()[callUIModel?.conditionType.orZero()].smallIcon(callUIModel?.type == Constants.BLOCKED_CALL)
            else -> null
        }
    }

    @Exclude
    fun callFilterTint(filter: FilterUIModel?): Int {
        return when {
            (isCallFiltered() && isBlockedCall()) || (isExtract && filter?.isBlocker().isTrue()) -> R.color.sunset
            (isCallFiltered() && isPermittedCall()) || (isExtract && filter?.isPermission().isTrue()) -> R.color.islamic_green
            else -> R.color.text_color_grey
        }
    }
}

