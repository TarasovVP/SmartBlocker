package com.tarasovvp.smartblocker.domain.entities.db_views

import android.content.Context
import android.os.Parcelable
import androidx.room.*
import com.google.firebase.database.Exclude
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.domain.entities.models.NumberData
import com.tarasovvp.smartblocker.domain.enums.FilterAction
import com.tarasovvp.smartblocker.utils.PhoneNumber
import com.tarasovvp.smartblocker.utils.extensions.*
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@DatabaseView("SELECT filters.*, " +
        "(SELECT COUNT(*) FROM contacts WHERE ((filters.filter = contacts.phoneNumberValue AND filters.conditionType = 0) OR (contacts.phoneNumberValue LIKE filters.filter || '%' AND filters.conditionType = 1) OR (contacts.phoneNumberValue LIKE '%' || filters.filter || '%' AND filters.conditionType = 2))) filteredContacts, " +
        "(SELECT COUNT(*) FROM log_calls WHERE ((filters.filter = log_calls.phoneNumberValue AND filters.conditionType = 0) OR (log_calls.phoneNumberValue LIKE filters.filter || '%' AND filters.conditionType = 1) OR (log_calls.phoneNumberValue LIKE '%' || filters.filter || '%' AND filters.conditionType = 2))) + " +
        "(SELECT COUNT(*) FROM filtered_calls WHERE ((filters.filter = filtered_calls.phoneNumberValue AND filters.conditionType = 0) OR (filtered_calls.phoneNumberValue LIKE filters.filter || '%' AND filters.conditionType = 1) OR (filtered_calls.phoneNumberValue LIKE '%' || filters.filter || '%' AND filters.conditionType = 2))) filteredCalls FROM filters " +
        "LEFT JOIN country_codes ON filters.country = country_codes.country")
