package com.tarasovvp.smartblocker.domain.models.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.tarasovvp.smartblocker.domain.models.Call
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.LOG_CALLS
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import kotlinx.parcelize.Parcelize

@Entity(tableName = LOG_CALLS, indices = [Index(value = ["callDate"], unique = true)])
@Parcelize
data class LogCall(
    @PrimaryKey var callId: Int = 0,
    var callName: String? = String.EMPTY,
    var number: String = String.EMPTY,
    var type: String? = String.EMPTY,
    var callDate: String? = String.EMPTY,
    var photoUrl: String? = String.EMPTY
) : Parcelable, Call()