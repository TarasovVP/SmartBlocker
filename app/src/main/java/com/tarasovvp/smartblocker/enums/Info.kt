package com.tarasovvp.smartblocker.enums

import com.tarasovvp.smartblocker.R

enum class Info(
    val description: Int,
    val title: Int,
) {
    INFO_BLOCKER_LIST(R.string.info_blocker_list,
        R.string.list_blocker),
    INFO_PERMISSION_LIST(R.string.info_permission_list,
        R.string.list_permission),
    INFO_CALL_LIST(R.string.info_call_list,
        R.string.list_call),
    INFO_CONTACT_LIST(R.string.info_contact_list,
        R.string.list_contact),
    INFO_FILTER_ADD_FULL(R.string.info_condition_full,
        R.string.filter_condition_full),
    INFO_FILTER_ADD_START(R.string.info_condition_start,
        R.string.filter_condition_start),
    INFO_FILTER_ADD_CONTAIN(R.string.info_condition_contain,
        R.string.filter_condition_contain),
    INFO_NUMBER_DATA_DETAIL(R.string.info_number_details,
        R.string.details_number),
    INFO_FILTER_DETAIL(R.string.info_filter_details,
        R.string.details_filter)
}