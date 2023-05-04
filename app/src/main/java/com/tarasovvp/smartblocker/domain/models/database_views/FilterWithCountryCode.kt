package com.tarasovvp.smartblocker.domain.models.database_views

import android.os.Parcelable
import androidx.room.*
import com.google.firebase.database.Exclude
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.domain.models.entities.CountryCode
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.utils.extensions.*
import kotlinx.parcelize.Parcelize

@DatabaseView("SELECT * FROM filters LEFT JOIN country_codes ON filters.country = country_codes.country")
@Parcelize
data class FilterWithCountryCode(
    @Embedded
    var filter: Filter? = null,
    @Relation(
        parentColumn = "country",
        entityColumn = "country",
        entity = CountryCode::class)
    var countryCode: CountryCode? = null
) : Parcelable {

    @Exclude
    fun filterToInput(): String {
        return when (filter?.conditionType) {
            FilterCondition.FILTER_CONDITION_FULL.ordinal -> if (filter?.filter.getPhoneNumber(countryCode?.country.orEmpty()).isNull())
                filter?.filter.digitsTrimmed().replace(Constants.PLUS_CHAR.toString(), String.EMPTY) else filter?.filter.getPhoneNumber(
                countryCode?.country.orEmpty())?.nationalNumber.toString()
            FilterCondition.FILTER_CONDITION_START.ordinal -> filter?.filter?.replaceFirst(countryCode?.countryCode.orEmpty(), String.EMPTY).orEmpty()
            else -> filter?.filter.digitsTrimmed().replace(Constants.PLUS_CHAR.toString(), String.EMPTY)
        }
    }
}