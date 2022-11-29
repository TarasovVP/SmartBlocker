package com.tarasovvp.smartblocker.models

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Parcelable
import androidx.core.content.ContextCompat
import androidx.room.Embedded
import androidx.room.Ignore
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants
import com.tarasovvp.smartblocker.constants.Constants.BLOCKED_CALL
import com.tarasovvp.smartblocker.constants.Constants.DATE_FORMAT
import com.tarasovvp.smartblocker.constants.Constants.MISSED_CALL
import com.tarasovvp.smartblocker.constants.Constants.OUT_COMING_CALL
import com.tarasovvp.smartblocker.constants.Constants.PERMITTED_CALL
import com.tarasovvp.smartblocker.constants.Constants.REJECTED_CALL
import com.tarasovvp.smartblocker.enums.FilterCondition
import com.tarasovvp.smartblocker.extensions.EMPTY
import com.tarasovvp.smartblocker.extensions.isTrue
import com.tarasovvp.smartblocker.extensions.nameInitial
import com.tarasovvp.smartblocker.extensions.toDateFromMilliseconds
import kotlinx.android.parcel.Parcelize

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
    @Embedded(prefix = "filter_") var filter: Filter? = Filter(),
) : Parcelable, NumberData() {
    @Ignore
    var isCheckedForDelete = false
    @Ignore
    var isDeleteMode = false
    @Ignore
    var isExtract = false
    @Ignore
    var isFilteredCallDetails = false

    fun isNameEmpty(): Boolean {
        return callName.isNullOrEmpty()
    }

    fun callIcon(): Int {
        return when (type) {
            OUT_COMING_CALL -> R.drawable.ic_outcoming_call
            MISSED_CALL -> R.drawable.ic_missed_call
            REJECTED_CALL -> R.drawable.ic_rejected_call
            BLOCKED_CALL -> R.drawable.ic_blocked_call
            PERMITTED_CALL -> R.drawable.ic_allowed_call
            else -> R.drawable.ic_incoming_call
        }
    }

    fun timeFromCallDate(): String? {
        return callDate?.toDateFromMilliseconds(Constants.TIME_FORMAT)
    }

    fun dateFromCallDate(): String? {
        return callDate?.toDateFromMilliseconds(DATE_FORMAT)
    }

    fun placeHolder(context: Context): Drawable? {
        return ContextCompat.getDrawable(context, R.drawable.ic_call_list)
    }

    fun dateTimeFromCallDate(): String {
        return String.format("%s, %s", dateFromCallDate(), timeFromCallDate())
    }

    fun isBlockedCall(): Boolean {
        return type == BLOCKED_CALL
    }

    fun isPermittedCall(): Boolean {
        return type == PERMITTED_CALL
    }

    fun nameInitial(): String {
        return if (callName.isNullOrEmpty()) String(Character.toChars(128222)) else callName.nameInitial()
    }

    fun filterConditionTypeIcon(context: Context): Drawable {
        return when (this) {
            is FilteredCall -> filter?.conditionType?.let { blockFilterCondition ->
                FilterCondition.getSmallIconByIndex(blockFilterCondition,
                    filter?.isBlackFilter().isTrue())
            }?.let {
                ContextCompat.getDrawable(context, it)
            } ?: ColorDrawable(Color.TRANSPARENT)
            else -> ColorDrawable(Color.TRANSPARENT)
        }
    }

    fun filterValue(): String {
        return filter?.filter.orEmpty()
    }
}
