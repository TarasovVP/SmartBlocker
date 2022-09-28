package com.tarasovvp.blacklister.model

import android.content.Context
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.BLACK_FILTER
import com.tarasovvp.blacklister.constants.Constants.CONDITION_TYPE_CONTAIN
import com.tarasovvp.blacklister.constants.Constants.CONDITION_TYPE_FULL
import com.tarasovvp.blacklister.constants.Constants.CONDITION_TYPE_START
import com.tarasovvp.blacklister.constants.Constants.DEFAULT_FILTER
import com.tarasovvp.blacklister.constants.Constants.WHITE_FILTER
import com.tarasovvp.blacklister.extensions.EMPTY
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.utils.PhoneNumberUtil.extractedFilter
import com.tarasovvp.blacklister.utils.PhoneNumberUtil.isValidPhoneNumber
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class Filter(
    @PrimaryKey var filter: String = "",
    var conditionType: Int = DEFAULT_FILTER,
    var filterType: Int = DEFAULT_FILTER
) : Parcelable, BaseAdapter.MainData {
    var isCheckedForDelete = false
    var isDeleteMode = false
    var isFromDb = false

    fun isFilterEmpty(): Boolean {
        return filter.isEmpty()
    }

    fun listTypeIcon(): Int {
        return when {
            isBlackFilter() -> R.drawable.ic_block
            else -> R.drawable.ic_white_filter
        }
    }

    fun filterTypeIcon(): Int {
        return when (conditionType) {
            CONDITION_TYPE_FULL -> R.drawable.ic_start
            CONDITION_TYPE_START -> R.drawable.ic_contain
            else -> R.drawable.ic_end
        }
    }

    fun filterToInput(context: Context): String {
        return context.extractedFilter(filter)
    }

    fun isValidPhoneNumber(context: Context): Boolean {
        return filter.isValidPhoneNumber(context)
    }

    fun isTypeStart(): Boolean {
        return conditionType == CONDITION_TYPE_START
    }

    fun isTypeFull(): Boolean {
        return conditionType == CONDITION_TYPE_FULL
    }

    fun isTypeContain(): Boolean {
        return conditionType == CONDITION_TYPE_CONTAIN
    }

    fun isBlackFilter(): Boolean {
        return filterType == BLACK_FILTER
    }

    fun isWhiteFilter(): Boolean {
        return filterType == WHITE_FILTER
    }
}
