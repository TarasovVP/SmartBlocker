package com.tarasovvp.smartblocker.domain.entities.models

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.entities.models.Call
import com.tarasovvp.smartblocker.domain.entities.models.NumberData
import kotlinx.parcelize.Parcelize

@Parcelize
open class CallWithFilter(
    @Embedded
    var call: Call? = Call(),
    @Relation(
        parentColumn = "filter",
        entityColumn = "filter"
    )
    var filterWithCountryCode: FilterWithCountryCode? = FilterWithCountryCode()
) : Parcelable, NumberData()
