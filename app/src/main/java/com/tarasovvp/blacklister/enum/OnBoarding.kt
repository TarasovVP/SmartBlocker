package com.tarasovvp.blacklister.enum

import com.tarasovvp.blacklister.R

enum class OnBoarding(val title: Int, val icon: Int) {
    AVOID_CALL(R.string.avoid_call, R.drawable.ic_avoid_call),
    RECEIVE_NOTIFICATIONS(R.string.receive_notifications, R.drawable.ic_receive_notifications),
    ACCEPT_PERMISSIONS(R.string.accept_permissions, R.drawable.ic_accept_permissions)
}