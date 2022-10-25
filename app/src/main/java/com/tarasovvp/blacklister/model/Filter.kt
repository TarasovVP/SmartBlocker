package com.tarasovvp.blacklister.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.BLACK_FILTER
import com.tarasovvp.blacklister.constants.Constants.DEFAULT_FILTER
import com.tarasovvp.blacklister.constants.Constants.HASH_CHAR
import com.tarasovvp.blacklister.constants.Constants.WHITE_FILTER
import com.tarasovvp.blacklister.enums.AddFilterState
import com.tarasovvp.blacklister.enums.Condition
import com.tarasovvp.blacklister.extensions.*
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class Filter(
    @PrimaryKey var filter: String = String.EMPTY,
    var conditionType: Int = DEFAULT_FILTER,
    var filterType: Int = DEFAULT_FILTER,
    var name: String? = String.EMPTY,
    var countryCode: CountryCode = CountryCode()
) : Parcelable, BaseAdapter.NumberData {
    @get:Exclude
    var isCheckedForDelete = false

    @get:Exclude
    var isDeleteMode = false

    @get:Exclude
    var searchText = String.EMPTY

    @get:Exclude
    var addFilterState: AddFilterState? = null

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
        return Condition.getTitleByIndex(conditionType)
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
        return Condition.getIconByIndex(conditionType)
    }

    @Exclude
    fun addFilter(): String {
        return when {
            isTypeContain() -> filter
            else -> String.format("%s%s", countryCode.countryCode, filterToInput())
        }
    }

    @Exclude
    fun addFilterStateIcon() = when (addFilterState) {
        AddFilterState.ADD_FILTER_INVALID -> if (isBlackFilter()) R.drawable.ic_black_filter_inactive else R.drawable.ic_white_filter_inactive
        AddFilterState.ADD_FILTER_CHANGE -> if (isBlackFilter()) R.drawable.ic_white_to_black_filter else R.drawable.ic_black_to_white_filter
        else -> filterTypeIcon()
    }

    @Exclude
    fun addFilterStateDescription() = when (addFilterState) {
        AddFilterState.ADD_FILTER_CHANGE -> if (isBlackFilter()) R.string.filter_description_change_blocker else R.string.filter_description_change_allow
        AddFilterState.ADD_FILTER_DELETE -> if (isBlackFilter()) R.string.filter_description_delete_blocker else R.string.filter_description_delete_allow
        AddFilterState.ADD_FILTER_INVALID -> R.string.filter_description_invalid
        else ->  if (isBlackFilter()) R.string.filter_description_add_blocker else R.string.filter_description_add_allow
    }

    @Exclude
    fun conditionTypeDescription() = when (conditionType) {
        Condition.CONDITION_TYPE_FULL.index -> R.string.filter_full_number_description
        Condition.CONDITION_TYPE_START.index -> R.string.filter_start_description
        else ->  R.string.filter_contain_description
    }

    @Exclude
    fun filterToInput(): String {
        return when(conditionType) {
            Condition.CONDITION_TYPE_FULL.index -> if (filter.getPhoneNumber(countryCode.country).isNull())
                filter.digitsTrimmed() else filter.getPhoneNumber(countryCode.country)?.nationalNumber.toString()
            Condition.CONDITION_TYPE_START.index -> filter.replace(countryCode.countryCode, String.EMPTY)
            else -> filter.digitsTrimmed()
        }
    }

    @Exclude
    fun isInValidPhoneNumber(): Boolean {
        return (isTypeFull() && filter.isValidPhoneNumber(countryCode.country).not())
                || (isTypeStart().not() && filter.isEmpty())
    }

    @Exclude
    fun isInValidAddFilterState(): Boolean {
        return addFilterState == AddFilterState.ADD_FILTER_INVALID
    }

    @Exclude
    fun isTypeStart(): Boolean {
        return conditionType == Condition.CONDITION_TYPE_START.index
    }

    @Exclude
    fun isTypeFull(): Boolean {
        return conditionType == Condition.CONDITION_TYPE_FULL.index
    }

    @Exclude
    fun isTypeContain(): Boolean {
        return conditionType == Condition.CONDITION_TYPE_CONTAIN.index
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
