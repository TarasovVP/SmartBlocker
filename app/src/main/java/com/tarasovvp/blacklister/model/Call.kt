package com.tarasovvp.blacklister.model

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Parcelable
import androidx.core.content.ContextCompat
import androidx.room.Ignore
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.constants.Constants.BLOCKED_CALL
import com.tarasovvp.blacklister.constants.Constants.DATE_FORMAT
import com.tarasovvp.blacklister.constants.Constants.MISSED_CALL
import com.tarasovvp.blacklister.constants.Constants.OUT_COMING_CALL
import com.tarasovvp.blacklister.constants.Constants.REJECTED_CALL
import com.tarasovvp.blacklister.enums.FilterCondition
import com.tarasovvp.blacklister.extensions.EMPTY
import com.tarasovvp.blacklister.extensions.nameInitial
import com.tarasovvp.blacklister.extensions.toDateFromMilliseconds
import kotlinx.android.parcel.Parcelize

@Parcelize
open class Call(
    open var id: Int = 0,
    var callId: String? = String.EMPTY,
    var name: String? = String.EMPTY,
    var number: String = String.EMPTY,
    var normalizedNumber: String? = String.EMPTY,
    var type: String? = String.EMPTY,
    var callDate: String? = String.EMPTY,
    var photoUrl: String? = String.EMPTY,
    var countryIso: String? = String.EMPTY,
    var numberPresentation: String? = String.EMPTY,
    var filter: Filter? = Filter(),
) : Parcelable, NumberData() {
    var isCheckedForDelete = false
    var isDeleteMode = false
    var searchText = String.EMPTY
    var isExtract = false

    fun filterTypeIcon(): Int {
        return when (filter?.filterType) {
            Constants.BLACK_FILTER -> R.drawable.ic_black_filter
            Constants.WHITE_FILTER -> R.drawable.ic_white_filter
            else -> 0
        }
    }

    fun isNumberEmpty(): Boolean {
        return number.isEmpty()
    }

    fun isNameEmpty(): Boolean {
        return name.isNullOrEmpty()
    }

    fun callIcon(): Int {
        return when (type) {
            OUT_COMING_CALL -> R.drawable.ic_outcoming_call
            MISSED_CALL -> R.drawable.ic_missed_call
            REJECTED_CALL -> R.drawable.ic_rejected_call
            BLOCKED_CALL -> R.drawable.ic_blocked_call
            else -> R.drawable.ic_incoming_call
        }
    }

    fun timeFromCallDate(): String? {
        return callDate?.toDateFromMilliseconds(Constants.TIME_FORMAT)
    }

    fun dateFromCallDate(): String? {
        return callDate?.toDateFromMilliseconds(DATE_FORMAT)
    }

    fun dateTimeFromCallDate(): String {
        return String.format("%s, %s", dateFromCallDate(), timeFromCallDate())
    }

    fun isBlockedCall(): Boolean {
        return type == BLOCKED_CALL
    }

    fun nameInitial(): String {
        return if (name.isNullOrEmpty()) String(Character.toChars(128222)) else name.nameInitial()
    }

    fun filterConditionTypeIcon(context: Context): Drawable {
        return when (this) {
            is BlockedCall -> filter?.conditionType?.let { blockFilterCondition ->
                FilterCondition.getSmallIconByIndex(blockFilterCondition)
            }?.let {
                ContextCompat.getDrawable(context, it)
            } ?: ColorDrawable(Color.TRANSPARENT)
            else -> ColorDrawable(Color.TRANSPARENT)
        }
    }

    fun filterValue(): String {
        return filter?.filter.orEmpty()
    }
}
