package com.tarasovvp.smartblocker.presentation.ui_models

import android.content.Context
import android.os.Parcelable
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.utils.extensions.*
import kotlinx.parcelize.Parcelize

@Parcelize
data class FilterWithCountryCodeUIModel(
    var filterUIModel: FilterUIModel? = null,
    var countryCodeUIModel: CountryCodeUIModel? = null
) : Parcelable, NumberDataUIModel() {

    fun filterActionText(context: Context): String {
        return filterUIModel?.filterAction?.let { action ->
            context.getString(if (filterUIModel?.isInvalidFilterAction().isTrue()) {
                when {
                    filterUIModel?.isTypeContain().isTrue() && filterUIModel?.filter?.isEmpty().isTrue() -> R.string.filter_action_create_number_empty
                    filterUIModel?.isTypeFull().isTrue() && filterUIModel?.filter?.length.orZero() < countryCodeUIModel?.numberFormat.digitsTrimmed().length -> R.string.filter_action_create_number_incomplete
                    else -> action.descriptionText()
                }
            } else {
                action.descriptionText()
            })
        }.orEmpty()
    }

    fun conditionTypeFullHint(): String {
        return countryCodeUIModel?.numberFormat?.replace(Regex("\\d"), Constants.MASK_CHAR.toString()).orEmpty()
    }

    fun conditionTypeStartHint(): String {
        return countryCodeUIModel?.numberFormat?.filter { it.isDigit() }
            ?.replace(Regex("\\d"), Constants.MASK_CHAR.toString())
            ?.replaceFirst(Constants.MASK_CHAR.toString(), String.EMPTY).orEmpty()
    }

    fun createFilter(): String {
        return when {
            filterUIModel?.isTypeContain().isTrue() -> filterUIModel?.filter.orEmpty()
            else -> String.format("%s%s", countryCodeUIModel?.countryCode, filterToInput())
        }
    }

    fun createFilterValue(context: Context): String {
        return when {
            filterUIModel?.isTypeContain().isTrue() -> filterUIModel?.filter?.ifEmpty { context.getString(R.string.creating_filter_no_data) }.orEmpty()
            else -> String.format("%s%s", countryCodeUIModel?.countryCode, filterToInput())
        }
    }

    fun extractFilterWithoutCountryCode(): String {
        return when (filterUIModel?.filter) {
            createFilter() -> filterUIModel?.filter?.replace(countryCodeUIModel?.countryCode.orEmpty(), String.EMPTY).orEmpty()
            else -> filterUIModel?.filter.orEmpty()
        }
    }

    fun filterToInput(): String {
        return when (filterUIModel?.conditionType) {
            FilterCondition.FILTER_CONDITION_FULL.ordinal -> if (filterUIModel?.filter.getPhoneNumber(countryCodeUIModel?.country.orEmpty()).isNull())
                filterUIModel?.filter.digitsTrimmed().replace(Constants.PLUS_CHAR.toString(), String.EMPTY) else filterUIModel?.filter.getPhoneNumber(
                countryCodeUIModel?.country.orEmpty())?.nationalNumber.toString()
            FilterCondition.FILTER_CONDITION_START.ordinal -> filterUIModel?.filter?.replaceFirst(countryCodeUIModel?.countryCode.orEmpty(), String.EMPTY).orEmpty()
            else -> filterUIModel?.filter.digitsTrimmed().replace(Constants.PLUS_CHAR.toString(), String.EMPTY)
        }
    }

    fun isInValidPhoneNumber(): Boolean {
        return (filterUIModel?.isTypeFull().isTrue() && filterUIModel?.filter.isValidPhoneNumber(countryCodeUIModel?.country.orEmpty()).not())
                || (filterUIModel?.isTypeStart().isTrue().not() && filterUIModel?.filter.orEmpty().isEmpty())
    }

}