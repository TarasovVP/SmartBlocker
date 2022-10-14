package com.tarasovvp.blacklister.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.BLACK_FILTER
import com.tarasovvp.blacklister.constants.Constants.COUNTRY_CODE_START
import com.tarasovvp.blacklister.constants.Constants.DEFAULT_FILTER
import com.tarasovvp.blacklister.constants.Constants.WHITE_FILTER
import com.tarasovvp.blacklister.database.CountryCodeConverter
import com.tarasovvp.blacklister.enums.Condition
import com.tarasovvp.blacklister.extensions.EMPTY
import com.tarasovvp.blacklister.extensions.digitsTrimmed
import com.tarasovvp.blacklister.extensions.getPhoneNumber
import com.tarasovvp.blacklister.extensions.isValidPhoneNumber
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
) : Parcelable, BaseAdapter.MainData {
    var isCheckedForDelete = false
    var isDeleteMode = false
    var isFromDb = false
    var searchText = String.EMPTY

    fun fullFilter(): String {
        return when {
            isTypeContain() -> filter
            else -> String.format(COUNTRY_CODE_START, countryCode.countryCode, filter)
        }
    }

    fun filterTypeIcon(): Int {
        return when (filterType) {
            BLACK_FILTER -> R.drawable.ic_block
            WHITE_FILTER -> R.drawable.ic_accepted
            else -> 0
        }
    }

    fun conditionTypeIcon(): Int {
        return when (conditionType) {
            Condition.CONDITION_TYPE_START.index -> R.drawable.ic_flag
            Condition.CONDITION_TYPE_CONTAIN.index -> R.drawable.ic_filter
            else -> R.drawable.ic_phone
        }
    }

    fun filterToInput(): String {
        return filter.getPhoneNumber(countryCode.country)?.nationalNumber?.toString() ?: filter.digitsTrimmed()
    }

    fun isInValidPhoneNumber(): Boolean {
        return (isTypeFull() && filter.isValidPhoneNumber(countryCode.country).not()) || (isTypeStart().not() && filter.isEmpty())
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
