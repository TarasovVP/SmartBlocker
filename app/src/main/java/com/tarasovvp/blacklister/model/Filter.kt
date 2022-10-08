package com.tarasovvp.blacklister.model

import android.content.Context
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.BLACK_FILTER
import com.tarasovvp.blacklister.constants.Constants.DEFAULT_FILTER
import com.tarasovvp.blacklister.constants.Constants.WHITE_FILTER
import com.tarasovvp.blacklister.enums.Condition
import com.tarasovvp.blacklister.extensions.EMPTY
import com.tarasovvp.blacklister.extensions.isValidPhoneNumber
import com.tarasovvp.blacklister.extensions.nameInitial
import com.tarasovvp.blacklister.repository.CountryCodeRepository.extractedFilter
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class Filter(
    @PrimaryKey var filter: String = String.EMPTY,
    var conditionType: Int = DEFAULT_FILTER,
    var filterType: Int = DEFAULT_FILTER,
    var name: String? = String.EMPTY,
    var photoUrl: String? = String.EMPTY
) : Parcelable, BaseAdapter.MainData {
    var isCheckedForDelete = false
    var isDeleteMode = false
    var isFromDb = false

    fun isFilterEmpty(): Boolean {
        return filter.isEmpty()
    }

    fun filterTypeIcon(): Int {
        return when(filterType) {
            BLACK_FILTER -> R.drawable.ic_block
            WHITE_FILTER -> R.drawable.ic_accepted
            else -> 0
        }
    }

    fun filterToInput(context: Context): String {
        return context.extractedFilter(filter)
    }

    fun isValidPhoneNumber(context: Context): Boolean {
        return filter.isValidPhoneNumber(context)
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

    fun nameInitial(): String {
        return when {
            isTypeContain() -> String(Character.toChars(128269))
            isTypeStart() -> String(Character.toChars(127987))
            name.isNullOrEmpty() -> String(Character.toChars( 128222))
            else -> name.nameInitial()
        }
    }
}
