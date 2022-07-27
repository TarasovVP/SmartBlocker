package com.tarasovvp.blacklister.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
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

@Entity(tableName = "callLog", indices = [Index(value = ["time"], unique = true)])
@Parcelize
data class CallLog(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var name: String? = "",
    var phone: String? = "",
    var type: String? = "",
    var time: String? = "",
    var isBlackList: Boolean = false,
    var isWhiteList: Boolean = false,
    var photoUrl: String? = "",
) : Parcelable, BaseAdapter.MainData {
    fun callLogIcon(): Int {
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
