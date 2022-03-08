package com.example.blacklister.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class Contact(
    @PrimaryKey var id: String = "",
    var name: String? = "",
    var photoUrl: String? = "",
    var phone: String? = "",
    var isPhoneClient: Boolean = false,
    var isBlackList: Boolean = false
) : Parcelable
