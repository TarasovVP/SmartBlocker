package com.tarasovvp.smartblocker.domain.enums

import com.tarasovvp.smartblocker.utils.extensions.colorRes
import com.tarasovvp.smartblocker.utils.extensions.descriptionRes
import com.tarasovvp.smartblocker.utils.extensions.iconRes
import com.tarasovvp.smartblocker.utils.extensions.requestRes
import com.tarasovvp.smartblocker.utils.extensions.successRes
import com.tarasovvp.smartblocker.utils.extensions.titleRes

enum class FilterAction {
    FILTER_ACTION_INVALID,
    FILTER_ACTION_BLOCKER_CREATE,
    FILTER_ACTION_PERMISSION_CREATE,
    FILTER_ACTION_BLOCKER_DELETE,
    FILTER_ACTION_PERMISSION_DELETE,
    FILTER_ACTION_PERMISSION_TRANSFER,
    FILTER_ACTION_BLOCKER_TRANSFER,
    ;

    fun title(): Int {
        return titleRes()
    }

    fun color(): Int {
        return colorRes()
    }

    fun icon(): Int {
        return iconRes()
    }

    fun descriptionText(): Int {
        return descriptionRes()
    }

    fun requestText(): Int {
        return requestRes()
    }

    fun successText(): Int {
        return successRes()
    }
}
