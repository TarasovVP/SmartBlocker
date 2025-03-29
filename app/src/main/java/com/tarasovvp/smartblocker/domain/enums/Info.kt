package com.tarasovvp.smartblocker.domain.enums

import com.tarasovvp.smartblocker.utils.extensions.descriptionRes
import com.tarasovvp.smartblocker.utils.extensions.titleRes

enum class Info {
    INFO_BLOCKER,
    INFO_PERMISSION,
    INFO_FILTER,
    INFO_CALL,
    INFO_CONTACT,
    INFO_LIST_BLOCKER,
    INFO_DETAILS_BLOCKER,
    INFO_CREATE_BLOCKER_FULL,
    INFO_CREATE_BLOCKER_START,
    INFO_CREATE_BLOCKER_CONTAIN,
    INFO_LIST_PERMISSION,
    INFO_DETAILS_PERMISSION,
    INFO_CREATE_PERMISSION_FULL,
    INFO_CREATE_PERMISSION_START,
    INFO_CREATE_PERMISSION_CONTAIN,
    INFO_LIST_CALL,
    INFO_LIST_CONTACT,
    INFO_DETAILS_NUMBER_DATA,
    INFO_PRIORITY_RULES,
    INFO_INCOMPLETE_NUMBERS,
    ;

    fun description(): Int {
        return descriptionRes()
    }

    fun title(): Int {
        return titleRes()
    }
}
