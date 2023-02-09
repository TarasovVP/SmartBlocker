package com.tarasovvp.smartblocker.enums

import android.content.Context
import android.text.Spanned
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.extensions.htmlWithImages

enum class OnBoarding(val description: Int, val mainImage: Int, val tabImage: Int) {
    INNER_DATA_ACCESS(R.string.onboarding_intro,
        R.drawable.ic_inner_data_access,
        R.drawable.ic_tab_first),
    BLACK_WHITE_LIST(R.string.onboarding_filter_conditions,
        R.drawable.ic_black_white_list,
        R.drawable.ic_tab_second),
    AUTHORIZED_USER(R.string.onboarding_filter_types,
        R.drawable.ic_authorized_user,
        R.drawable.ic_tab_third),
    ACCESS_PERMISSIONS(R.string.onboarding_permissions,
        R.drawable.ic_access_permissions,
        R.drawable.ic_tab_last);

    fun getHtmlWithImages(context: Context): Spanned {
        return context.htmlWithImages(context.getString(description))
    }
}