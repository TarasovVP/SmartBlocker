package com.tarasovvp.smartblocker.domain.enums

import com.tarasovvp.smartblocker.utils.extensions.mainIconRes
import com.tarasovvp.smartblocker.utils.extensions.smallIconRes
import com.tarasovvp.smartblocker.utils.extensions.titleRes

enum class FilterCondition {
    FILTER_CONDITION_FULL,
    FILTER_CONDITION_START,
    FILTER_CONDITION_CONTAIN;

    fun title(): Int {
        return titleRes()
    }

    fun mainIcon(): Int {
        return mainIconRes()
    }

    fun smallIcon(isBlocker: Boolean): Int {
        return smallIconRes(isBlocker)
    }
}