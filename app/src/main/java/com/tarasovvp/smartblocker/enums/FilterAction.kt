package com.tarasovvp.smartblocker.enums

import com.tarasovvp.smartblocker.R

enum class FilterAction(val title: Int, val color: Int, val icon: Int, val descriptionText: Int, val requestText: Int, val successText: Int) {
    FILTER_ACTION_BLOCKER_INVALID(R.string.invalid, R.color.nobel, R.drawable.ic_black_filter_inactive, R.string.filter_description_invalid,0,0),
    FILTER_ACTION_PERMISSION_INVALID(R.string.invalid, R.color.nobel, R.drawable.ic_white_filter_inactive, R.string.filter_description_invalid,0,0),
    FILTER_ACTION_BLOCKER_ADD(R.string.add, R.color.main_color, R.drawable.ic_blocker, R.string.number_add_to_blocker_description, R.string.number_add_to_blocker_request,R.string.number_add_to_blocker_success),
    FILTER_ACTION_PERMISSION_ADD(R.string.add, R.color.main_color, R.drawable.ic_permission, R.string.number_add_to_permission_description, R.string.number_add_to_permission_request,R.string.number_add_to_permission_success),
    FILTER_ACTION_BLOCKER_DELETE(R.string.delete_menu, android.R.color.holo_red_light, R.drawable.ic_delete, R.string.number_delete_from_blocker_description, R.string.number_delete_from_blocker_request, R.string.number_delete_from_blocker_success),
    FILTER_ACTION_PERMISSION_DELETE(R.string.delete_menu, android.R.color.holo_red_light, R.drawable.ic_delete, R.string.number_delete_from_permission_description, R.string.number_delete_from_permission_request, R.string.number_delete_from_permission_success),
    FILTER_ACTION_PERMISSION_TRANSFER(R.string.change, android.R.color.holo_orange_light, R.drawable.ic_white_to_black_filter, R.string.number_transfer_to_blocker_description, R.string.number_transfer_to_blocker_request, R.string.number_transfer_to_blocker_success),
    FILTER_ACTION_BLOCKER_TRANSFER(R.string.change, android.R.color.holo_orange_light, R.drawable.ic_black_to_white_filter, R.string.number_transfer_to_permission_description, R.string.number_transfer_to_permission_request, R.string.number_transfer_to_permission_success),
    FILTER_ACTION_PERMISSION_CHANGE(0, 0, R.drawable.ic_white_to_black_filter, 0, R.string.transfer_to_blocker, 0),
    FILTER_ACTION_BLOCKER_CHANGE(0, 0, R.drawable.ic_black_to_white_filter, 0, R.string.transfer_to_permission, 0);
}