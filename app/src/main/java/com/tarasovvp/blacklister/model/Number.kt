package com.tarasovvp.blacklister.model

import com.tarasovvp.blacklister.ui.base.BaseAdapter

open class Number(
    open var number: String = "",
    var start: Boolean = false,
    var contain: Boolean = false,
    var end: Boolean = false,
) : BaseAdapter.MainData {
    var isCheckedForDelete = false
    open var isBlackNumber = false
}
