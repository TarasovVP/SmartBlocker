package com.tarasovvp.smartblocker.domain.enums

import android.content.Context
import android.text.Spanned
import com.tarasovvp.smartblocker.utils.extensions.descriptionText
import com.tarasovvp.smartblocker.utils.extensions.mainImageRes
import com.tarasovvp.smartblocker.utils.extensions.tabImageRes

enum class OnBoarding {
    ONBOARDING_INTRO,
    ONBOARDING_FILTER_CONDITIONS,
    ONBOARDING_INFO,
    ONBOARDING_PERMISSIONS,
    ;

    fun description(context: Context): Spanned {
        return descriptionText(context)
    }

    fun mainImage(): Int {
        return mainImageRes()
    }

    fun tabImage(): Int {
        return tabImageRes()
    }
}
