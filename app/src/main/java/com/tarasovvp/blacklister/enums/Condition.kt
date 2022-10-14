package com.tarasovvp.blacklister.enums

import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.extensions.orZero

enum class Condition(val title: Int, val icon: Int, var isSelected: Boolean, var index: Int) {
    CONDITION_TYPE_FULL(R.string.condition_full, R.drawable.ic_phone, false, 0),
    CONDITION_TYPE_START(R.string.condition_start,  R.drawable.ic_flag, false, 1),
    CONDITION_TYPE_CONTAIN(R.string.condition_contain,  R.drawable.ic_filter, false, 2);

    companion object {
        fun getTitleByIndex(index: Int): Int = values().find { it.index == index }?.title.orZero()
        fun getIconByIndex(index: Int): Int = values().find { it.index == index }?.icon.orZero()
    }
}