@Parcelize
data class FilterWithCountryCode(
    @Embedded
    var filter: Filter? = null,
    @ColumnInfo(name = "filteredContacts")
    var filteredContacts: Int? = 0,
    @ColumnInfo(name = "filteredCalls")
    var filteredCalls: Int? = 0,
    @Relation(
        parentColumn = "country",
        entityColumn = "country",
        entity = CountryCode::class)
    var countryCode: CountryCode? = null
) : Parcelable, NumberData() {
    @Exclude
    fun filterActionText(context: Context): String {
        return filterAction?.let { action ->
            context.getString(if (isInvalidFilterAction()) {
                when {
                    isTypeContain().isTrue() && filter?.filter?.isEmpty().isTrue() -> R.string.filter_action_create_number_empty
                    isTypeFull().isTrue() && filter?.filter?.length.orZero() < countryCode?.numberFormat.digitsTrimmed().length -> R.string.filter_action_create_number_incomplete
                    else -> action.descriptionText()
                }
            } else {
                action.descriptionText()
            })
        }.orEmpty()
    }

    @Exclude
    fun conditionTypeFullHint(): String {
        return countryCode?.numberFormat?.replace(Regex("\\d"), Constants.MASK_CHAR.toString()).orEmpty()
    }

    @Exclude
    fun conditionTypeStartHint(): String {
        return countryCode?.numberFormat?.filter { it.isDigit() }
            ?.replace(Regex("\\d"), Constants.MASK_CHAR.toString())
            ?.replaceFirst(Constants.MASK_CHAR.toString(), String.EMPTY).orEmpty()
    }

    @Exclude
    fun createFilter(): String {
        return when {
            isTypeContain().isTrue() -> filter?.filter.orEmpty()
            else -> String.format("%s%s", countryCode?.countryCode, filterToInput())
        }
    }

    @Exclude
    fun createFilterValue(context: Context): String {
        return when {
            isTypeContain().isTrue() -> filter?.filter?.ifEmpty { context.getString(R.string.creating_filter_no_data) }.orEmpty()
            else -> String.format("%s%s", countryCode?.countryCode, filterToInput())
        }
    }

    @Exclude
    fun extractFilterWithoutCountryCode(): String {
        return when (filter?.filter) {
            createFilter() -> filter?.filter?.replace(countryCode?.countryCode.orEmpty(), String.EMPTY).orEmpty()
            else -> filter?.filter.orEmpty()
        }
    }

    @Exclude
    fun filterToInput(): String {
        //TODO
        val phoneNumber = PhoneNumber()
        return when (filter?.conditionType) {
            FilterCondition.FILTER_CONDITION_FULL.ordinal -> (if (phoneNumber.getPhoneNumber(filter?.filter, countryCode?.country.orEmpty()).isNull()) phoneNumber.getPhoneNumber(filter?.filter.digitsTrimmed().replace(Constants.PLUS_CHAR.toString(), String.EMPTY), countryCode?.country.orEmpty()) else phoneNumber.getPhoneNumber(filter?.filter, countryCode?.country.orEmpty()))?.nationalNumber.toString()
            FilterCondition.FILTER_CONDITION_START.ordinal -> filter?.filter?.replaceFirst(countryCode?.countryCode.orEmpty(), String.EMPTY).orEmpty()
            else -> filter?.filter.digitsTrimmed().replace(Constants.PLUS_CHAR.toString(), String.EMPTY)
        }
    }

    @Exclude
    fun isInValidPhoneNumber(phoneNumber: PhoneNumber): Boolean {
        return (isTypeFull() && phoneNumber.isPhoneNumberValid(filter?.filter, countryCode?.country.orEmpty()))
                || (isTypeStart().not() && filter?.filter.orEmpty().isEmpty())
    }

    @IgnoredOnParcel
    @Ignore
    @get:Exclude
    var isCheckedForDelete = false

    @IgnoredOnParcel
    @Ignore
    @get:Exclude
    var isDeleteMode = false

    @IgnoredOnParcel
    @Ignore
    @get:Exclude
    var filterAction: FilterAction? = null

    @Exclude
    fun filterTypeTitle(): Int {
        return when (filter?.filterType) {
            Constants.PERMISSION -> R.string.filter_type_permission
            else -> R.string.filter_type_blocker
        }
    }

    @Exclude
    fun filterTypeIcon(): Int {
        return when (filter?.filterType) {
            Constants.PERMISSION -> R.drawable.ic_permission
            else -> R.drawable.ic_blocker
        }
    }

    @Exclude
    fun filterTypeTint(): Int {
        return when (filter?.filterType) {
            Constants.PERMISSION -> R.color.islamic_green
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
        return context.resources.getQuantityString(when (filter?.filterType) {
            Constants.PERMISSION -> R.plurals.details_number_permit_contacts
            else -> R.plurals.details_number_block_contacts
        }, filteredContacts?.quantityString().orZero(), filteredContacts)
    }

    @Exclude
    fun filteredCallsText(context: Context): String {
        return context.resources.getQuantityString(when (filter?.filterType) {
            Constants.PERMISSION -> R.plurals.details_number_permitted_calls
            else -> R.plurals.details_number_blocked_calls
        }, filteredCalls?.quantityString().orZero(), filteredCalls)
    }

    @Exclude
    fun conditionTypeName(): Int {
        return FilterCondition.values()[filter?.conditionType.orZero()].title()
    }

    @Exclude
    fun conditionTypeIcon(): Int {
        return FilterCondition.values()[filter?.conditionType.orZero()].mainIcon()
    }

    @Exclude
    fun conditionTypeSmallIcon(): Int? {
        return filter?.conditionType?.takeIf { it >= 0 }?.let { FilterCondition.values()[filter?.conditionType.orZero()].smallIcon(isBlocker()) }
    }

    @Exclude
    fun filterCreatedDate(): String {
        return filter?.created?.let { SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault()).format(it) }
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
        return filter?.conditionType == FilterCondition.FILTER_CONDITION_START.ordinal
    }

    @Exclude
    fun isTypeFull(): Boolean {
        return filter?.conditionType == FilterCondition.FILTER_CONDITION_FULL.ordinal
    }

    @Exclude
    fun isTypeContain(): Boolean {
        return filter?.conditionType == FilterCondition.FILTER_CONDITION_CONTAIN.ordinal
    }

    @Exclude
    fun isBlocker(): Boolean {
        return filter?.filterType == Constants.BLOCKER
    }

    @Exclude
    fun isPermission(): Boolean {
        return filter?.filterType == Constants.PERMISSION
    }
}