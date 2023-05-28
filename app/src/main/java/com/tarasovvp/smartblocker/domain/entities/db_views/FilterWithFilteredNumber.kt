package com.tarasovvp.smartblocker.domain.entities.db_views

import android.os.Parcelable
import androidx.room.*
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.utils.extensions.*
import kotlinx.parcelize.Parcelize
import java.util.*

@DatabaseView("SELECT filters.*, " +
        "(SELECT COUNT(*) FROM contacts WHERE ((filters.filter = contacts.phoneNumberValue AND filters.conditionType = 0) OR (contacts.phoneNumberValue LIKE filters.filter || '%' AND filters.conditionType = 1) OR (contacts.phoneNumberValue LIKE '%' || filters.filter || '%' AND filters.conditionType = 2))) filteredContacts, " +
        "(SELECT COUNT(*) FROM log_calls WHERE ((filters.filter = log_calls.phoneNumberValue AND filters.conditionType = 0) OR (log_calls.phoneNumberValue LIKE filters.filter || '%' AND filters.conditionType = 1) OR (log_calls.phoneNumberValue LIKE '%' || filters.filter || '%' AND filters.conditionType = 2))) + " +
        "(SELECT COUNT(*) FROM filtered_calls WHERE ((filters.filter = filtered_calls.phoneNumberValue AND filters.conditionType = 0) OR (filtered_calls.phoneNumberValue LIKE filters.filter || '%' AND filters.conditionType = 1) OR (filtered_calls.phoneNumberValue LIKE '%' || filters.filter || '%' AND filters.conditionType = 2))) filteredCalls FROM filters")
@Parcelize
data class FilterWithFilteredNumber(
    @Embedded
    var filter: Filter? = null,
    @ColumnInfo(name = "filteredContacts")
    var filteredContacts: Int? = 0,
    @ColumnInfo(name = "filteredCalls")
    var filteredCalls: Int? = 0
) : Parcelable