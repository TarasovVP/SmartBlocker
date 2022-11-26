package com.tarasovvp.smartblocker.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class FilteredCall(
    @PrimaryKey(autoGenerate = true) override var id: Int = 0,
) : Call(), Parcelable