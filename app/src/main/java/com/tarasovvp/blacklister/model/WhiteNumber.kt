package com.tarasovvp.blacklister.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class WhiteNumber(
    @PrimaryKey val number: String = "",
    var start: Boolean = false,
    var contain: Boolean = false,
    var end: Boolean = false,
) : Parcelable, BaseAdapter.MainData {
    var isCheckedForDelete = false
}
