package com.tarasovvp.smartblocker.domain.enums

import com.tarasovvp.smartblocker.utils.extensions.titleRes

enum class NumberDataFiltering {
    FILTER_CONDITION_FULL_FILTERING,
    FILTER_CONDITION_START_FILTERING,
    FILTER_CONDITION_CONTAIN_FILTERING,
    CALL_BLOCKED,
    CALL_PERMITTED,
    CONTACT_WITH_BLOCKER,
    CONTACT_WITH_PERMISSION;

    fun title(): Int {
        return titleRes()
    }
}