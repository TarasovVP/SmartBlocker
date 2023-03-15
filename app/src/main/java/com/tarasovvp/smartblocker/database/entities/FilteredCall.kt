package com.tarasovvp.smartblocker.database.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tarasovvp.smartblocker.constants.Constants.FILTERED_CALLS
import com.tarasovvp.smartblocker.models.Call
import kotlinx.parcelize.Parcelize

@Entity(tableName = FILTERED_CALLS)
@Parcelize
data class FilteredCall(
    @PrimaryKey override var callId: Int = 0
) : Call(), Parcelable