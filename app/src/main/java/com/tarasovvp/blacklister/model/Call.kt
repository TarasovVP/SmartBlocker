package com.tarasovvp.blacklister.model

import android.os.Parcelable
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.constants.Constants.BLOCKED_CALL
import com.tarasovvp.blacklister.constants.Constants.DATE_FORMAT
import com.tarasovvp.blacklister.constants.Constants.MISSED_CALL
import com.tarasovvp.blacklister.constants.Constants.OUT_COMING_CALL
import com.tarasovvp.blacklister.constants.Constants.REJECTED_CALL
import com.tarasovvp.blacklister.extensions.toDateFromMilliseconds
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import kotlinx.android.parcel.Parcelize

@Parcelize
open class Call(
    open var id: Int = 0,
    var name: String? = "",
    var phone: String? = "",
    var type: String? = "",
    var time: String? = "",
    var photoUrl: String? = "",
) : Parcelable, BaseAdapter.MainData {

    var isCheckedForDelete = false
    var isDeleteMode = false

    fun isPhoneEmpty(): Boolean {
        return phone.isNullOrEmpty()
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
}
