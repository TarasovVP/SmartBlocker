package com.tarasovvp.smartblocker.enums

import com.tarasovvp.smartblocker.R

enum class EmptyState(
    val descriptionResource: Int,
    val iconResource: Int,
) {
    EMPTY_STATE_BLOCKERS(R.string.empty_state_blockers_description,
        R.drawable.ic_assistant_empty_state),
    EMPTY_STATE_PERMISSIONS(R.string.empty_state_permissions_description,
        R.drawable.ic_assistant_empty_state),
    EMPTY_STATE_CONTACTS(R.string.empty_state_contacts_description,
        R.drawable.ic_assistant_empty_state),
    EMPTY_STATE_CALLS(R.string.empty_state_calls_description,
        R.drawable.ic_assistant_empty_state),
    EMPTY_STATE_QUERY(R.string.empty_state_query_description,
        R.drawable.ic_assistant_empty_state),
    EMPTY_STATE_ACCOUNT(R.string.empty_state_account_description,
        R.drawable.ic_assistant_empty_state),
    EMPTY_STATE_FILTERS_BY_CONTACT(R.string.empty_state_filters_by_contact_description,
        R.drawable.ic_assistant_empty_state),
    EMPTY_STATE_FILTERS_CONTACTS_BY_BLOCKER(R.string.empty_state_contacts_by_blocker_description,
        R.drawable.ic_assistant_empty_state),
    EMPTY_STATE_FILTERS_CONTACTS_BY_PERMISSION(R.string.empty_state_contacts_by_permission_description,
        R.drawable.ic_assistant_empty_state),
    EMPTY_STATE_BLOCKED_CALLS(R.string.empty_state_blocked_calls_description,
        R.drawable.ic_assistant_empty_state)
}