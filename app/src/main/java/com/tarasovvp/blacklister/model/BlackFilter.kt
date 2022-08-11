package com.tarasovvp.blacklister.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class BlackFilter(
    @PrimaryKey override var filter: String = "",
) : Filter(), Parcelable {
    override var isBlackFilter = true
}
