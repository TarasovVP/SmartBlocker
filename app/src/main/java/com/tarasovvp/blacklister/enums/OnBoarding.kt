package com.tarasovvp.blacklister.enums

import com.tarasovvp.blacklister.R

enum class OnBoarding(val title: Int, val mainImage: Int, val tabImage: Int) {
    INNER_DATA_ACCESS(R.string.inner_data_access_title, R.drawable.ic_inner_data_access, R.drawable.ic_tab_first),
    BLACK_WHITE_LIST(R.string.black_white_list_title, R.drawable.ic_black_white_list, R.drawable.ic_tab_second),
    AUTHORIZED_USER(R.string.authorized_user_title, R.drawable.ic_authorized_user, R.drawable.ic_tab_third),
    ACCESS_PERMISSIONS(R.string.access_permissions, R.drawable.ic_access_permissions, R.drawable.ic_tab_last)
}