package com.tarasovvp.blacklister.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
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
    var isCheckedForDelete = false
    var isDeleteMode = false
    var searchText = String.EMPTY

    fun addFilter(): String {
        return when {
            isTypeContain() -> filter
            else -> String.format("%s%s", countryCode.countryCode, filter)
        }
    }

    fun filterTypeIcon(): Int {
        return when (filterType) {
            BLACK_FILTER -> R.drawable.ic_block
            WHITE_FILTER -> R.drawable.ic_accepted
            else -> 0
        }
    }

    fun conditionTypeFullHint(): String {
        var hint = ""
        countryCode.numberExample.forEachIndexed { index, char ->
            if (index % 2 == 0 && index != 0 && index != countryCode.numberExample.lastIndex)
                hint += "$char-" else hint += char
        }
        return hint
    }

    fun conditionTypeIcon(): Int {
        return Condition.getIconByIndex(conditionType)
    }

    fun filterToInput(): String {
        return if (filter.getPhoneNumber(countryCode.country)
                .isNull()
        ) filter.digitsTrimmed() else filter.getPhoneNumber(countryCode.country)?.nationalNumber.toString()
    }

    fun isInValidPhoneNumber(): Boolean {
        return (isTypeFull() && filter.isValidPhoneNumber(countryCode.country)
            .not()) || (isTypeStart().not() && filter.isEmpty())
    }

    fun isTypeStart(): Boolean {
        return conditionType == Condition.CONDITION_TYPE_START.index
    }

    fun isTypeFull(): Boolean {
        return conditionType == Condition.CONDITION_TYPE_FULL.index
    }

    fun isTypeContain(): Boolean {
        return conditionType == Condition.CONDITION_TYPE_CONTAIN.index
    }

    fun isBlackFilter(): Boolean {
        return filterType == BLACK_FILTER
    }

    fun isWhiteFilter(): Boolean {
        return filterType == WHITE_FILTER
    }
}
