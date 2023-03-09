package com.tarasovvp.smartblocker.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tarasovvp.smartblocker.constants.Constants.FILTERED_CALLS
import kotlinx.parcelize.Parcelize

@Entity(tableName = FILTERED_CALLS)
@Parcelize
data class FilteredCall(
    @PrimaryKey override var callId: Int = 0
) : Call(), Parcelable