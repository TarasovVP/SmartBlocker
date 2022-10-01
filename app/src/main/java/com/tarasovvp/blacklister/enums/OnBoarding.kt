package com.tarasovvp.blacklister.enums

import com.tarasovvp.blacklister.R

enum class OnBoarding(val title: Int, val icon: Int) {
    INNER_DATA_ACCESS(R.string.inner_data_access_title, R.drawable.ic_inner_data_access),
    BLACK_WHITE_LIST(R.string.black_white_list_title, R.drawable.ic_black_white_list),
    AUTHORIZED_USER(R.string.authorized_user_title, R.drawable.ic_authorized_user),
    ACCESS_PERMISSIONS(R.string.access_permissions, R.drawable.ic_access_permissions)
}