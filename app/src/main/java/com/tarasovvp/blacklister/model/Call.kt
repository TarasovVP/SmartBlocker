package com.tarasovvp.blacklister.model

import android.os.Parcelable
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.BLOCKED_CALL
import com.tarasovvp.blacklister.constants.Constants.DATE_FORMAT
import com.tarasovvp.blacklister.constants.Constants.MISSED_CALL
import com.tarasovvp.blacklister.constants.Constants.OUT_COMING_CALL
import com.tarasovvp.blacklister.constants.Constants.REJECTED_CALL
import com.tarasovvp.blacklister.constants.Constants.TIME_FORMAT
import com.tarasovvp.blacklister.extensions.toDateFromMilliseconds
import com.tarasovvp.blacklister.extensions.toMillisecondsFromString
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import kotlinx.android.parcel.Parcelize
import java.util.*

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

    fun callIcon(): Int {
        return when (type) {
            OUT_COMING_CALL -> R.drawable.ic_outcoming_call
            MISSED_CALL -> R.drawable.ic_missed_call
            REJECTED_CALL -> R.drawable.ic_rejected_call
            BLOCKED_CALL -> R.drawable.ic_stop
            else -> R.drawable.ic_incoming_call
        }
    }

    fun calendarFromTime(): Calendar {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time?.toMillisecondsFromString() ?: 0
        calendar.add(Calendar.MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar
    }

    fun dateFromTime(): String? {
        return time?.toDateFromMilliseconds(DATE_FORMAT)
    }

    fun dateTimeFromTime(): String? {
        return time?.toDateFromMilliseconds(TIME_FORMAT)
    }
}
