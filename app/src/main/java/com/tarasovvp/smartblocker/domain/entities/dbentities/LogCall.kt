package com.tarasovvp.smartblocker.domain.entities.dbentities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.tarasovvp.smartblocker.domain.entities.models.Call
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.LOG_CALLS
import kotlinx.parcelize.Parcelize

@Entity(tableName = LOG_CALLS, indices = [Index(value = ["callDate"], unique = true)])
@Parcelize
data class LogCall(
    @PrimaryKey override var callId: Int = 0,
) : Call(), Parcelable
