package com.tarasovvp.blacklister.enums

import com.tarasovvp.blacklister.R

enum class Condition(
    val title: Int,
    val mainIcon: Int,
    val smallIcon: Int,
    var isSelected: Boolean,
    var index: Int,
) {
    CONDITION_TYPE_FULL(R.string.condition_full,
        R.drawable.ic_condition_phone,
        R.drawable.ic_condition_phone_small,
        false,
        0),
    CONDITION_TYPE_START(R.string.condition_start,
        R.drawable.ic_condition_flag,
        R.drawable.ic_condition_flag_small,
        false,
        1),
    CONDITION_TYPE_CONTAIN(R.string.condition_contain,
        R.drawable.ic_condition_filter,
        R.drawable.ic_condition_filter_small,
        false,
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