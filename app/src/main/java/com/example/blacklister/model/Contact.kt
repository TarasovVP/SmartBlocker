package com.example.blacklister.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Contact(
    var id: String = "",
    var name: String? = "",
    var photoUrl: String? = "",
    var phone: String? = "",
    var isPhoneClient: Boolean = false
) : Parcelable
