package com.tarasovvp.smartblocker.domain.entities.db_views

import android.os.Parcelable
import androidx.room.DatabaseView
import androidx.room.Embedded
import com.tarasovvp.smartblocker.domain.entities.db_entities.Contact
import kotlinx.parcelize.Parcelize

@DatabaseView("SELECT contacts.*, filters.* FROM contacts " +
        "LEFT JOIN filters ON (filters.filter = contacts.phoneNumberValue AND filters.conditionType = 0) OR (contacts.phoneNumberValue LIKE filters.filter || '%' AND filters.conditionType = 1) OR (contacts.phoneNumberValue LIKE '%' || filters.filter || '%' AND filters.conditionType = 2) " +
        "WHERE filters.filter = (SELECT filter FROM filters WHERE contacts.phoneNumberValue LIKE filter || '%' OR contacts.phoneNumberValue LIKE '%' || filter || '%' ORDER BY LENGTH(filter) DESC LIMIT 1) OR filters.filter IS NULL")
@Parcelize
data class ContactWithFilter(
    @Embedded
    var contact: Contact? = Contact(),
    @Embedded
    var filterWithFilteredNumber: FilterWithFilteredNumber? = FilterWithFilteredNumber()
) : Parcelable
