package com.tarasovvp.smartblocker.models

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class FilteredCall(
    @PrimaryKey override var callId: Int = 0,
    @Embedded(prefix = "filtered_") var filtered: Filter? = Filter()
) : Call(), Parcelable