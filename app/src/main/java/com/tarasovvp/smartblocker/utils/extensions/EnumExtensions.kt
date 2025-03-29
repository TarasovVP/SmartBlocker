package com.tarasovvp.smartblocker.utils.extensions

import android.content.Context
import android.text.Spanned
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.enums.EmptyState
import com.tarasovvp.smartblocker.domain.enums.FilterAction
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.domain.enums.Info
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.domain.enums.OnBoarding

fun EmptyState.descriptionRes(): Int {
    return when (this) {
        EmptyState.EMPTY_STATE_BLOCKERS -> R.string.empty_state_blockers
        EmptyState.EMPTY_STATE_PERMISSIONS -> R.string.empty_state_permissions
        EmptyState.EMPTY_STATE_CONTACTS -> R.string.empty_state_contacts
        EmptyState.EMPTY_STATE_CALLS -> R.string.empty_state_calls
        EmptyState.EMPTY_STATE_QUERY -> R.string.empty_state_query
        EmptyState.EMPTY_STATE_CREATE_FILTER -> R.string.empty_state_add_filter
        EmptyState.EMPTY_STATE_FILTERS -> R.string.empty_state_filters
        EmptyState.EMPTY_STATE_NUMBERS -> R.string.empty_state_numbers
        EmptyState.EMPTY_STATE_FILTERED_CALLS -> R.string.empty_state_filtered_calls
        EmptyState.EMPTY_STATE_HIDDEN -> R.string.empty_state_hidden
        EmptyState.EMPTY_STATE_ACCOUNT -> R.string.empty_state_account
    }
}

fun Info.titleRes(): Int {
    return when (this) {
        Info.INFO_BLOCKER -> R.string.info_blocker_title
        Info.INFO_PERMISSION -> R.string.info_permission_title
        Info.INFO_FILTER -> R.string.info_filter_title
        Info.INFO_CALL -> R.string.info_call_title
        Info.INFO_CONTACT -> R.string.info_contact_title
        Info.INFO_LIST_BLOCKER -> R.string.info_list_blocker_title
        Info.INFO_DETAILS_BLOCKER -> R.string.info_details_blocker_title
        Info.INFO_CREATE_BLOCKER_FULL,
        Info.INFO_CREATE_BLOCKER_START,
        Info.INFO_CREATE_BLOCKER_CONTAIN,
        -> R.string.info_create_blocker_title

        Info.INFO_LIST_PERMISSION -> R.string.info_list_permission_title
        Info.INFO_DETAILS_PERMISSION -> R.string.info_details_permission_title
        Info.INFO_CREATE_PERMISSION_FULL,
        Info.INFO_CREATE_PERMISSION_START,
        Info.INFO_CREATE_PERMISSION_CONTAIN,
        -> R.string.info_create_permission_title

        Info.INFO_LIST_CALL -> R.string.info_list_call_title
        Info.INFO_LIST_CONTACT -> R.string.info_list_contact_title
        Info.INFO_DETAILS_NUMBER_DATA -> R.string.info_details_number_data_title
        Info.INFO_PRIORITY_RULES -> R.string.info_priority_rules_title
        Info.INFO_INCOMPLETE_NUMBERS -> R.string.info_incomplete_numbers_title
    }
}

fun Info.descriptionRes(): Int {
    return when (this) {
        Info.INFO_BLOCKER -> R.string.info_blocker
        Info.INFO_PERMISSION -> R.string.info_permission
        Info.INFO_FILTER -> R.string.info_filter
        Info.INFO_CALL -> R.string.info_call
        Info.INFO_CONTACT -> R.string.info_contact
        Info.INFO_LIST_BLOCKER -> R.string.info_list_blocker
        Info.INFO_DETAILS_BLOCKER -> R.string.info_details_blocker
        Info.INFO_CREATE_BLOCKER_FULL -> R.string.info_create_blocker_full
        Info.INFO_CREATE_BLOCKER_START -> R.string.info_create_blocker_start
        Info.INFO_CREATE_BLOCKER_CONTAIN -> R.string.info_create_blocker_contain
        Info.INFO_LIST_PERMISSION -> R.string.info_list_permission
        Info.INFO_DETAILS_PERMISSION -> R.string.info_details_permission
        Info.INFO_CREATE_PERMISSION_FULL -> R.string.info_create_permission_full
        Info.INFO_CREATE_PERMISSION_START -> R.string.info_create_permission_start
        Info.INFO_CREATE_PERMISSION_CONTAIN -> R.string.info_create_permission_contain
        Info.INFO_LIST_CALL -> R.string.info_list_call
        Info.INFO_LIST_CONTACT -> R.string.info_list_contact
        Info.INFO_DETAILS_NUMBER_DATA -> R.string.info_details_number_data
        Info.INFO_PRIORITY_RULES -> R.string.info_priority_rules
        Info.INFO_INCOMPLETE_NUMBERS -> R.string.info_incomplete_numbers
    }
}

