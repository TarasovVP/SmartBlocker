package com.tarasovvp.smartblocker.presentation.ui_models

import android.content.Context
import android.os.Parcelable
import androidx.room.*
import com.google.firebase.database.Exclude
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.domain.enums.FilterAction
import com.tarasovvp.smartblocker.utils.PhoneNumber
import com.tarasovvp.smartblocker.utils.extensions.*
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
data class FilterWithCountryCodeUIModel(
    var filterUIModel: FilterUIModel? = null,
    var filteredContacts: Int? = 0,
    var filteredCalls: Int? = 0,
    var countryCodeUIModel: CountryCodeUIModel? = null
) : Parcelable, NumberDataUIModel() {
    @Exclude
    fun filterActionText(context: Context): String {
        return filterAction?.let { action ->
            context.getString(if (isInvalidFilterAction()) {
                when {
                    isTypeContain().isTrue() && filterUIModel?.filter?.isEmpty().isTrue() -> R.string.filter_action_create_number_empty
                    isTypeFull().isTrue() && filterUIModel?.filter?.length.orZero() < countryCodeUIModel?.numberFormat.digitsTrimmed().length -> R.string.filter_action_create_number_incomplete
                    else -> action.descriptionText()
                }
            } else {
                action.descriptionText()
            })
        }.orEmpty()
    }

    @Exclude
    fun conditionTypeFullHint(): String {
        return countryCodeUIModel?.numberFormat?.replace(Regex("\\d"), Constants.MASK_CHAR.toString()).orEmpty()
    }

    @Exclude
    fun conditionTypeStartHint(): String {
        return countryCodeUIModel?.numberFormat?.filter { it.isDigit() }
            ?.replace(Regex("\\d"), Constants.MASK_CHAR.toString())
            ?.replaceFirst(Constants.MASK_CHAR.toString(), String.EMPTY).orEmpty()
    }

    @Exclude
    fun createFilter(): String {
        return when {
            isTypeContain().isTrue() -> filterUIModel?.filter.orEmpty()
            else -> String.format("%s%s", countryCodeUIModel?.countryCode, filterToInput())
        }
    }

    @Exclude
    fun createFilterValue(context: Context): String {
        return when {
            isTypeContain().isTrue() -> filterUIModel?.filter?.ifEmpty { context.getString(R.string.creating_filter_no_data) }.orEmpty()
            else -> String.format("%s%s", countryCodeUIModel?.countryCode, filterToInput())
        }
    }

    @Exclude
    fun extractFilterWithoutCountryCode(): String {
        return when (filterUIModel?.filter) {
            createFilter() -> filterUIModel?.filter?.replace(countryCodeUIModel?.countryCode.orEmpty(), String.EMPTY).orEmpty()
            else -> filterUIModel?.filter.orEmpty()
        }
    }

    @Exclude
    fun filterToInput(): String {
        //TODO
        val phoneNumber = PhoneNumber()
        return when (filterUIModel?.conditionType) {
            FilterCondition.FILTER_CONDITION_FULL.ordinal -> (if (phoneNumber.getPhoneNumber(filterUIModel?.filter, countryCodeUIModel?.country.orEmpty()).isNull()) phoneNumber.getPhoneNumber(filterUIModel?.filter.digitsTrimmed().replace(Constants.PLUS_CHAR.toString(), String.EMPTY), countryCodeUIModel?.country.orEmpty()) else phoneNumber.getPhoneNumber(filterUIModel?.filter, countryCodeUIModel?.country.orEmpty()))?.nationalNumber.toString()
            FilterCondition.FILTER_CONDITION_START.ordinal -> filterUIModel?.filter?.replaceFirst(countryCodeUIModel?.countryCode.orEmpty(), String.EMPTY).orEmpty()
            else -> filterUIModel?.filter.digitsTrimmed().replace(Constants.PLUS_CHAR.toString(), String.EMPTY)
        }
    }

    @Exclude
    fun isInValidPhoneNumber(phoneNumber: PhoneNumber): Boolean {
        return (isTypeFull() && phoneNumber.isPhoneNumberValid(filterUIModel?.filter, countryCodeUIModel?.country.orEmpty()))
                || (isTypeStart().not() && filterUIModel?.filter.orEmpty().isEmpty())
    }

    @IgnoredOnParcel
    @Ignore
    @get:Exclude
    var isCheckedForDelete = false

    @IgnoredOnParcel
    @Ignore
    @get:Exclude
    var isDeleteMode = false

    @IgnoredOnParcel
    @Ignore
    @get:Exclude
    var filterAction: FilterAction? = null

    @Exclude
    fun filterTypeTitle(): Int {
        return when (filterUIModel?.filterType) {
            Constants.PERMISSION -> R.string.filter_type_permission
            else -> R.string.filter_type_blocker
        }
    }

    @Exclude
    fun filterTypeIcon(): Int {
        return when (filterUIModel?.filterType) {
            Constants.PERMISSION -> R.drawable.ic_permission
            else -> R.drawable.ic_blocker
        }
    }

    @Exclude
    fun filterTypeTint(): Int {
        return when (filterUIModel?.filterType) {
            Constants.PERMISSION -> R.color.islamic_green
            else -> R.color.sunset
        }
    }

    @Exclude
    fun filterDetailTint(): Int {
        return if (isDeleteFilterAction()) R.color.sunset else R.color.text_color_grey
    }

    @Exclude
    fun filterActionTextTint(): Int {
        return if (isCreateFilterAction()) R.color.white else filterAction?.color() ?: R.color.white
    }

    @Exclude
    fun filterActionBgTint(): Int {
        return if (isCreateFilterAction()) R.color.button_bg else R.color.transparent
    }

    @Exclude
    fun filteredContactsText(context: Context): String {
        return context.resources.getQuantityString(when (filterUIModel?.filterType) {
            Constants.PERMISSION -> R.plurals.details_number_permit_contacts
            else -> R.plurals.details_number_block_contacts
        }, filteredContacts?.quantityString().orZero(), filteredContacts)
    }

    @Exclude
    fun filteredCallsText(context: Context): String {
        return context.resources.getQuantityString(when (filterUIModel?.filterType) {
            Constants.PERMISSION -> R.plurals.details_number_permitted_calls
            else -> R.plurals.details_number_blocked_calls
        }, filteredCalls?.quantityString().orZero(), filteredCalls)
    }

    @Exclude
    fun conditionTypeName(): Int {
        return FilterCondition.values()[filterUIModel?.conditionType.orZero()].title()
    }

    @Exclude
    fun conditionTypeIcon(): Int {
        return FilterCondition.values()[filterUIModel?.conditionType.orZero()].mainIcon()
    }

    @Exclude
    fun conditionTypeSmallIcon(): Int? {
        return filterUIModel?.conditionType?.takeIf { it >= 0 }?.let { FilterCondition.values()[filterUIModel?.conditionType.orZero()].smallIcon(isBlocker()) }
    }

    @Exclude
    fun filterCreatedDate(): String {
        return filterUIModel?.created?.let { SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault()).format(it) }
            .orEmpty()
    }

    @Exclude
    fun isInvalidFilterAction(): Boolean {
        return filterAction == FilterAction.FILTER_ACTION_INVALID
    }

    @Exclude
    fun isCreateFilterAction(): Boolean {
        return filterAction == FilterAction.FILTER_ACTION_BLOCKER_CREATE || filterAction == FilterAction.FILTER_ACTION_PERMISSION_CREATE
    }

    @Exclude
    fun isDeleteFilterAction(): Boolean {
        return filterAction == FilterAction.FILTER_ACTION_BLOCKER_DELETE || filterAction == FilterAction.FILTER_ACTION_PERMISSION_DELETE
    }

    @Exclude
    fun isChangeFilterAction(): Boolean {
        return filterAction == FilterAction.FILTER_ACTION_BLOCKER_TRANSFER || filterAction == FilterAction.FILTER_ACTION_PERMISSION_TRANSFER
    }

    @Exclude
    fun isTypeStart(): Boolean {
        return filterUIModel?.conditionType == FilterCondition.FILTER_CONDITION_START.ordinal
    }

    @Exclude
    fun isTypeFull(): Boolean {
        return filterUIModel?.conditionType == FilterCondition.FILTER_CONDITION_FULL.ordinal
    }

    @Exclude
    fun isTypeContain(): Boolean {
        return filterUIModel?.conditionType == FilterCondition.FILTER_CONDITION_CONTAIN.ordinal
    }

    @Exclude
    fun isBlocker(): Boolean {
        return filterUIModel?.filterType == Constants.BLOCKER
    }

    @Exclude
    fun isPermission(): Boolean {
        return filterUIModel?.filterType == Constants.PERMISSION
    }
}