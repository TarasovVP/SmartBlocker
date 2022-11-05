package com.tarasovvp.blacklister.enums

import com.tarasovvp.blacklister.R

enum class FilterCondition(
    val title: Int,
    val mainIcon: Int,
    val smallIcon: Int,
    var index: Int
) {
    FILTER_CONDITION_FULL(R.string.condition_full,
        R.drawable.ic_condition_phone,
        R.drawable.ic_condition_phone_small,
        0),
    FILTER_CONDITION_START(R.string.condition_start,
        R.drawable.ic_condition_flag,
        R.drawable.ic_condition_flag_small,
        1),
    FILTER_CONDITION_CONTAIN(R.string.condition_contain,
        R.drawable.ic_condition_filter,
        R.drawable.ic_condition_filter_small,
        2);

    companion object {
        fun getTitleByIndex(index: Int): Int =
            values().find { it.index == index }?.title ?: R.string.condition_full

        fun getMainIconByIndex(index: Int): Int =
            values().find { it.index == index }?.mainIcon ?: R.drawable.ic_condition_phone

        fun getSmallIconByIndex(index: Int): Int =
            values().find { it.index == index }?.smallIcon ?: R.drawable.ic_condition_phone_small
    }
}