package com.tarasovvp.smartblocker.domain.enums

import com.tarasovvp.smartblocker.R

enum class NumberDataFiltering(val title: Int) {
    FILTER_CONDITION_FULL_FILTERING(R.string.filter_condition_full),
    FILTER_CONDITION_START_FILTERING(R.string.filter_condition_start),
    FILTER_CONDITION_CONTAIN_FILTERING(R.string.filter_condition_contain),
    CALL_BLOCKED(R.string.filter_call_blocked),
    CALL_PERMITTED(R.string.filter_call_permitted),
    CONTACT_WITH_BLOCKER(R.string.filter_contact_blocker),
    CONTACT_WITH_PERMISSION(R.string.filter_contact_permission)
}