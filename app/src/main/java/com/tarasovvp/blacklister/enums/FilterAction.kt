package com.tarasovvp.blacklister.enums

import com.tarasovvp.blacklister.R

enum class FilterAction(val title: Int, val color: Int) {
    FILTER_ACTION_INVALID(R.string.invalid, R.color.nobel),
    FILTER_ACTION_ADD(R.string.add,  R.color.main_color),
    FILTER_ACTION_DELETE(R.string.delete_menu,  android.R.color.holo_red_light),
    FILTER_ACTION_CHANGE(R.string.change,  android.R.color.holo_orange_light);
}