package com.tarasovvp.blacklister.model

import android.os.Parcelable
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.constants.Constants.BLOCKED_CALL
import com.tarasovvp.blacklister.constants.Constants.DATE_FORMAT
import com.tarasovvp.blacklister.constants.Constants.MISSED_CALL
import com.tarasovvp.blacklister.constants.Constants.OUT_COMING_CALL
import com.tarasovvp.blacklister.constants.Constants.REJECTED_CALL
import com.tarasovvp.blacklister.extensions.EMPTY
import com.tarasovvp.blacklister.extensions.nameInitial
import com.tarasovvp.blacklister.extensions.toDateFromMilliseconds
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import kotlinx.android.parcel.Parcelize

@Parcelize
open class Call(
    open var id: Int = 0,
    var callId: String? = String.EMPTY,
    var name: String? = String.EMPTY,
    var number: String? = String.EMPTY,
    var normalizedNumber: String? = String.EMPTY,
    var type: String? = String.EMPTY,
    var time: String? = String.EMPTY,
    var photoUrl: String? = String.EMPTY,
    var countryIso: String? = String.EMPTY,
    var numberPresentation: String? = String.EMPTY
) : Parcelable, BaseAdapter.MainData {

    var isCheckedForDelete = false
    var isDeleteMode = false

    fun isNumberEmpty(): Boolean {
        return number.isNullOrEmpty()
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
        return name.nameInitial()
    }
}
