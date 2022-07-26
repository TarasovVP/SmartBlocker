package com.tarasovvp.blacklister.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class Contact(
    var id: String = "",
    var name: String? = "",
    var photoUrl: String? = "",
    @PrimaryKey var phone: String = "",
    var isBlackList: Boolean = false,
    var isWhiteList: Boolean = false
) : Parcelable, BaseAdapter.MainData
