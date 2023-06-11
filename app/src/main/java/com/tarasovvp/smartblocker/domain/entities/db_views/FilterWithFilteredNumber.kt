package com.tarasovvp.smartblocker.domain.entities.db_views

import android.os.Parcelable
import androidx.room.*
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.utils.extensions.*
import kotlinx.parcelize.Parcelize
import java.util.*

@DatabaseView("SELECT filters.*, (SELECT COUNT(*) FROM ContactWithFilter WHERE filter = filters.filter) filteredContacts FROM filters")
@Parcelize
data class FilterWithFilteredNumber(
    @Embedded
    var filter: Filter? = null,
    @ColumnInfo(name = "filteredContacts")
    var filteredContacts: Int? = 0
) : Parcelable