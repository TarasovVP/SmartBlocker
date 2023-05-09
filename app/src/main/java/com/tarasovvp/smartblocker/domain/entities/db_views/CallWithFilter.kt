package com.tarasovvp.smartblocker.domain.entities.db_views

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcelable
import androidx.core.content.ContextCompat
import androidx.room.DatabaseView
import androidx.room.Embedded
import androidx.room.Ignore
import com.google.firebase.database.Exclude
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.data.prefs.SharedPrefs
import com.tarasovvp.smartblocker.domain.entities.models.Call
import com.tarasovvp.smartblocker.domain.entities.models.NumberData
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.utils.extensions.*
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@DatabaseView("SELECT log_calls.*, filters.* FROM log_calls LEFT JOIN filters ON (filters.filter = log_calls.phoneNumberValue AND filters.conditionType = 0) OR (log_calls.phoneNumberValue LIKE filters.filter || '%' AND filters.conditionType = 1) OR (log_calls.phoneNumberValue LIKE '%' || filters.filter || '%' AND filters.conditionType = 2) WHERE filters.filter = (SELECT filter FROM filters WHERE log_calls.phoneNumberValue LIKE filter || '%' OR log_calls.phoneNumberValue LIKE '%' || filter || '%' ORDER BY LENGTH(filter) DESC LIMIT 1) OR filters.filter IS NULL " +
        "UNION SELECT filtered_calls.*, filters.* FROM filtered_calls LEFT JOIN filters ON (filters.filter = filtered_calls.phoneNumberValue AND filters.conditionType = 0) OR (filtered_calls.phoneNumberValue LIKE filters.filter || '%' AND filters.conditionType = 1) OR (filtered_calls.phoneNumberValue LIKE '%' || filters.filter || '%' AND filters.conditionType = 2) WHERE filters.filter = (SELECT filter FROM filters WHERE filtered_calls.phoneNumberValue LIKE filter || '%' OR filtered_calls.phoneNumberValue LIKE '%' || filter || '%' ORDER BY LENGTH(filter) DESC LIMIT 1) OR filters.filter IS NULL")
@Parcelize
open class CallWithFilter(
    @Embedded
    var call: Call? = Call(),
    @Embedded
    var filterWithCountryCode: FilterWithCountryCode? = FilterWithCountryCode()
) : Parcelable, NumberData() {

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
    var isExtract = false

    @IgnoredOnParcel
    @Ignore
    @get:Exclude
    var isFilteredCallDetails = false

    @Exclude
    fun isNameEmpty(): Boolean {
        return call?.callName.isNullOrEmpty()
    }

    @Exclude
    fun callIcon(): Int {
        return when (call?.type) {
            Constants.IN_COMING_CALL -> if (isCallFiltered()) R.drawable.ic_call_incoming_permitted else R.drawable.ic_call_incoming
            Constants.MISSED_CALL -> if (isCallFiltered()) R.drawable.ic_call_missed_permitted else R.drawable.ic_call_missed
            Constants.REJECTED_CALL -> if (isCallFiltered()) R.drawable.ic_call_rejected_permitted else R.drawable.ic_call_rejected
            Constants.BLOCKED_CALL -> R.drawable.ic_call_blocked
            else -> R.drawable.ic_call_outcoming
        }
    }

    @Exclude
    fun timeFromCallDate(): String? {
        return call?.callDate?.toDateFromMilliseconds(Constants.TIME_FORMAT)
    }

    @Exclude
    fun dateFromCallDate(): String? {
        return call?.callDate?.toDateFromMilliseconds(Constants.DATE_FORMAT)
    }

    @Exclude
    fun placeHolder(context: Context): Drawable? {
        return if (call?.callName.isNullOrEmpty()) ContextCompat.getDrawable(context,
            R.drawable.ic_call) else if (call?.callName.nameInitial()
                .isEmpty()
        ) ContextCompat.getDrawable(context,
            R.drawable.ic_contact) else context.getInitialDrawable(call?.callName.nameInitial())
    }

    @Exclude
    private fun dateTimeFromCallDate(): String {
        return String.format("%s, %s", dateFromCallDate(), timeFromCallDate())
    }

    @Exclude
    fun isBlockedCall(): Boolean {
        return call?.type == Constants.BLOCKED_CALL
    }

    @Exclude
    fun isPermittedCall(): Boolean {
        return isCallFiltered() && isBlockedCall().not()
    }

    @Exclude
    fun isCallFiltered(): Boolean {
        return call?.isFilteredCall.isTrue() || isFilteredCallDetails
    }

    @Exclude
    fun isFilteredCallDelete(): Boolean {
        return (isCallFiltered() || call?.number.orEmpty().isEmpty()) && isDeleteMode
    }

    @Exclude
    fun callFilterValue(): String {
        return when {
            isFilteredCallDetails -> dateTimeFromCallDate()
            isExtract.not() && isCallFiltered() -> call?.filteredNumber.orEmpty()
            //isExtract && isFilterNullOrEmpty().not() -> filter.orEmpty()
            else -> String.EMPTY
        }
    }

    @Exclude
    fun callFilterTitle(filter: FilterWithCountryCode?): Int {
        return if (isExtract && !isFilteredCallDetails) {
            when {
                filter?.isPermission().isTrue() -> R.string.details_number_permit_with_filter
                filter?.isBlocker().isTrue() -> R.string.details_number_block_with_filter
                call?.number.orEmpty().isEmpty() && SharedPrefs.blockHidden.isTrue() -> R.string.details_number_hidden_on
                call?.number.orEmpty().isEmpty() && SharedPrefs.blockHidden.isNotTrue() -> R.string.details_number_hidden_off
                else -> R.string.details_number_contact_without_filter
            }
        } else {
            if (isCallFiltered()) {
                when {
                    call?.number.orEmpty().isEmpty() -> R.string.details_number_blocked_by_settings
                    isPermittedCall() -> R.string.details_number_permitted_by_filter
                    else -> R.string.details_number_blocked_by_filter
                }
            } else {
                R.string.details_number_call_without_filter
            }
        }
    }

    @Exclude
    fun callFilterIcon(): Int? {
        return when {
            isCallFiltered() && call?.filteredNumber.orEmpty().isEmpty() -> R.drawable.ic_settings_small
            isCallFiltered() -> FilterCondition.values()[call?.filteredConditionType.orZero()].smallIcon(call?.type == Constants.BLOCKED_CALL)
            else -> null
        }
    }

    @Exclude
    fun callFilterTint(filter: FilterWithCountryCode?): Int {
        return when {
            (isCallFiltered() && isBlockedCall()) || (isExtract && filter?.isBlocker().isTrue()) -> R.color.sunset
            (isCallFiltered() && isPermittedCall()) || (isExtract && filter?.isPermission().isTrue()) -> R.color.islamic_green
            else -> R.color.text_color_grey
        }
    }
}

