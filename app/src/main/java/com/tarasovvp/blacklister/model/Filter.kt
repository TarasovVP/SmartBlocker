package com.tarasovvp.blacklister.model

import android.os.Parcelable
import com.tarasovvp.blacklister.R
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
}
