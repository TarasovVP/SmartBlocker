package com.tarasovvp.smartblocker.domain.enums

import com.tarasovvp.smartblocker.R

enum class EmptyState(val description: Int) {
    EMPTY_STATE_BLOCKERS(R.string.empty_state_blockers),
    EMPTY_STATE_PERMISSIONS(R.string.empty_state_permissions),
    EMPTY_STATE_CONTACTS(R.string.empty_state_contacts),
    EMPTY_STATE_CALLS(R.string.empty_state_calls),
    EMPTY_STATE_QUERY(R.string.empty_state_query),
    EMPTY_STATE_ADD_FILTER(R.string.empty_state_add_filter),
    EMPTY_STATE_FILTERS(R.string.empty_state_filters),
    EMPTY_STATE_NUMBERS(R.string.empty_state_numbers),
    EMPTY_STATE_FILTERED_CALLS(R.string.empty_state_filtered_calls),
    EMPTY_STATE_HIDDEN(R.string.empty_state_hidden),
    EMPTY_STATE_ACCOUNT(R.string.empty_state_account)
}