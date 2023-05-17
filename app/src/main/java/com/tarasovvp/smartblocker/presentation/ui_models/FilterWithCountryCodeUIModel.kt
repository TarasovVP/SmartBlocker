package com.tarasovvp.smartblocker.presentation.ui_models

import android.content.Context
import android.os.Parcelable
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.domain.enums.FilterAction
import com.tarasovvp.smartblocker.utils.PhoneNumberUtil
import com.tarasovvp.smartblocker.utils.extensions.*
import kotlinx.parcelize.Parcelize

@Parcelize
data class FilterWithCountryCodeUIModel(
    var filterWithFilteredNumberUIModel: FilterWithFilteredNumberUIModel = FilterWithFilteredNumberUIModel(),
    var countryCodeUIModel: CountryCodeUIModel = CountryCodeUIModel()
) : Parcelable {

    fun filterActionText(context: Context): String {
        return filterWithFilteredNumberUIModel.filterAction?.let { action ->
            context.getString(if (isInvalidFilterAction()) {
                when {
                    filterWithFilteredNumberUIModel.isTypeContain() && filterWithFilteredNumberUIModel.filter.isEmpty() -> R.string.filter_action_create_number_empty
                    filterWithFilteredNumberUIModel.isTypeFull() && filterWithFilteredNumberUIModel.filter.length < countryCodeUIModel.numberFormat.digitsTrimmed().length -> R.string.filter_action_create_number_incomplete
                    else -> action.descriptionText()
                }
            } else {
                action.descriptionText()
            })
        }.orEmpty()
    }

    fun conditionTypeFullHint(): String {
        return countryCodeUIModel.numberFormat.replace(Regex("\\d"), Constants.MASK_CHAR.toString())
    }

    fun conditionTypeStartHint(): String {
        return countryCodeUIModel.numberFormat.filter { it.isDigit() }
            .replace(Regex("\\d"), Constants.MASK_CHAR.toString())
            .replaceFirst(Constants.MASK_CHAR.toString(), String.EMPTY)
    }

    fun createFilter(): String {
        return when {
            filterWithFilteredNumberUIModel.isTypeContain() -> filterWithFilteredNumberUIModel.filter
            else -> String.format("%s%s", countryCodeUIModel.countryCode, filterToInput())
        }
    }

    fun createFilterValue(context: Context): String {
        return when {
            filterWithFilteredNumberUIModel.isTypeContain().isTrue() -> filterWithFilteredNumberUIModel.filter.ifEmpty { context.getString(R.string.creating_filter_no_data) }
            else -> String.format("%s%s", countryCodeUIModel.countryCode, filterToInput())
        }
    }

    fun extractFilterWithoutCountryCode(): String {
        return when (filterWithFilteredNumberUIModel.filter) {
            createFilter() -> filterWithFilteredNumberUIModel.filter.replace(countryCodeUIModel.countryCode.orEmpty(), String.EMPTY)
            else -> filterWithFilteredNumberUIModel.filter
        }
    }

    fun filterToInput(): String {
        //TODO
        val phoneNumberUtil = PhoneNumberUtil()
        return when (filterWithFilteredNumberUIModel.conditionType) {
            FilterCondition.FILTER_CONDITION_FULL.ordinal -> (if (phoneNumberUtil.getPhoneNumber(
                    filterWithFilteredNumberUIModel.filter, countryCodeUIModel.country).isNull()) phoneNumberUtil.getPhoneNumber(filterWithFilteredNumberUIModel?.filter.digitsTrimmed().replace(Constants.PLUS_CHAR.toString(), String.EMPTY), countryCodeUIModel?.country.orEmpty()) else phoneNumberUtil.getPhoneNumber(filterWithFilteredNumberUIModel?.filter, countryCodeUIModel?.country.orEmpty()))?.nationalNumber.toString()
            FilterCondition.FILTER_CONDITION_START.ordinal -> filterWithFilteredNumberUIModel.filter?.replaceFirst(
                countryCodeUIModel.countryCode, String.EMPTY).orEmpty()
            else -> filterWithFilteredNumberUIModel.filter.digitsTrimmed().replace(Constants.PLUS_CHAR.toString(), String.EMPTY)
        }
    }

    fun isInValidPhoneNumber(phoneNumberUtil: PhoneNumberUtil): Boolean {
        return (filterWithFilteredNumberUIModel.isTypeFull() && phoneNumberUtil.isPhoneNumberValid(filterWithFilteredNumberUIModel.filter, countryCodeUIModel.country))
                || (filterWithFilteredNumberUIModel.isTypeStart().not() && filterWithFilteredNumberUIModel.filter.isEmpty())
    }

    fun filterDetailTint(): Int {
        return if (isDeleteFilterAction()) R.color.sunset else R.color.text_color_grey
    }

    fun filterActionTextTint(): Int {
        return if (isCreateFilterAction()) R.color.white else filterWithFilteredNumberUIModel.filterAction?.color() ?: R.color.white
    }

    fun filterActionBgTint(): Int {
        return if (isCreateFilterAction()) R.color.button_bg else R.color.transparent
    }

    fun isInvalidFilterAction(): Boolean {
        return filterWithFilteredNumberUIModel.filterAction == FilterAction.FILTER_ACTION_INVALID
    }

    fun isCreateFilterAction(): Boolean {
        return filterWithFilteredNumberUIModel.filterAction == FilterAction.FILTER_ACTION_BLOCKER_CREATE || filterWithFilteredNumberUIModel.filterAction == FilterAction.FILTER_ACTION_PERMISSION_CREATE
    }

    fun isDeleteFilterAction(): Boolean {
        return filterWithFilteredNumberUIModel.filterAction == FilterAction.FILTER_ACTION_BLOCKER_DELETE || filterWithFilteredNumberUIModel.filterAction == FilterAction.FILTER_ACTION_PERMISSION_DELETE
    }
}