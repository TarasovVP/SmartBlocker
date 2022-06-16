package com.tarasovvp.blacklister.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tarasovvp.blacklister.enum.BlackNumberCategory
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class BlackNumber(
    @PrimaryKey val blackNumber: String = "",
    val start: Boolean = false,
    val contain: Boolean = false,
    val end: Boolean = false,
    val category: Int = BlackNumberCategory.OTHER.id,
) : Parcelable, BaseAdapter.MainData
