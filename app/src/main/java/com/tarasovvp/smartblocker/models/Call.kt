package com.tarasovvp.smartblocker.models

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcelable
import androidx.core.content.ContextCompat
import androidx.room.Ignore
import com.google.firebase.database.Exclude
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants.BLOCKED_CALL
import com.tarasovvp.smartblocker.constants.Constants.DATE_FORMAT
import com.tarasovvp.smartblocker.constants.Constants.MISSED_CALL
import com.tarasovvp.smartblocker.constants.Constants.IN_COMING_CALL
import com.tarasovvp.smartblocker.constants.Constants.REJECTED_CALL
import com.tarasovvp.smartblocker.constants.Constants.TIME_FORMAT
import com.tarasovvp.smartblocker.enums.FilterCondition
import com.tarasovvp.smartblocker.extensions.*
import com.tarasovvp.smartblocker.local.SharedPrefs
import kotlinx.parcelize.Parcelize

@Parcelize
open class Call(
    open var callId: Int = 0,
    var callName: String? = String.EMPTY,
    var number: String = String.EMPTY,
    var normalizedNumber: String? = String.EMPTY,
    var type: String? = String.EMPTY,
    var callDate: String? = String.EMPTY,
    var photoUrl: String? = String.EMPTY,
    var countryIso: String? = String.EMPTY,
    var filter: String? = String.EMPTY
) : Parcelable {

    @Ignore
    @get:Exclude
    var isCheckedForDelete = false

    @Ignore
    @get:Exclude
    var isDeleteMode = false

    @Ignore
    @get:Exclude
    var isExtract = false

    @Ignore
    @get:Exclude
    var isFilteredCallDetails = false

    @Exclude
    fun isNameEmpty(): Boolean {
        return callName.isNullOrEmpty()
    }

    @Exclude
    fun callIcon(): Int {
        return when (type) {
            IN_COMING_CALL -> if (isFilteredNotNullOrEmpty()) R.drawable.ic_call_incoming_permitted else R.drawable.ic_call_incoming
            MISSED_CALL -> if (isFilteredNotNullOrEmpty()) R.drawable.ic_call_missed_permitted else R.drawable.ic_call_missed
            REJECTED_CALL -> if (isFilteredNotNullOrEmpty()) R.drawable.ic_call_rejected_permitted else R.drawable.ic_call_rejected
            BLOCKED_CALL -> R.drawable.ic_call_blocked
            else -> R.drawable.ic_call_outcoming
        }
    }

    @Exclude
    fun timeFromCallDate(): String? {
        return callDate?.toDateFromMilliseconds(TIME_FORMAT)
    }

    @Exclude
    fun dateFromCallDate(): String? {
        return callDate?.toDateFromMilliseconds(DATE_FORMAT)
    }

    @Exclude
    fun placeHolder(context: Context): Drawable? {
        return if (callName.isNullOrEmpty()) ContextCompat.getDrawable(context,
            R.drawable.ic_call) else if (callName.nameInitial()
                .isEmpty()
        ) ContextCompat.getDrawable(context,
            R.drawable.ic_contact) else context.getInitialDrawable(callName.nameInitial())
    }

    @Exclude
    private fun dateTimeFromCallDate(): String {
        return String.format("%s, %s", dateFromCallDate(), timeFromCallDate())
    }

    @Exclude
    fun isBlockedCall(): Boolean {
        return type == BLOCKED_CALL
    }

    @Exclude
    fun isPermittedCall(): Boolean {
        return isFilteredNotNullOrEmpty() && isBlockedCall().not()
    }

    @Exclude
    fun filterValue(): String {
        return filter.orEmpty()
    }

    @Exclude
    fun isFilterNullOrEmpty(): Boolean {
        return filter.isNullOrEmpty().isTrue()
    }

    @Exclude
    fun isFilteredNotNullOrEmpty(): Boolean {
        return this is FilteredCall && filterNumber.isNotEmpty()
    }

    @Exclude
    fun isFilteredCallDelete(): Boolean {
        return this is FilteredCall && (isFilteredNotNullOrEmpty() || number.isEmpty()) && isDeleteMode
    }

    @Exclude
    fun callFilterValue(): String {
        return when {
            isFilteredCallDetails -> dateTimeFromCallDate()
            isExtract.not() && this is FilteredCall && isFilteredNotNullOrEmpty() -> filterNumber
            isExtract && isFilterNullOrEmpty().not() -> filter.orEmpty()
            else -> String.EMPTY
        }
    }

    @Exclude
    fun callFilterTitle(): Int {
        return if (isExtract && !isFilteredCallDetails) {
            when {
                /*filter?.isPermission().isTrue() -> R.string.details_number_permit_with_filter
                filter?.isBlocker().isTrue() -> R.string.details_number_block_with_filter*/
                number.isEmpty() && SharedPrefs.blockHidden -> R.string.details_number_hidden_on
                number.isEmpty() && SharedPrefs.blockHidden.not() -> R.string.details_number_hidden_off
                else -> R.string.details_number_contact_without_filter
            }
        } else {
            if (this is FilteredCall) {
                when {
                    number.isEmpty() -> R.string.details_number_blocked_by_settings
                    isBlockedCall().not() -> R.string.details_number_permitted_by_filter
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
            isExtract.not() && this is FilteredCall && isFilteredNotNullOrEmpty().not() -> R.drawable.ic_settings_small
            isExtract.not() && this is FilteredCall && isFilteredNotNullOrEmpty() -> FilterCondition.getSmallIconByIndex(conditionType, isBlockedCall())
            //isExtract && isFilterNullOrEmpty().not() -> filter?.conditionTypeSmallIcon()
            else -> null
        }
    }

    @Exclude
    fun callFilterTint(): Int {
        return when {
            isExtract.not() && this is FilteredCall && isBlockedCall() -> R.color.sunset
            isExtract.not() && this is FilteredCall && isBlockedCall().not() -> R.color.islamic_green
            /*isExtract && filter?.isBlocker().isTrue() -> R.color.sunset
            isExtract && filter?.isPermission().isTrue() -> R.color.islamic_green*/
            else -> R.color.text_color_grey
        }
    }
}
