package com.tarasovvp.smartblocker.enums

import com.tarasovvp.smartblocker.R

enum class FilterCondition(
    val title: Int,
    val mainIcon: Int,
    val smallBlockerIcon: Int,
    val smallPermissionIcon: Int,
    var index: Int,
) {
    FILTER_CONDITION_FULL(R.string.condition_full,
        R.drawable.ic_condition_full,
        R.drawable.ic_condition_full_blocker_small,
        R.drawable.ic_condition_full_permission_small,
        0),
    FILTER_CONDITION_START(R.string.condition_start,
        R.drawable.ic_condition_start,
        R.drawable.ic_condition_start_blocker_small,
        R.drawable.ic_condition_start_permission_small,
        1),
    FILTER_CONDITION_CONTAIN(R.string.condition_contain,
        R.drawable.ic_condition_contain,
        R.drawable.ic_condition_contain_blocker_small,
        R.drawable.ic_condition_contain_permission_small,
        2);

    companion object {
        fun getTitleByIndex(index: Int): Int =
            values().find { it.index == index }?.title ?: R.string.condition_full

        fun getMainIconByIndex(index: Int): Int =
            values().find { it.index == index }?.mainIcon ?: R.drawable.ic_condition_full

        fun getSmallIconByIndex(index: Int, isBlocker: Boolean): Int =
            if (isBlocker) values().find { it.index == index }?.smallBlockerIcon
                ?: R.drawable.ic_condition_full_blocker_small else values().find { it.index == index }?.smallPermissionIcon
                ?: R.drawable.ic_condition_full_permission_small
    }
}