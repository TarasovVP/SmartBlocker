package com.tarasovvp.blacklister.model

import android.content.Context
import android.os.Parcelable
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.COUNTRY_CODE_START
import com.tarasovvp.blacklister.extensions.*
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.utils.PhoneNumberUtil
import com.tarasovvp.blacklister.utils.PhoneNumberUtil.extractedFilter
import kotlinx.android.parcel.Parcelize

@Parcelize
open class Filter(
    open var filter: String = "",
    var type: Int = 0,
) : Parcelable, BaseAdapter.MainData {
    var isCheckedForDelete = false
    open var isBlackFilter = false
    var isDeleteMode = false
    var isFromDb = false
    var isFilterIdentical = false
    fun listTypeIcon(): Int {
        return when {
            isBlackFilter -> R.drawable.ic_black_filter
            else -> R.drawable.ic_white_filter
        }
    }

    fun filterTypeIcon(): Int {
        return when (type) {
            FILTER_FULL -> R.drawable.ic_start
            FILTER_START -> R.drawable.ic_contain
            else -> R.drawable.ic_end
        }
    }

    fun filterToInput(context: Context): String {
        return context.extractedFilter(filter)
    }

    fun isTypeStart(): Boolean {
        return type == FILTER_START
    }

    fun isTypeContain(): Boolean {
        return type == FILTER_CONTAIN
    }

    companion object {
        const val FILTER_FULL = 0
        const val FILTER_START = 1
        const val FILTER_CONTAIN = 2
    }
}
