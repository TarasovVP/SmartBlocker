package com.example.blacklister.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class CallLog(
    var name: String? = "",
    @PrimaryKey  var phone: String = "",
    var type: String? = "",
    var time: String? = ""
) : Parcelable
