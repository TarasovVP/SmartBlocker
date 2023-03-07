package com.tarasovvp.smartblocker.models

import android.os.Parcelable
import androidx.room.*
import kotlinx.parcelize.Parcelize

@Parcelize
data class FilterWithCountryCode(
    @Embedded
    var filter: Filter? = null,
    @Relation(
        parentColumn = "country",
        entityColumn = "country",
        entity = CountryCode::class
        )
    var countryCode: CountryCode? = null
) : Parcelable