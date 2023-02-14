package com.tarasovvp.smartblocker.models

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcelable
import androidx.core.content.ContextCompat
import androidx.room.Embedded
import androidx.room.Ignore
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants.BLOCKED_CALL
import com.tarasovvp.smartblocker.constants.Constants.DATE_FORMAT
import com.tarasovvp.smartblocker.constants.Constants.MISSED_CALL
import com.tarasovvp.smartblocker.constants.Constants.OUT_COMING_CALL
import com.tarasovvp.smartblocker.constants.Constants.PERMITTED_CALL
import com.tarasovvp.smartblocker.constants.Constants.REJECTED_CALL
import com.tarasovvp.smartblocker.constants.Constants.TIME_FORMAT
import com.tarasovvp.smartblocker.extensions.*
import com.tarasovvp.smartblocker.local.SharedPreferencesUtil
import kotlinx.android.parcel.Parcelize

@Parcelize
open class Call(
    open var callId: Int = 0,
    var callName: String? = String.EMPTY,
    var number: String = String.EMPTY,
    var normalizedNumber: String? = String.EMPTY,
    var type: String? = String.EMPTY,
    var callDate: String? = String.EMPTY,
    var photoUrl: String? = String.EMPTY,
    var countryIso: String? = String.EMPTY,
    @Embedded(prefix = "filter_") var filter: Filter? = Filter(),
) : Parcelable, NumberData() {
    @Ignore
    var isCheckedForDelete = false

    @Ignore
    var isDeleteMode = false

    @Ignore
    var isExtract = false

    @Ignore
    var isFilteredCallDetails = false

    fun isNameEmpty(): Boolean {
        return callName.isNullOrEmpty()
    }

    fun callIcon(): Int {
        return when (type) {
            OUT_COMING_CALL -> R.drawable.ic_call_outcoming
            MISSED_CALL -> R.drawable.ic_call_missed
            REJECTED_CALL -> R.drawable.ic_call_rejected
            BLOCKED_CALL -> R.drawable.ic_call_blocked
            PERMITTED_CALL -> R.drawable.ic_call_permitted
            else -> R.drawable.ic_call_incoming
        }
    }

    fun timeFromCallDate(): String? {
        return callDate?.toDateFromMilliseconds(TIME_FORMAT)
    }

    fun dateFromCallDate(): String? {
        return callDate?.toDateFromMilliseconds(DATE_FORMAT)
    }

    fun placeHolder(context: Context): Drawable? {
        return if (callName.isNullOrEmpty()) ContextCompat.getDrawable(context,
            R.drawable.ic_call) else if (callName.nameInitial()
                .isEmpty()
        ) ContextCompat.getDrawable(context,
            R.drawable.ic_contact) else context.getInitialDrawable(callName.nameInitial())
    }

    private fun dateTimeFromCallDate(): String {
        return String.format("%s, %s", dateFromCallDate(), timeFromCallDate())
    }

    fun isBlockedCall(): Boolean {
        return type == BLOCKED_CALL
    }

    fun isPermittedCall(): Boolean {
        return type == PERMITTED_CALL
    }

    fun filterValue(): String {
        return filter?.filter.orEmpty()
    }

    fun isFilterNullOrEmpty(): Boolean {
        return filter?.filter.isNullOrEmpty().isTrue()
    }

    fun isFilteredNotNullOrEmpty(): Boolean {
        return this is FilteredCall && filtered?.filter.isNullOrEmpty().not()
    }

    fun isFilteredCallDelete(): Boolean {
        return this is FilteredCall && (isFilteredNotNullOrEmpty() || number.isEmpty()) && isDeleteMode
    }

    fun callFilterTitle(): Int {
        return if (isExtract && !isFilteredCallDetails) {
            when {
                filter?.isPermission().isTrue() -> R.string.details_number_permit_with_filter
                filter?.isBlocker().isTrue() -> R.string.details_number_block_with_filter
                number.isEmpty() && SharedPreferencesUtil.blockHidden -> R.string.details_number_hidden_on
                number.isEmpty() && SharedPreferencesUtil.blockHidden.not() -> R.string.details_number_hidden_off
                else -> R.string.details_number_contact_without_filter
            }
        } else {
            if (this is FilteredCall) {
                when {
                    number.isEmpty() -> R.string.details_number_blocked_by_settings
                    filtered?.isPermission().isTrue() -> R.string.details_number_permitted_by_filter
                    else -> R.string.details_number_blocked_by_filter
                }
            } else {
                R.string.details_number_call_without_filter
            }
        }
    }

    fun callFilterValue(): String {
        return when {
            isFilteredCallDetails -> dateTimeFromCallDate()
            isExtract.not() && this is FilteredCall && isFilteredNotNullOrEmpty() -> filtered?.filter.orEmpty()
            isExtract && isFilterNullOrEmpty().not() -> filter?.filter.orEmpty()
            else -> String.EMPTY
        }
    }

    fun callFilterIcon(): Int? {
        return when {
            isExtract.not() && this is FilteredCall && isFilteredNotNullOrEmpty().not() -> R.drawable.ic_settings_small
            isExtract.not() && this is FilteredCall && isFilteredNotNullOrEmpty() -> filtered?.conditionTypeSmallIcon()
            isExtract && isFilterNullOrEmpty().not() -> filter?.conditionTypeSmallIcon()
            else -> null
        }
    }

    fun callFilterTint(): Int {
        return when {
            isExtract.not() && this is FilteredCall && filtered?.isBlocker()
                .isTrue() -> R.color.sunset
            isExtract.not() && this is FilteredCall && filtered?.isPermission()
                .isTrue() -> R.color.islamic_green
            isExtract && filter?.isBlocker().isTrue() -> R.color.sunset
            isExtract && filter?.isPermission().isTrue() -> R.color.islamic_green
            else -> R.color.text_color_grey
        }
    }
}