fun OnBoarding.descriptionText(context: Context): Spanned {
    return context.htmlWithImages(
        context.getString(
            when (this) {
                OnBoarding.ONBOARDING_INTRO -> R.string.onboarding_intro
                OnBoarding.ONBOARDING_FILTER_CONDITIONS -> R.string.onboarding_filter_conditions
                OnBoarding.ONBOARDING_INFO -> R.string.onboarding_info
                OnBoarding.ONBOARDING_PERMISSIONS -> R.string.onboarding_permissions
            },
        ),
    )
}

fun OnBoarding.mainImageRes(): Int {
    return when (this) {
        OnBoarding.ONBOARDING_INTRO -> R.drawable.ic_onboarding_intro
        OnBoarding.ONBOARDING_FILTER_CONDITIONS -> R.drawable.ic_onboarding_filter_conditions
        OnBoarding.ONBOARDING_INFO -> R.drawable.ic_onboarding_info
        OnBoarding.ONBOARDING_PERMISSIONS -> R.drawable.ic_onboarding_permissions
    }
}

fun OnBoarding.tabImageRes(): Int {
    return when (this) {
        OnBoarding.ONBOARDING_INTRO -> R.drawable.ic_tab_first
        OnBoarding.ONBOARDING_FILTER_CONDITIONS -> R.drawable.ic_tab_second
        OnBoarding.ONBOARDING_INFO -> R.drawable.ic_tab_third
        OnBoarding.ONBOARDING_PERMISSIONS -> R.drawable.ic_tab_last
    }
}

fun NumberDataFiltering.titleRes(): Int {
    return when (this) {
        NumberDataFiltering.FILTER_CONDITION_FULL_FILTERING -> R.string.filter_condition_full
        NumberDataFiltering.FILTER_CONDITION_START_FILTERING -> R.string.filter_condition_start
        NumberDataFiltering.FILTER_CONDITION_CONTAIN_FILTERING -> R.string.filter_condition_contain
        NumberDataFiltering.CALL_BLOCKED -> R.string.filter_call_blocked
        NumberDataFiltering.CALL_PERMITTED -> R.string.filter_call_permitted
        NumberDataFiltering.CONTACT_WITH_BLOCKER -> R.string.filter_contact_blocker
        NumberDataFiltering.CONTACT_WITH_PERMISSION -> R.string.filter_contact_permission
    }
}

fun FilterCondition.titleRes(): Int {
    return when (this) {
        FilterCondition.FILTER_CONDITION_FULL -> R.string.filter_condition_full
        FilterCondition.FILTER_CONDITION_START -> R.string.filter_condition_start
        FilterCondition.FILTER_CONDITION_CONTAIN -> R.string.filter_condition_contain
    }
}

fun FilterCondition.mainIconRes(): Int {
    return when (this) {
        FilterCondition.FILTER_CONDITION_FULL -> R.drawable.ic_condition_full
        FilterCondition.FILTER_CONDITION_START -> R.drawable.ic_condition_start
        FilterCondition.FILTER_CONDITION_CONTAIN -> R.drawable.ic_condition_contain
    }
}

fun FilterCondition.smallIconRes(isBlocker: Boolean): Int {
    return when (this) {
        FilterCondition.FILTER_CONDITION_FULL -> if (isBlocker) R.drawable.ic_condition_full_blocker_small else R.drawable.ic_condition_full_permission_small
        FilterCondition.FILTER_CONDITION_START -> if (isBlocker) R.drawable.ic_condition_start_blocker_small else R.drawable.ic_condition_start_permission_small
        FilterCondition.FILTER_CONDITION_CONTAIN -> if (isBlocker) R.drawable.ic_condition_contain_blocker_small else R.drawable.ic_condition_contain_permission_small
    }
}

