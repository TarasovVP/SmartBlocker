package com.tarasovvp.blacklister.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tarasovvp.blacklister.enum.BlackNumberCategory
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class WhiteNumber(
    @PrimaryKey val whiteNumber: String = "",
    val isStart: Boolean = false,
    val isContain: Boolean = false,
    val isEnd: Boolean = false
) : Parcelable, BaseAdapter.MainData
