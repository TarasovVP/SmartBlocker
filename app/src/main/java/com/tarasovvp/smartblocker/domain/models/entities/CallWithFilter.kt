package com.tarasovvp.smartblocker.domain.models.entities

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.NumberData
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
