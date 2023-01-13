package com.tarasovvp.smartblocker.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.constants.Constants.DEFAULT_FILTER
import com.tarasovvp.smartblocker.constants.Constants.MASK_CHAR
import com.tarasovvp.smartblocker.constants.Constants.PERMISSION
import com.tarasovvp.smartblocker.constants.Constants.PLUS_CHAR
import com.tarasovvp.smartblocker.enums.FilterAction
import com.tarasovvp.smartblocker.enums.FilterCondition
import com.tarasovvp.smartblocker.extensions.*
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class Filter(
    @PrimaryKey var filter: String = String.EMPTY,
    var conditionType: Int = DEFAULT_FILTER,
    var filterType: Int = DEFAULT_FILTER,
    var name: String? = String.EMPTY,
    var countryCode: CountryCode = CountryCode(),
    var filterWithoutCountryCode: String = String.EMPTY,
) : Parcelable, NumberData() {

    @get:Exclude
    var filteredContacts: String = String.EMPTY

    @get:Exclude
    var filteredCalls: String = String.EMPTY

    @get:Exclude
    var isCheckedForDelete = false

    @get:Exclude
    var isDeleteMode = false

    @Ignore
    @get:Exclude
    var filterAction: FilterAction? = null

    @Exclude
    fun filterTypeTitle(): Int {
        return when (filterType) {
            PERMISSION -> R.string.filter_type_permission
            else -> R.string.filter_type_blocker
        }
    }

    @Exclude
    fun filterTypeIcon(): Int {
        return when (filterType) {
            PERMISSION -> R.drawable.ic_permission
            else -> R.drawable.ic_blocker
        }
    }

    @Exclude
    fun filterTypeTint(): Int {
        return when (filterType) {
            PERMISSION -> R.color.islamic_green
            else -> R.color.sunset
        }
    }

    @Exclude
    fun filterDetailTint(): Int {
        return filterAction?.color ?: filterTypeTint()
    }

    @Exclude
    fun filteredContactsText(): Int {
        return when (filterType) {
            PERMISSION -> R.string.details_number_permit_contacts
            else -> R.string.details_number_block_contacts
        }
    }

    @Exclude
    fun filteredCallsText(): Int {
        return when (filterType) {
            PERMISSION -> R.string.details_number_permittes_calls
            else -> R.string.details_number_blocked_calls
        }
    }

    @Exclude
    fun conditionTypeName(): Int {
        return FilterCondition.getTitleByIndex(conditionType)
    }

    @Exclude
    fun conditionTypeFullHint(): String {
        return countryCode.numberFormat.replace(Regex("[0-9]"), MASK_CHAR.toString())
    }

    @Exclude
    fun conditionTypeStartHint(): String {
        return countryCode.numberFormat.filter { it.isDigit() }
            .replace(Regex("[0-9]"), MASK_CHAR.toString())
            .replaceFirst(MASK_CHAR.toString(), String.EMPTY)
    }

    @Exclude
    fun conditionTypeIcon(): Int {
        return FilterCondition.getMainIconByIndex(conditionType)
    }

    @Exclude
    fun conditionTypeSmallIcon(): Int {
        return FilterCondition.getSmallIconByIndex(conditionType, isBlocker())
    }

    @Exclude
    fun addFilter(): String {
        return when {
            isTypeContain() -> filter
            else -> String.format("%s%s", countryCode.countryCode, filterToInput())
        }
    }

    @Exclude
    fun extractFilterWithoutCountryCode(): String {
        return when (filter) {
            addFilter() -> filter.replace(countryCode.countryCode, String.EMPTY)
            else -> filter
        }
    }

    @Exclude
    fun filterToInput(): String {
        return when (conditionType) {
            FilterCondition.FILTER_CONDITION_FULL.index -> if (filter.getPhoneNumber(countryCode.country)
                    .isNull()
            )
                filter.digitsTrimmed()
                    .replace(PLUS_CHAR.toString(), String.EMPTY) else filter.getPhoneNumber(
                countryCode.country)?.nationalNumber.toString()
            FilterCondition.FILTER_CONDITION_START.index -> filter.replaceFirst(countryCode.countryCode,
                String.EMPTY)
            else -> filter.digitsTrimmed().replace(PLUS_CHAR.toString(), String.EMPTY)
        }
    }

    @Exclude
    fun isInValidPhoneNumber(): Boolean {
        return (isTypeFull() && filter.isValidPhoneNumber(countryCode.country).not())
                || (isTypeStart().not() && filter.isEmpty())
    }

    @Exclude
    fun isInvalidFilterAction(): Boolean {
        return filterAction == FilterAction.FILTER_ACTION_INVALID
    }

    @Exclude
    fun isAddFilterAction(): Boolean {
        return filterAction == FilterAction.FILTER_ACTION_BLOCKER_CREATE || filterAction == FilterAction.FILTER_ACTION_PERMISSION_CREATE
    }

    @Exclude
    fun isChangeFilterAction(): Boolean {
        return filterAction == FilterAction.FILTER_ACTION_BLOCKER_TRANSFER || filterAction == FilterAction.FILTER_ACTION_PERMISSION_TRANSFER
    }

    @Exclude
    fun isTypeStart(): Boolean {
        return conditionType == FilterCondition.FILTER_CONDITION_START.index
    }

    @Exclude
    fun isTypeFull(): Boolean {
        return conditionType == FilterCondition.FILTER_CONDITION_FULL.index
    }

    @Exclude
    fun isTypeContain(): Boolean {
        return conditionType == FilterCondition.FILTER_CONDITION_CONTAIN.index
    }

    @Exclude
    fun isBlocker(): Boolean {
        return filterType == BLOCKER
    }

    @Exclude
    fun isPermission(): Boolean {
        return filterType == PERMISSION
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Filter) {
            this.filter == other.filter && this.conditionType == other.conditionType && this.filterType == other.filterType
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = filter.hashCode()
        result = 31 * result + conditionType
        result = 31 * result + filterType
        return result
    }
}
