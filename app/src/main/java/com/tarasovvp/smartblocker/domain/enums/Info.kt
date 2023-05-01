package com.tarasovvp.smartblocker.domain.enums

import com.tarasovvp.smartblocker.utils.extensions.descriptionRes
import com.tarasovvp.smartblocker.utils.extensions.titleRes

enum class Info {
    INFO_BLOCKER_LIST,
    INFO_PERMISSION_LIST,
    INFO_CALL_LIST,
    INFO_CONTACT_LIST,
    INFO_CREATE_FILTER_FULL,
    INFO_CREATE_FILTER_START,
    INFO_CREATE_FILTER_CONTAIN,
    INFO_DETAILS_NUMBER_DATA,
    INFO_DETAILS_FILTER;

    fun description(): Int {
        return descriptionRes()
    }

    fun title(): Int {
        return titleRes()
    }
}