package com.tarasovvp.smartblocker.data.database.database_views

import android.content.Context
import android.os.Parcelable
import androidx.room.*
import com.google.firebase.database.Exclude
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.data.database.entities.CountryCode
import com.tarasovvp.smartblocker.data.database.entities.Filter
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.domain.models.NumberData
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
        entity = CountryCode::class
        )
    var countryCode: CountryCode? = null
) : Parcelable, NumberData() {
    @Exclude
    fun filterActionText(context: Context): String {
        return filter?.filterAction?.let { action ->
            context.getString(if (filter?.isInvalidFilterAction().isTrue()) {
                when {
                    filter?.isTypeContain().isTrue() && filter?.filter?.isEmpty().isTrue() -> R.string.filter_action_create_number_empty
                    filter?.isTypeFull().isTrue() && filter?.filter?.length.orZero() < countryCode?.numberFormat.digitsTrimmed().length -> R.string.filter_action_create_number_incomplete
                    else -> action.descriptionText
                }
            } else {
                action.descriptionText
            })
        }.orEmpty()
    }

    @Exclude
    fun conditionTypeFullHint(): String {
        return countryCode?.numberFormat?.replace(Regex("\\d"), Constants.MASK_CHAR.toString()).orEmpty()
    }

    @Exclude
    fun conditionTypeStartHint(): String {
        return countryCode?.numberFormat?.filter { it.isDigit() }
            ?.replace(Regex("\\d"), Constants.MASK_CHAR.toString())
            ?.replaceFirst(Constants.MASK_CHAR.toString(), String.EMPTY).orEmpty()
    }

    @Exclude
    fun createFilter(): String {
        return when {
            filter?.isTypeContain().isTrue() -> filter?.filter.orEmpty()
            else -> String.format("%s%s", countryCode?.countryCode, filterToInput())
        }
    }

    @Exclude
    fun createFilterValue(context: Context): String {
        return when {
            filter?.isTypeContain().isTrue() -> filter?.filter?.ifEmpty { context.getString(R.string.creating_filter_no_data) }.orEmpty()
            else -> String.format("%s%s", countryCode?.countryCode, filterToInput())
        }
    }

    @Exclude
    fun extractFilterWithoutCountryCode(): String {
        return when (filter?.filter) {
            createFilter() -> filter?.filter?.replace(countryCode?.countryCode.orEmpty(), String.EMPTY).orEmpty()
            else -> filter?.filter.orEmpty()
        }
    }

    @Exclude
    fun filterToInput(): String {
        return when (filter?.conditionType) {
            FilterCondition.FILTER_CONDITION_FULL.index -> if (filter?.filter.getPhoneNumber(countryCode?.country.orEmpty()).isNull())
                filter?.filter.digitsTrimmed().replace(Constants.PLUS_CHAR.toString(), String.EMPTY) else filter?.filter.getPhoneNumber(
                countryCode?.country.orEmpty())?.nationalNumber.toString()
            FilterCondition.FILTER_CONDITION_START.index -> filter?.filter?.replaceFirst(countryCode?.countryCode.orEmpty(), String.EMPTY).orEmpty()
            else -> filter?.filter.digitsTrimmed().replace(Constants.PLUS_CHAR.toString(), String.EMPTY)
        }
    }

    @Exclude
    fun isInValidPhoneNumber(): Boolean {
        return (filter?.isTypeFull().isTrue() && filter?.filter.isValidPhoneNumber(countryCode?.country.orEmpty()).not())
                || (filter?.isTypeStart().isTrue().not() && filter?.filter.orEmpty().isEmpty())
    }

}