package com.tarasovvp.smartblocker.presentation.ui_models

import android.content.Context
import android.os.Parcelable
import androidx.room.*
import com.google.firebase.database.Exclude
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.DATE_FORMAT
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.DEFAULT_FILTER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PERMISSION
import com.tarasovvp.smartblocker.domain.enums.FilterAction
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.quantityString
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
data class FilterUIModel(
    var filter: String = String.EMPTY,
    var conditionType: Int = DEFAULT_FILTER,
    var filterType: Int = DEFAULT_FILTER,
    var name: String? = String.EMPTY,
    var countryCode: String = String.EMPTY,
    var country: String = String.EMPTY,
    var filterWithoutCountryCode: String = String.EMPTY,
    var created: Long? = null
) : Parcelable {

    @IgnoredOnParcel
    var filteredContacts: Int = 0

    @IgnoredOnParcel
    var filteredCalls: Int = 0

    @IgnoredOnParcel
    var isCheckedForDelete = false

    @IgnoredOnParcel
    var isDeleteMode = false

    @IgnoredOnParcel
    var filterAction: FilterAction? = null

    @Exclude
    fun filterTypeTitle(): Int {
        return when (filterType) {
            PERMISSION -> R.string.filter_type_permission
            else -> R.string.filter_type_blocker
        }
    }

    fun filterTypeIcon(): Int {
        return when (filterType) {
            PERMISSION -> R.drawable.ic_permission
            else -> R.drawable.ic_blocker
        }
    }

    fun filterTypeTint(): Int {
        return when (filterType) {
            PERMISSION -> R.color.islamic_green
            else -> R.color.sunset
        }
    }

    fun filterDetailTint(): Int {
        return if (isDeleteFilterAction()) R.color.sunset else R.color.text_color_grey
    }

    fun filterActionTextTint(): Int {
        return if (isCreateFilterAction()) R.color.white else filterAction?.color() ?: R.color.white
    }

    fun filterActionBgTint(): Int {
        return if (isCreateFilterAction()) R.color.button_bg else R.color.transparent
    }

    fun filteredContactsText(context: Context): String {
        return context.resources.getQuantityString(when (filterType) {
            PERMISSION -> R.plurals.details_number_permit_contacts
            else -> R.plurals.details_number_block_contacts
        }, filteredContacts.quantityString(), filteredContacts)
    }

    fun filteredCallsText(context: Context): String {
        return context.resources.getQuantityString(when (filterType) {
            PERMISSION -> R.plurals.details_number_permitted_calls
            else -> R.plurals.details_number_blocked_calls
        }, filteredCalls.quantityString(), filteredCalls)
    }

    fun conditionTypeName(): Int {
        return FilterCondition.values()[conditionType].title()
    }

    fun conditionTypeIcon(): Int {
        return FilterCondition.values()[conditionType].mainIcon()
    }

    fun conditionTypeSmallIcon(): Int {
        return FilterCondition.values()[conditionType].smallIcon(isBlocker())
    }

    fun filterCreatedDate(): String {
        return created?.let { SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(it) }
            .orEmpty()
    }

    fun isInvalidFilterAction(): Boolean {
        return filterAction == FilterAction.FILTER_ACTION_INVALID
    }

    fun isCreateFilterAction(): Boolean {
        return filterAction == FilterAction.FILTER_ACTION_BLOCKER_CREATE || filterAction == FilterAction.FILTER_ACTION_PERMISSION_CREATE
    }

    fun isDeleteFilterAction(): Boolean {
        return filterAction == FilterAction.FILTER_ACTION_BLOCKER_DELETE || filterAction == FilterAction.FILTER_ACTION_PERMISSION_DELETE
    }

    fun isChangeFilterAction(): Boolean {
        return filterAction == FilterAction.FILTER_ACTION_BLOCKER_TRANSFER || filterAction == FilterAction.FILTER_ACTION_PERMISSION_TRANSFER
    }

    fun isTypeStart(): Boolean {
        return conditionType == FilterCondition.FILTER_CONDITION_START.ordinal
    }

    fun isTypeFull(): Boolean {
        return conditionType == FilterCondition.FILTER_CONDITION_FULL.ordinal
    }

    fun isTypeContain(): Boolean {
        return conditionType == FilterCondition.FILTER_CONDITION_CONTAIN.ordinal
    }

    fun isBlocker(): Boolean {
        return filterType == BLOCKER
    }

    fun isPermission(): Boolean {
        return filterType == PERMISSION
    }

    override fun equals(other: Any?): Boolean {
        return if (other is FilterUIModel) {
            this.filter == other.filter && this.conditionType == other.conditionType && this.filterType == other.filterType && this.filteredContacts == other.filteredContacts
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = filter.hashCode()
        result = 31 * result + conditionType
        result = 31 * result + filterType
        result = 31 * result + filteredContacts
        return result
    }
}
