package com.tarasovvp.smartblocker.models

import android.os.Parcelable
import androidx.room.DatabaseView
import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.parcelize.Parcelize

@Parcelize
open class CallWithFilter(
    @Embedded
    var call: Call? = Call(),
    @Relation(
        parentColumn = "filter",
        entityColumn = "filter"
    )
    var filter: Filter? = Filter()
) : Parcelable, NumberData()