fun FilterAction.titleRes(): Int {
    return when (this) {
        FilterAction.FILTER_ACTION_INVALID,
        FilterAction.FILTER_ACTION_BLOCKER_CREATE,
        FilterAction.FILTER_ACTION_PERMISSION_CREATE,
        -> R.string.filter_action_create

        FilterAction.FILTER_ACTION_BLOCKER_DELETE,
        FilterAction.FILTER_ACTION_PERMISSION_DELETE,
        -> R.string.filter_action_delete

        FilterAction.FILTER_ACTION_PERMISSION_TRANSFER,
        FilterAction.FILTER_ACTION_BLOCKER_TRANSFER,
        -> R.string.filter_action_transfer
    }
}

fun FilterAction.colorRes(): Int {
    return when (this) {
        FilterAction.FILTER_ACTION_INVALID -> R.color.inactive_bg
        FilterAction.FILTER_ACTION_BLOCKER_CREATE,
        FilterAction.FILTER_ACTION_PERMISSION_CREATE,
        FilterAction.FILTER_ACTION_PERMISSION_TRANSFER,
        FilterAction.FILTER_ACTION_BLOCKER_TRANSFER,
        -> R.color.button_bg

        FilterAction.FILTER_ACTION_BLOCKER_DELETE,
        FilterAction.FILTER_ACTION_PERMISSION_DELETE,
        -> R.color.sunset
    }
}

fun FilterAction.iconRes(): Int {
    return when (this) {
        FilterAction.FILTER_ACTION_INVALID -> R.drawable.ic_blocker_inactive
        FilterAction.FILTER_ACTION_BLOCKER_CREATE,
        FilterAction.FILTER_ACTION_BLOCKER_TRANSFER,
        -> R.drawable.ic_blocker

        FilterAction.FILTER_ACTION_PERMISSION_CREATE,
        FilterAction.FILTER_ACTION_PERMISSION_TRANSFER,
        -> R.drawable.ic_permission

        FilterAction.FILTER_ACTION_BLOCKER_DELETE -> R.drawable.ic_delete
        FilterAction.FILTER_ACTION_PERMISSION_DELETE -> R.drawable.ic_delete
    }
}

fun FilterAction.descriptionRes(): Int {
    return when (this) {
        FilterAction.FILTER_ACTION_INVALID -> R.string.filter_action_create_number_invalid
        FilterAction.FILTER_ACTION_BLOCKER_CREATE -> R.string.filter_action_create_blocker_description
        FilterAction.FILTER_ACTION_PERMISSION_CREATE -> R.string.filter_action_create_permission_description
        FilterAction.FILTER_ACTION_BLOCKER_DELETE -> R.string.filter_action_delete_blocker_description
        FilterAction.FILTER_ACTION_PERMISSION_DELETE -> R.string.filter_action_delete_permission_description
        FilterAction.FILTER_ACTION_PERMISSION_TRANSFER -> R.string.filter_action_transfer_blocker_description
        FilterAction.FILTER_ACTION_BLOCKER_TRANSFER -> R.string.filter_action_transfer_permission_description
    }
}

fun FilterAction.requestRes(): Int {
    return when (this) {
        FilterAction.FILTER_ACTION_INVALID -> 0
        FilterAction.FILTER_ACTION_BLOCKER_CREATE -> R.string.filter_action_create_blocker_request
        FilterAction.FILTER_ACTION_PERMISSION_CREATE -> R.string.filter_action_create_permission_request
        FilterAction.FILTER_ACTION_BLOCKER_DELETE -> R.string.filter_action_delete_blocker_request
        FilterAction.FILTER_ACTION_PERMISSION_DELETE -> R.string.filter_action_delete_permission_request
        FilterAction.FILTER_ACTION_PERMISSION_TRANSFER -> R.string.filter_action_transfer_blocker_request
        FilterAction.FILTER_ACTION_BLOCKER_TRANSFER -> R.string.filter_action_transfer_permission_request
    }
}

fun FilterAction.successRes(): Int {
    return when (this) {
        FilterAction.FILTER_ACTION_INVALID -> 0
        FilterAction.FILTER_ACTION_BLOCKER_CREATE -> R.string.filter_action_create_blocker_success
        FilterAction.FILTER_ACTION_PERMISSION_CREATE -> R.string.filter_action_create_permission_success
        FilterAction.FILTER_ACTION_BLOCKER_DELETE -> R.string.filter_action_delete_blocker_success
        FilterAction.FILTER_ACTION_PERMISSION_DELETE -> R.string.filter_action_delete_permission_success
        FilterAction.FILTER_ACTION_PERMISSION_TRANSFER -> R.string.filter_action_transfer_blocker_success
        FilterAction.FILTER_ACTION_BLOCKER_TRANSFER -> R.string.filter_action_transfer_permission_success
    }
}
