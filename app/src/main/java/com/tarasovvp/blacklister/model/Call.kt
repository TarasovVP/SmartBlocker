package com.tarasovvp.blacklister.model

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Parcelable
import androidx.core.content.ContextCompat
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
import com.tarasovvp.blacklister.ui.number_data.NumberData
import kotlinx.android.parcel.Parcelize

@Parcelize
open class Call(
    open var id: Int = 0,
    var callId: String? = String.EMPTY,
    var name: String? = String.EMPTY,
    var number: String = String.EMPTY,
    var normalizedNumber: String? = String.EMPTY,
    var type: String? = String.EMPTY,
    var time: String? = String.EMPTY,
    var photoUrl: String? = String.EMPTY,
    var countryIso: String? = String.EMPTY,
    var numberPresentation: String? = String.EMPTY,
    var filterType: Int = Constants.DEFAULT_FILTER,
) : Parcelable, NumberData {

    var isCheckedForDelete = false
    var isDeleteMode = false
    var searchText = String.EMPTY

    fun filterTypeIcon(): Int {
        return when (filterType) {
            Constants.BLACK_FILTER -> R.drawable.ic_block
            Constants.WHITE_FILTER -> R.drawable.ic_accepted
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

    fun dateTimeFromTime(): String? {
        return time?.toDateFromMilliseconds(Constants.TIME_FORMAT)
    }

    fun dateFromTime(): String? {
        return time?.toDateFromMilliseconds(DATE_FORMAT)
    }

    fun isBlockedType(): Boolean {
        return type == BLOCKED_CALL
    }

    fun nameInitial(): String {
        return if (name.isNullOrEmpty()) String(Character.toChars(128222)) else name.nameInitial()
    }

    private fun getBlockedCall(): BlockedCall? {
        return if (this is BlockedCall) this else null
    }

    fun blockConditionTypeIcon(context: Context): Drawable {
        return getBlockedCall()?.blockFilterFilterCondition?.let { blockFilterCondition ->
            FilterCondition.getSmallIconByIndex(blockFilterCondition)
        }?.let {
            ContextCompat.getDrawable(context, it)
        } ?: ColorDrawable(Color.TRANSPARENT)
    }

    fun getBlockFilterValue(): String {
        return getBlockedCall()?.blockFilter ?: String.EMPTY
    }
}
