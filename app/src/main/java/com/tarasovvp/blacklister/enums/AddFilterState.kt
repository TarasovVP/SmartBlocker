package com.tarasovvp.blacklister.enums

import com.tarasovvp.blacklister.R

enum class AddFilterState(val title: Int, val color: Int) {
    ADD_FILTER_INVALID(R.string.invalid, R.color.nobel),
    ADD_FILTER_ADD(R.string.add,  R.color.main_color),
    ADD_FILTER_DELETE(R.string.delete_menu,  android.R.color.holo_red_light),
    ADD_FILTER_CHANGE(R.string.change,  android.R.color.holo_orange_light);
}