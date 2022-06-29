package com.tarasovvp.blacklister.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tarasovvp.blacklister.constants.Constants.BLOCKED_CALL
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class BlockedCall(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var name: String? = "",
    var phone: String? = "",
    var time: String? = "",
    var type: String? = BLOCKED_CALL,
) : Parcelable