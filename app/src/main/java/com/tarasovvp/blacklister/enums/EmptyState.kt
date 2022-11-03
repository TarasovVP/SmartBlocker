package com.tarasovvp.blacklister.enums

import com.tarasovvp.blacklister.R

enum class EmptyState(
    val titleResource: Int,
    val descriptionResource: Int,
    val iconResource: Int
) {
    EMPTY_STATE_BLOCKERS(R.string.empty_state_blockers_title,
        R.string.empty_state_blockers_description,
        R.drawable.ic_empty_state_blockers),
    EMPTY_STATE_PERMISSIONS(R.string.empty_state_permissions_title,
        R.string.empty_state_permissions_description,
        R.drawable.ic_empty_state_permissions),
    EMPTY_STATE_CONTACTS(R.string.empty_state_contacts_title,
        R.string.empty_state_contacts_description,
        R.drawable.ic_empty_state_contacts),
    EMPTY_STATE_CALLS(R.string.empty_state_calls_title,
        R.string.empty_state_calls_description,
        R.drawable.ic_empty_state_calls),
    EMPTY_STATE_QUERY(R.string.empty_state_query_title,
        R.string.empty_state_query_description,
        R.drawable.ic_empty_state_query)
}