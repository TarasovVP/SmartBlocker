package com.tarasovvp.smartblocker.models

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcelable
import androidx.core.content.ContextCompat
import androidx.room.Embedded
import androidx.room.Ignore
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants
import com.tarasovvp.smartblocker.constants.Constants.BLOCKED_CALL
import com.tarasovvp.smartblocker.constants.Constants.DATE_FORMAT
import com.tarasovvp.smartblocker.constants.Constants.MISSED_CALL
import com.tarasovvp.smartblocker.constants.Constants.OUT_COMING_CALL
import com.tarasovvp.smartblocker.constants.Constants.PERMITTED_CALL
import com.tarasovvp.smartblocker.constants.Constants.REJECTED_CALL
import com.tarasovvp.smartblocker.extensions.*
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
            OUT_COMING_CALL -> R.drawable.ic_outcoming_call
            MISSED_CALL -> R.drawable.ic_missed_call
            REJECTED_CALL -> R.drawable.ic_rejected_call
            BLOCKED_CALL -> R.drawable.ic_blocked_call
            PERMITTED_CALL -> R.drawable.ic_allowed_call
            else -> R.drawable.ic_incoming_call
        }
    }

    fun timeFromCallDate(): String? {
        return callDate?.toDateFromMilliseconds(Constants.TIME_FORMAT)
    }

    fun dateFromCallDate(): String? {
        return callDate?.toDateFromMilliseconds(DATE_FORMAT)
    }

    fun placeHolder(context: Context): Drawable? {
        return if (callName.isNullOrEmpty()) ContextCompat.getDrawable(context,
            R.drawable.ic_call_list) else context.getInitialDrawable(callName.nameInitial())
    }

    fun dateTimeFromCallDate(): String {
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

    fun isFilteredNullOrEmpty(): Boolean {
        return this is FilteredCall && filtered?.filter.isNullOrEmpty().isTrue()
    }

    fun callFilterTitle(): Int {
        return if (this is FilteredCall) {
            if (isFilteredNullOrEmpty()) {
                R.string.without_filter_call
            } else if (filtered?.isBlocker().isTrue()) {
                R.string.blocker_call_value
            } else {
                R.string.permission_call_value
            }
        } else {
            if (isExtract.not()) {
                R.string.without_filter_call
            } else {
                if (isFilterNullOrEmpty()) {
                    R.string.without_filter
                } else if (filter?.isBlocker().isTrue()) {
                    R.string.blocker_indication_value
                } else {
                    R.string.permission_indication_value
                }
            }
        }
    }

    fun callFilterValue(): String {
        return when {
            this is FilteredCall && isFilteredNullOrEmpty().not() && isExtract.not() -> filtered?.filter.orEmpty()
            this is LogCall && isFilterNullOrEmpty().not() && isExtract -> filter?.filter.orEmpty()
            else -> String.EMPTY
        }
    }

    fun callFilterIcon(): Int? {
        return when {
            this is FilteredCall && isFilteredNullOrEmpty().not() && isExtract.not() -> filtered?.conditionTypeSmallIcon()
            this is LogCall && isFilterNullOrEmpty().not() && isExtract -> filter?.conditionTypeSmallIcon()
            else -> null
        }
    }

    fun callFilterTint(): Int {
        return when {
            this is FilteredCall && isFilteredNullOrEmpty().not() && isExtract.not() -> when {
                filtered?.isBlocker().isTrue() -> R.color.sunset
                else -> R.color.islamic_green
            }
            this is LogCall && isFilterNullOrEmpty().not() && isExtract -> when {
                filter?.isBlocker().isTrue() -> R.color.sunset
                else -> R.color.islamic_green
            }
            else -> R.color.comet
        }
    }
}
