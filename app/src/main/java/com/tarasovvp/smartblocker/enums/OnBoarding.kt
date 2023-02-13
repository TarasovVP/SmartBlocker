package com.tarasovvp.smartblocker.enums

import com.tarasovvp.smartblocker.R

enum class OnBoarding(val description: Int, val mainImage: Int, val tabImage: Int) {
    ONBOARDING_INTRO(R.string.onboarding_intro,
        R.drawable.ic_onboarding_intro,
        R.drawable.ic_tab_first),
    ONBOARDING_FILTER_CONDITIONS(R.string.onboarding_filter_conditions,
        R.drawable.ic_onboarding_filter_conditions,
        R.drawable.ic_tab_second),
    ONBOARDING_INFO(R.string.onboarding_info,
        R.drawable.ic_onboarding_info,
        R.drawable.ic_tab_third),
    ONBOARDING_PERMISSIONS(R.string.onboarding_permissions,
        R.drawable.ic_onboarding_permissions,
        R.drawable.ic_tab_last);
}