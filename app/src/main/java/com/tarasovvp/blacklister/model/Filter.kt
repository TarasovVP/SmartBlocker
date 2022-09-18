package com.tarasovvp.blacklister.model

import android.os.Parcelable
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.extensions.getPhoneNumber
import com.tarasovvp.blacklister.extensions.isNotNull
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import kotlinx.android.parcel.Parcelize

@Parcelize
open class Filter(
    open var filter: String = "",
    var start: Boolean = false,
    var contain: Boolean = false,
    var end: Boolean = false,
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
        return when {
            start && contain.not() && end.not() -> R.drawable.ic_start
            contain && start.not() && end.not() -> R.drawable.ic_contain
            end && contain.not() && start.not() -> R.drawable.ic_contain
            start && contain && end.not() -> R.drawable.ic_start
            start && contain.not() && end -> R.drawable.ic_start
            contain && end && start.not() -> R.drawable.ic_start
            start && contain && end -> R.drawable.ic_start
            else -> 0
        }
    }
   fun isFilterIdentical(isStart: Boolean, isContain: Boolean, isEnd: Boolean): Boolean {
        return isFromDb && filter.isNotEmpty() && isStart == start &&isContain == contain && isEnd == end
    }
    fun nationalNumber(countryCode: String): String {
        return filter.getPhoneNumber(countryCode)?.nationalNumber.toString()
    }
}
