package com.tarasovvp.blacklister.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class WhiteFilter(
    @PrimaryKey override var filter: String = "",
) : Filter(), Parcelable