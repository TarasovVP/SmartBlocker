package com.tarasovvp.smartblocker.domain.models.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tarasovvp.smartblocker.domain.models.Call
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTERED_CALLS
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import kotlinx.parcelize.Parcelize

@Entity(tableName = FILTERED_CALLS)
@Parcelize
data class FilteredCall(
    @PrimaryKey var callId: Int = 0,
    var callName: String? = String.EMPTY,
    var number: String = String.EMPTY,
    var type: String? = String.EMPTY,
    var callDate: String? = String.EMPTY,
    var photoUrl: String? = String.EMPTY,
    var filter: String? = String.EMPTY,
    var conditionType: Int = Constants.DEFAULT_FILTER
) : Parcelable, Call()