package com.tarasovvp.smartblocker.domain.entities.dbviews

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded
import com.tarasovvp.smartblocker.domain.entities.dbentities.Filter
import kotlinx.parcelize.Parcelize

@DatabaseView(
    "SELECT filters.*, (SELECT COUNT(*) " +
        "FROM ContactWithFilter " +
        "WHERE filter = filters.filter) filteredContacts " +
        "FROM filters",
)
@Parcelize
data class FilterWithFilteredNumber(
    @Embedded
    var filter: Filter? = null,
    @ColumnInfo(name = "filteredContacts")
    var filteredContacts: Int? = 0,
) : Parcelable
