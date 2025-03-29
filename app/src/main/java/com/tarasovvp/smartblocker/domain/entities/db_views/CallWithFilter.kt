package com.tarasovvp.smartblocker.domain.entities.db_views

import android.os.Parcelable
import androidx.room.DatabaseView
import androidx.room.Embedded
import com.tarasovvp.smartblocker.domain.entities.models.Call
import kotlinx.parcelize.Parcelize

@DatabaseView(
    "SELECT log_calls.*, filters.* FROM log_calls LEFT JOIN filters ON (filters.filter = log_calls.phoneNumberValue AND filters.conditionType = 0) OR (log_calls.phoneNumberValue LIKE filters.filter || '%' AND filters.conditionType = 1) OR (log_calls.phoneNumberValue LIKE '%' || filters.filter || '%' AND filters.conditionType = 2) WHERE filters.filter = (SELECT filter FROM filters WHERE log_calls.phoneNumberValue LIKE filter || '%' OR log_calls.phoneNumberValue LIKE '%' || filter || '%' ORDER BY LENGTH(filter) DESC LIMIT 1) OR filters.filter IS NULL " +
        "UNION SELECT filtered_calls.*, filters.* FROM filtered_calls LEFT JOIN filters ON (filters.filter = filtered_calls.phoneNumberValue AND filters.conditionType = 0) OR (filtered_calls.phoneNumberValue LIKE filters.filter || '%' AND filters.conditionType = 1) OR (filtered_calls.phoneNumberValue LIKE '%' || filters.filter || '%' AND filters.conditionType = 2) WHERE filters.filter = (SELECT filter FROM filters WHERE filtered_calls.phoneNumberValue LIKE filter || '%' OR filtered_calls.phoneNumberValue LIKE '%' || filter || '%' ORDER BY LENGTH(filter) DESC LIMIT 1) OR filters.filter IS NULL",
)
@Parcelize
open class CallWithFilter(
    @Embedded
    var call: Call? = Call(),
    @Embedded
    var filterWithFilteredNumber: FilterWithFilteredNumber? = FilterWithFilteredNumber(),
) : Parcelable
