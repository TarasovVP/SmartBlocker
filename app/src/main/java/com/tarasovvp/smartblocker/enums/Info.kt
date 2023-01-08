package com.tarasovvp.smartblocker.enums

import com.tarasovvp.smartblocker.R

enum class Info(
    val description: Int,
    val title: Int,
) {
    INFO_BLOCKER_LIST(R.string.info_blockers_list,
        R.string.blocker_list),
    INFO_PERMISSION_LIST(R.string.empty_state_permissions,
        R.string.permission_list),
    INFO_CALL_LIST(R.string.empty_state_contacts,
        R.string.all_call_log_list),
    INFO_CONTACT_LIST(R.string.empty_state_calls,
        R.string.contact_list),
    INFO_FILTER_ADD_FULL(R.string.filter_full_number_description,
        R.string.filter_action_create),
    INFO_FILTER_ADD_START(R.string.filter_start_description,
        R.string.condition_start),
    INFO_FILTER_ADD_CONTAIN(R.string.filter_contain_description,
        R.string.condition_contain),
    INFO_NUMBER_DATA_DETAIL(R.string.number_data_detail_info,
        R.string.details_),
    INFO_FILTER_DETAIL(R.string.empty_state_account,
        R.string.details_)
}