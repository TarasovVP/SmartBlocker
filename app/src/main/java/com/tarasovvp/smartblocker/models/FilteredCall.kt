package com.tarasovvp.smartblocker.models

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class FilteredCall(
    @PrimaryKey override var callId: Int = 0,
) : Call(), Parcelable {
    @Embedded(prefix = "filtered_") var filtered: Filter? = Filter()
}