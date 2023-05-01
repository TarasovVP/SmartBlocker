package com.tarasovvp.smartblocker.domain.enums

import com.tarasovvp.smartblocker.utils.extensions.descriptionRes

enum class EmptyState {
    EMPTY_STATE_BLOCKERS,
    EMPTY_STATE_PERMISSIONS,
    EMPTY_STATE_CONTACTS,
    EMPTY_STATE_CALLS,
    EMPTY_STATE_QUERY,
    EMPTY_STATE_CREATE_FILTER,
    EMPTY_STATE_FILTERS,
    EMPTY_STATE_NUMBERS,
    EMPTY_STATE_FILTERED_CALLS,
    EMPTY_STATE_HIDDEN,
    EMPTY_STATE_ACCOUNT;

    fun description(): Int {
        return descriptionRes()
    }
}