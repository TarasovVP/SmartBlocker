package com.tarasovvp.blacklister.enums

import com.tarasovvp.blacklister.R

enum class Condition(val title: Int, var isSelected: Boolean, var index: Int) {
    CONDITION_TYPE_FULL(R.string.condition_full, false, 0),
    CONDITION_TYPE_START(R.string.condition_start, false, 1),
    CONDITION_TYPE_CONTAIN(R.string.condition_contain, false, 2)
}