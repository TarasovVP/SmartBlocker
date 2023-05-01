package com.tarasovvp.smartblocker.domain.enums

import com.tarasovvp.smartblocker.utils.extensions.descriptionRes
import com.tarasovvp.smartblocker.utils.extensions.mainImageRes
import com.tarasovvp.smartblocker.utils.extensions.tabImageRes

enum class OnBoarding {
    ONBOARDING_INTRO,
    ONBOARDING_FILTER_CONDITIONS,
    ONBOARDING_INFO,
    ONBOARDING_PERMISSIONS;

    fun description(): Int {
        return descriptionRes()
    }

    fun mainImage(): Int {
        return mainImageRes()
    }

    fun tabImage(): Int {
        return tabImageRes()
    }
}