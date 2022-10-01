package com.tarasovvp.blacklister.enum

import com.tarasovvp.blacklister.R

enum class Condition(val title: Int, val index: Int) {
    CONDITION_TYPE_FULL(R.string.condition_full, 0),
    CONDITION_TYPE_START(R.string.condition_start, 1),
    CONDITION_TYPE_CONTAIN(R.string.condition_contain, 2)
}