package com.tarasovvp.smartblocker.models

import android.os.Parcelable
import androidx.room.DatabaseView
import androidx.room.Embedded
import androidx.room.Relation
import com.tarasovvp.smartblocker.extensions.*
import kotlinx.parcelize.Parcelize

@DatabaseView("SELECT * FROM contacts LEFT JOIN filters ON contacts.filter = filters.filter")
@Parcelize
data class ContactWithFilter(
    @Embedded
    var contact: Contact? = Contact(),
    @Relation(
        parentColumn = "filter",
        entityColumn = "filter"
    )
    var filter: Filter? = Filter()
) : Parcelable, NumberData()
