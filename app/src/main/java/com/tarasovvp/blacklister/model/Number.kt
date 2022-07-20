package com.tarasovvp.blacklister.model

import android.os.Parcelable
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import kotlinx.android.parcel.Parcelize

@Parcelize
open class Number(
    open var number: String = "",
    var start: Boolean = false,
    var contain: Boolean = false,
    var end: Boolean = false,
) : Parcelable, BaseAdapter.MainData {
    var isCheckedForDelete = false
    open var isBlackNumber = false
}
