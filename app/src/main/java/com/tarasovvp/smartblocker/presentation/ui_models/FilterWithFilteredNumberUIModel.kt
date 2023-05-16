package com.tarasovvp.smartblocker.presentation.ui_models

import android.content.Context
import android.os.Parcelable
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.utils.extensions.*
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
data class FilterWithFilteredNumberUIModel(
    var filter: String = String.EMPTY,
    var conditionType: Int = Constants.DEFAULT_FILTER,
    var filterType: Int = Constants.DEFAULT_FILTER,
    var filterName: String = String.EMPTY,
    var countryCode: String = String.EMPTY,
    var country: String = String.EMPTY,
    var created: Long = 0,
    var filteredContacts: Int = 0,
    var filteredCalls: Int = 0
) : Parcelable, NumberDataUIModel() {

    @IgnoredOnParcel
    var isCheckedForDelete = false

    @IgnoredOnParcel
    var isDeleteMode = false

    fun filterTypeTitle(): Int {
        return when (filterType) {
            Constants.PERMISSION -> R.string.filter_type_permission
            else -> R.string.filter_type_blocker
        }
    }

    fun filterTypeIcon(): Int {
        return when (filterType) {
            Constants.PERMISSION -> R.drawable.ic_permission
            else -> R.drawable.ic_blocker
        }
    }

    fun filterTypeTint(): Int {
        return when (filterType) {
            Constants.PERMISSION -> R.color.islamic_green
            else -> R.color.sunset
        }
    }

    fun filteredContactsText(context: Context): String {
        return context.resources.getQuantityString(when (filterType) {
            Constants.PERMISSION -> R.plurals.details_number_permit_contacts
            else -> R.plurals.details_number_block_contacts
        }, filteredContacts.quantityString(), filteredContacts)
    }

    fun filteredCallsText(context: Context): String {
        return context.resources.getQuantityString(when (filterType) {
            Constants.PERMISSION -> R.plurals.details_number_permitted_calls
            else -> R.plurals.details_number_blocked_calls
        }, filteredCalls.quantityString(), filteredCalls)
    }

    fun conditionTypeName(): Int {
        return FilterCondition.values()[conditionType].title()
    }

    fun conditionTypeIcon(): Int {
        return FilterCondition.values()[conditionType].mainIcon()
    }

    fun conditionTypeSmallIcon(): Int? {
        return conditionType.takeIf { it >= 0 }?.let { FilterCondition.values()[conditionType].smallIcon(isBlocker()) }
    }

    fun filterCreatedDate(): String {
        return SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault()).format(created)
    }

    fun isTypeStart(): Boolean {
        return conditionType == FilterCondition.FILTER_CONDITION_START.ordinal
    }

    fun isTypeFull(): Boolean {
        return conditionType == FilterCondition.FILTER_CONDITION_FULL.ordinal
    }

    fun isTypeContain(): Boolean {
        return conditionType == FilterCondition.FILTER_CONDITION_CONTAIN.ordinal
    }

    fun isBlocker(): Boolean {
        return filterType == Constants.BLOCKER
    }

    fun isPermission(): Boolean {
        return filterType == Constants.PERMISSION
    }
}