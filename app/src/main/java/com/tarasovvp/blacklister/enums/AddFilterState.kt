package com.tarasovvp.blacklister.enums

import com.tarasovvp.blacklister.R

enum class AddFilterState(val title: Int, val color: Int, var index: Int) {
    ADD_FILTER_INVALID(R.string.invalid, android.R.color.holo_red_light, 0),
    ADD_FILTER_ADD(R.string.add,  R.color.main_color, 1),
    ADD_FILTER_DELETE(R.string.delete_menu,  android.R.color.holo_green_dark, 2),
    ADD_FILTER_CHANGE(R.string.change,  android.R.color.holo_orange_light, 3);
}