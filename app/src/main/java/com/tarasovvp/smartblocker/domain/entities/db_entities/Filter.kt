package com.tarasovvp.smartblocker.domain.entities.db_entities

import android.content.Context
import android.os.Parcelable
import androidx.room.*
import com.google.firebase.database.Exclude
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.DATE_FORMAT
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.DEFAULT_FILTER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTERS
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PERMISSION
import com.tarasovvp.smartblocker.domain.enums.FilterAction
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.quantityString
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = FILTERS)
@Parcelize
data class Filter(
    @PrimaryKey var filter: String = String.EMPTY,
    var conditionType: Int = DEFAULT_FILTER,
    var filterType: Int = DEFAULT_FILTER,
    var filterName: String? = String.EMPTY,
    var countryCode: String = String.EMPTY,
    var country: String = String.EMPTY,
    var filterWithoutCountryCode: String = String.EMPTY,
    var created: Long? = null
) : Parcelable {

    @IgnoredOnParcel
    @get:Exclude
    var filteredContacts: Int = 0

    @IgnoredOnParcel
    @get:Exclude
    var filteredCalls: Int = 0

    @IgnoredOnParcel
    @get:Exclude
    var isCheckedForDelete = false

    @IgnoredOnParcel
    @get:Exclude
    var isDeleteMode = false

    @IgnoredOnParcel
    @Ignore
    @get:Exclude
    var filterAction: FilterAction? = null

    @Exclude
    fun filterTypeTitle(): Int {
        return when (filterType) {
            PERMISSION -> R.string.filter_type_permission
            else -> R.string.filter_type_blocker
        }
    }

    @Exclude
    fun filterTypeIcon(): Int {
        return when (filterType) {
            PERMISSION -> R.drawable.ic_permission
            else -> R.drawable.ic_blocker
        }
    }

    @Exclude
    fun filterTypeTint(): Int {
        return when (filterType) {
            PERMISSION -> R.color.islamic_green
            else -> R.color.sunset
        }
    }

    @Exclude
    fun filterDetailTint(): Int {
        return if (isDeleteFilterAction()) R.color.sunset else R.color.text_color_grey
    }

    @Exclude
    fun filterActionTextTint(): Int {
        return if (isCreateFilterAction()) R.color.white else filterAction?.color() ?: R.color.white
    }

    @Exclude
    fun filterActionBgTint(): Int {
        return if (isCreateFilterAction()) R.color.button_bg else R.color.transparent
    }

    @Exclude
    fun filteredContactsText(context: Context): String {
        return context.resources.getQuantityString(when (filterType) {
            PERMISSION -> R.plurals.details_number_permit_contacts
            else -> R.plurals.details_number_block_contacts
        }, filteredContacts.quantityString(), filteredContacts)
    }

    @Exclude
    fun filteredCallsText(context: Context): String {
        return context.resources.getQuantityString(when (filterType) {
            PERMISSION -> R.plurals.details_number_permitted_calls
            else -> R.plurals.details_number_blocked_calls
        }, filteredCalls.quantityString(), filteredCalls)
    }

    @Exclude
    fun conditionTypeName(): Int {
        return FilterCondition.values()[conditionType].title()
    }

    @Exclude
    fun conditionTypeIcon(): Int {
        return FilterCondition.values()[conditionType].mainIcon()
    }

    @Exclude
    fun conditionTypeSmallIcon(): Int? {
        return conditionType.takeIf { it >= 0 }?.let { FilterCondition.values()[conditionType].smallIcon(isBlocker()) }
    }

    @Exclude
    fun filterCreatedDate(): String {
        return created?.let { SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(it) }
            .orEmpty()
    }

    @Exclude
    fun isInvalidFilterAction(): Boolean {
        return filterAction == FilterAction.FILTER_ACTION_INVALID
    }

    @Exclude
    fun isCreateFilterAction(): Boolean {
        return filterAction == FilterAction.FILTER_ACTION_BLOCKER_CREATE || filterAction == FilterAction.FILTER_ACTION_PERMISSION_CREATE
    }

    @Exclude
    fun isDeleteFilterAction(): Boolean {
        return filterAction == FilterAction.FILTER_ACTION_BLOCKER_DELETE || filterAction == FilterAction.FILTER_ACTION_PERMISSION_DELETE
    }

    @Exclude
    fun isChangeFilterAction(): Boolean {
        return filterAction == FilterAction.FILTER_ACTION_BLOCKER_TRANSFER || filterAction == FilterAction.FILTER_ACTION_PERMISSION_TRANSFER
    }

    @Exclude
    fun isTypeStart(): Boolean {
        return conditionType == FilterCondition.FILTER_CONDITION_START.ordinal
    }

    @Exclude
    fun isTypeFull(): Boolean {
        return conditionType == FilterCondition.FILTER_CONDITION_FULL.ordinal
    }

    @Exclude
    fun isTypeContain(): Boolean {
        return conditionType == FilterCondition.FILTER_CONDITION_CONTAIN.ordinal
    }

    @Exclude
    fun isBlocker(): Boolean {
        return filterType == BLOCKER
    }

    @Exclude
    fun isPermission(): Boolean {
        return filterType == PERMISSION
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Filter) {
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
