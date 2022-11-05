package com.tarasovvp.blacklister.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.BLACK_FILTER
import com.tarasovvp.blacklister.constants.Constants.DEFAULT_FILTER
import com.tarasovvp.blacklister.constants.Constants.HASH_CHAR
import com.tarasovvp.blacklister.constants.Constants.PLUS_CHAR
import com.tarasovvp.blacklister.constants.Constants.WHITE_FILTER
import com.tarasovvp.blacklister.enums.FilterCondition
import com.tarasovvp.blacklister.enums.FilterAction
import com.tarasovvp.blacklister.extensions.*
import com.tarasovvp.blacklister.ui.number_data.NumberData
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class Filter(
    @PrimaryKey var filter: String = String.EMPTY,
    var conditionType: Int = DEFAULT_FILTER,
    var filterType: Int = DEFAULT_FILTER,
    var name: String? = String.EMPTY,
    var countryCode: CountryCode = CountryCode(),
) : Parcelable, NumberData {
    @get:Exclude
    var isCheckedForDelete = false

    @get:Exclude
    var isDeleteMode = false

    @get:Exclude
    var searchText = String.EMPTY

    @get:Exclude
    var filterAction: FilterAction? = null

    @Exclude
    fun filterTypeTitle(): Int {
        return when (filterType) {
            WHITE_FILTER -> R.string.allow
            else -> R.string.blocker
        }
    }

    @Exclude
    fun filterTypeIcon(): Int {
        return when (filterType) {
            BLACK_FILTER -> R.drawable.ic_black_filter
            WHITE_FILTER -> R.drawable.ic_white_filter
            else -> 0
        }
    }

    @Exclude
    fun conditionTypeName(): Int {
        return FilterCondition.getTitleByIndex(conditionType)
    }

    @Exclude
    fun conditionTypeFullHint(): String {
        return countryCode.numberFormat.replace(Regex("[0-9]"), HASH_CHAR.toString())
    }

    @Exclude
    fun conditionTypeStartHint(): String {
        return countryCode.numberFormat.filter { it.isDigit() }
            .replace(Regex("[0-9]"), HASH_CHAR.toString())
            .replaceFirst(HASH_CHAR.toString(), String.EMPTY)
    }

    @Exclude
    fun conditionTypeIcon(): Int {
        return FilterCondition.getMainIconByIndex(conditionType)
    }

    @Exclude
    fun conditionTypeDescription() = when (conditionType) {
        FilterCondition.FILTER_CONDITION_FULL.index -> R.string.filter_full_number_description
        FilterCondition.FILTER_CONDITION_START.index -> R.string.filter_start_description
        else -> R.string.filter_contain_description
    }

    @Exclude
    fun addFilter(): String {
        return when {
            isTypeContain() -> filter
            else -> String.format("%s%s", countryCode.countryCode, filterToInput())
        }
    }

    @Exclude
    fun filterActionIcon() = when (filterAction) {
        FilterAction.FILTER_ACTION_INVALID -> if (isBlackFilter()) R.drawable.ic_black_filter_inactive else R.drawable.ic_white_filter_inactive
        FilterAction.FILTER_ACTION_CHANGE -> if (isBlackFilter()) R.drawable.ic_white_to_black_filter else R.drawable.ic_black_to_white_filter
        else -> filterTypeIcon()
    }

    @Exclude
    fun filterActionDescription() = when (filterAction) {
        FilterAction.FILTER_ACTION_CHANGE -> if (isBlackFilter()) R.string.filter_description_change_blocker else R.string.filter_description_change_allow
        FilterAction.FILTER_ACTION_DELETE -> if (isBlackFilter()) R.string.filter_description_delete_blocker else R.string.filter_description_delete_allow
        FilterAction.FILTER_ACTION_INVALID -> R.string.filter_description_invalid
        else -> if (isBlackFilter()) R.string.filter_description_add_blocker else R.string.filter_description_add_allow
    }

    @Exclude
    fun filterActionSuccessText() = when (filterAction) {
        FilterAction.FILTER_ACTION_ADD -> R.string.filter_added
        FilterAction.FILTER_ACTION_CHANGE -> R.string.filter_update
        FilterAction.FILTER_ACTION_DELETE -> R.string.delete_filter_from_list
        else -> R.string.empty
    }

    @Exclude
    fun filterToInput(): String {
        return when (conditionType) {
            FilterCondition.FILTER_CONDITION_FULL.index -> if (filter.getPhoneNumber(countryCode.country).isNull())
                filter.digitsTrimmed().replace(PLUS_CHAR.toString(), String.EMPTY) else filter.getPhoneNumber(countryCode.country)?.nationalNumber.toString()
            FilterCondition.FILTER_CONDITION_START.index -> filter.replaceFirst(countryCode.countryCode, String.EMPTY)
            else -> filter.digitsTrimmed().replace(PLUS_CHAR.toString(), String.EMPTY)
        }
    }

    @Exclude
    fun isInValidPhoneNumber(): Boolean {
        return (isTypeFull() && filter.isValidPhoneNumber(countryCode.country).not())
                || (isTypeStart().not() && filter.isEmpty())
    }

    @Exclude
    fun isInValidAddFilterState(): Boolean {
        return filterAction == FilterAction.FILTER_ACTION_INVALID
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
    fun isBlackFilter(): Boolean {
        return filterType == BLACK_FILTER
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
