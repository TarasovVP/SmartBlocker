package com.example.blacklister.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.blacklister.R
import com.example.blacklister.constants.Constants.DATE_FORMAT
import com.example.blacklister.constants.Constants.DATE_TIME_FORMAT
import com.example.blacklister.constants.Constants.MISSED_CALL
import com.example.blacklister.constants.Constants.OUTCOMING_CALL
import com.example.blacklister.constants.Constants.REJECTED_CALL
import com.example.blacklister.extensions.dateFromMilliseconds
import com.example.blacklister.extensions.stringToTimeMillis
import com.example.blacklister.ui.base.BaseAdapter
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
    var isBlackList: Boolean = false
) : Parcelable, BaseAdapter.MainData {
    fun callLogIcon(): Int {
        return when (type) {
            OUTCOMING_CALL -> R.drawable.ic_outcoming_call
            MISSED_CALL -> R.drawable.ic_missed_call
            REJECTED_CALL -> R.drawable.ic_rejected_call
            else -> R.drawable.ic_incoming_call
        }
    }

    fun dateFromTime(): String? {
        return time?.dateFromMilliseconds(DATE_FORMAT)
    }

    fun dateTimeFromTime(): String? {
        return time?.dateFromMilliseconds(DATE_TIME_FORMAT)
    }
}
