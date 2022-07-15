package com.tarasovvp.blacklister.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class BlackNumber(
    @PrimaryKey val number: String = "",
    val start: Boolean = false,
    val contain: Boolean = false,
    val end: Boolean = false
) : Parcelable, BaseAdapter.MainData {
    var isCheckedForDelete = false
}
