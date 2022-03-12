package com.example.blacklister.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.blacklister.R
import com.example.blacklister.constants.Constants.MISSED_CALL
import com.example.blacklister.constants.Constants.OUTCOMING_CALL
import com.example.blacklister.constants.Constants.REJECTED_CALL
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class CallLog(
    var name: String? = "",
    @PrimaryKey var phone: String = "",
    var type: String? = "",
    var time: String? = "",
    var isBlackList: Boolean = false
) : Parcelable {
    fun callLogIcon(): Int {
        return when (type) {
            OUTCOMING_CALL -> R.drawable.ic_outcoming_call
            MISSED_CALL -> R.drawable.ic_missed_call
            REJECTED_CALL -> R.drawable.ic_rejected_call
            else -> R.drawable.ic_incoming_call
        }
    }
}
