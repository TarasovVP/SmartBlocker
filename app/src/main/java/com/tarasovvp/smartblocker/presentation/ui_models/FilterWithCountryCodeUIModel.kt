package com.tarasovvp.smartblocker.presentation.ui_models

import android.content.Context
import android.os.Parcelable
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.enums.FilterAction
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.utils.AppPhoneNumberUtil
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.digitsTrimmed
import com.tarasovvp.smartblocker.utils.extensions.isNull
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

    fun filterToInput(): String {
        //TODO
        val appPhoneNumberUtil = AppPhoneNumberUtil()
        return when (filterWithFilteredNumberUIModel.conditionType) {
            FilterCondition.FILTER_CONDITION_FULL.ordinal -> (if (appPhoneNumberUtil.getPhoneNumber(
                    filterWithFilteredNumberUIModel.filter, countryCodeUIModel.country).isNull()) appPhoneNumberUtil.getPhoneNumber(
                filterWithFilteredNumberUIModel.filter.digitsTrimmed().replace(Constants.PLUS_CHAR.toString(), String.EMPTY),
                countryCodeUIModel.country
            ) else appPhoneNumberUtil.getPhoneNumber(
                filterWithFilteredNumberUIModel.filter, countryCodeUIModel.country
            ))?.nationalNumber.toString()
            FilterCondition.FILTER_CONDITION_START.ordinal -> filterWithFilteredNumberUIModel.filter.replaceFirst(
                countryCodeUIModel.countryCode, String.EMPTY)
            else -> filterWithFilteredNumberUIModel.filter.digitsTrimmed().replace(Constants.PLUS_CHAR.toString(), String.EMPTY)
        }
    }

    fun isInValidPhoneNumber(appPhoneNumberUtil: AppPhoneNumberUtil): Boolean {
        return (filterWithFilteredNumberUIModel.isTypeFull() /*&& appPhoneNumberUtil.isPhoneNumberValid(filterWithFilteredNumberUIModel.filter, countryCodeUIModel.country)*/)
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

    private fun isCreateFilterAction(): Boolean {
        return filterWithFilteredNumberUIModel.filterAction == FilterAction.FILTER_ACTION_BLOCKER_CREATE || filterWithFilteredNumberUIModel.filterAction == FilterAction.FILTER_ACTION_PERMISSION_CREATE
    }

    private fun isDeleteFilterAction(): Boolean {
        return filterWithFilteredNumberUIModel.filterAction == FilterAction.FILTER_ACTION_BLOCKER_DELETE || filterWithFilteredNumberUIModel.filterAction == FilterAction.FILTER_ACTION_PERMISSION_DELETE
    }
}