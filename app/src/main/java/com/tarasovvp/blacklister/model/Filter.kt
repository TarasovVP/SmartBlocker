package com.tarasovvp.blacklister.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.BLACK_FILTER
import com.tarasovvp.blacklister.constants.Constants.DEFAULT_FILTER
import com.tarasovvp.blacklister.constants.Constants.WHITE_FILTER
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
    var countryCode: CountryCode = CountryCode(),
    var isFromDb: Boolean = false,
) : Parcelable, BaseAdapter.MainData {
    @get:Exclude var isCheckedForDelete = false
    @get:Exclude var isDeleteMode = false
    @get:Exclude var searchText = String.EMPTY

    @Exclude fun addFilter(): String {
        return when {
            isTypeContain() -> filter
            else -> String.format("%s%s", countryCode.countryCode, filter)
        }
    }

    @Exclude fun filterTypeIcon(): Int {
        return when (filterType) {
            BLACK_FILTER -> R.drawable.ic_block
            WHITE_FILTER -> R.drawable.ic_accepted
            else -> 0
        }
    }

    @Exclude fun conditionTypeFullHint(): String {
        return countryCode.numberFormat.replace(Regex("[0-9]"), "#")
    }

    @Exclude fun conditionTypeIcon(): Int {
        return Condition.getIconByIndex(conditionType)
    }

    @Exclude fun filterToInput(): String {
        return if (filter.getPhoneNumber(countryCode.country)
                .isNull()
        ) filter.digitsTrimmed() else filter.getPhoneNumber(countryCode.country)?.nationalNumber.toString()
    }

    @Exclude fun isInValidPhoneNumber(): Boolean {
        return (isTypeFull() && filter.isValidPhoneNumber(countryCode.country)
            .not()) || (isTypeStart().not() && filter.isEmpty())
    }

    @Exclude fun isTypeStart(): Boolean {
        return conditionType == Condition.CONDITION_TYPE_START.index
    }

    @Exclude fun isTypeFull(): Boolean {
        return conditionType == Condition.CONDITION_TYPE_FULL.index
    }

    @Exclude fun isTypeContain(): Boolean {
        return conditionType == Condition.CONDITION_TYPE_CONTAIN.index
    }

    @Exclude fun isBlackFilter(): Boolean {
        return filterType == BLACK_FILTER
    }

    @Exclude fun isWhiteFilter(): Boolean {
        return filterType == WHITE_FILTER
    }
}
