package com.tarasovvp.smartblocker.domain.entities.db_views

import android.os.Parcelable
import androidx.room.DatabaseView
import androidx.room.Embedded
import androidx.room.Relation
import com.tarasovvp.smartblocker.domain.entities.models.Call
import com.tarasovvp.smartblocker.domain.entities.models.NumberData
import kotlinx.parcelize.Parcelize

@DatabaseView("SELECT * FROM log_calls LEFT JOIN filters ON log_calls.filter = filters.filter UNION SELECT * FROM filtered_calls LEFT JOIN filters ON filtered_calls.filter = filters.filter")
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
