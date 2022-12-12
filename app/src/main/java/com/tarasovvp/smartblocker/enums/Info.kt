package com.tarasovvp.smartblocker.enums

import android.content.Context
import android.text.Spanned
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.extensions.htmlWithImages

enum class Info(
    val descriptionResource: Int,
    val iconResource: Int,
) {
    INFO_BLOCKER_LIST(R.string.info_blockers_list,
        R.drawable.ic_assistant_info),
    INFO_PERMISSION_LIST(R.string.empty_state_permissions_description,
        R.drawable.ic_assistant_info),
    INFO_CALL_LIST(R.string.empty_state_contacts_description,
        R.drawable.ic_assistant_info),
    INFO_CONTACT_LIST(R.string.empty_state_calls_description,
        R.drawable.ic_assistant_info),
    INFO_FILTER_ADD_FULL(R.string.filter_full_number_description,
        R.drawable.ic_assistant_info),
    INFO_FILTER_ADD_START(R.string.filter_start_description,
        R.drawable.ic_assistant_info),
    INFO_FILTER_ADD_CONTAIN(R.string.filter_contain_description,
        R.drawable.ic_assistant_info),
    INFO_NUMBER_DATA_DETAIL(R.string.empty_state_query_description,
        R.drawable.ic_assistant_info),
    INFO_FILTER_DETAIL(R.string.empty_state_account_description,
        R.drawable.ic_assistant_info),
    INFO_BLOCKED_CALL_DETAIL(R.string.empty_state_filters_by_contact_description,
        R.drawable.ic_assistant_info),
    INFO_SETTINGS(R.string.empty_state_contacts_by_blocker_description,
        R.drawable.ic_assistant_info);

    fun getHtmlWithImages(context: Context): Spanned {
        return context.htmlWithImages(context.getString(descriptionResource))
    }
}