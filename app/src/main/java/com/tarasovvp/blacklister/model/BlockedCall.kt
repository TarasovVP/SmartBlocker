package com.tarasovvp.blacklister.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class BlockedCall(
    @PrimaryKey(autoGenerate = true) override var id: Int = 0,
) : Call(), Parcelable