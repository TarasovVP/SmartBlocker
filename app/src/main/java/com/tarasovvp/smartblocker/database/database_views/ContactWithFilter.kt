package com.tarasovvp.smartblocker.database.database_views

import android.os.Parcelable
import androidx.room.DatabaseView
import androidx.room.Embedded
import androidx.room.Relation
import com.tarasovvp.smartblocker.extensions.*
import com.tarasovvp.smartblocker.database.entities.Contact
import com.tarasovvp.smartblocker.models.NumberData
import kotlinx.parcelize.Parcelize

@DatabaseView("SELECT * FROM contacts LEFT JOIN filters ON contacts.filter = filters.filter LEFT JOIN FilterWithCountryCode ON filters.filter = FilterWithCountryCode.filter")
@Parcelize
data class ContactWithFilter(
    @Embedded
    var contact: Contact? = Contact(),
    @Relation(
        parentColumn = "filter",
        entityColumn = "filter"
    )
    var filterWithCountryCode: FilterWithCountryCode? = FilterWithCountryCode()
) : Parcelable, NumberData()
