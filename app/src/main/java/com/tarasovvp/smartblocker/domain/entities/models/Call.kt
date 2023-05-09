package com.tarasovvp.smartblocker.domain.entities.models

import android.os.Parcelable
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.DEFAULT_FILTER
import com.tarasovvp.smartblocker.utils.extensions.*
import kotlinx.parcelize.Parcelize

@Parcelize
open class Call(
    open var callId: Int = 0,
    var callName: String? = String.EMPTY,
    var number: String = String.EMPTY,
    var type: String? = String.EMPTY,
    var callDate: String? = String.EMPTY,
    var photoUrl: String? = String.EMPTY,
    var isFilteredCall: Boolean? = false,
    var filteredNumber: String = String.EMPTY,
    var filteredConditionType: Int = DEFAULT_FILTER,
    var phoneNumberValue: String = String.EMPTY,
    var isPhoneNumberValid: Boolean = false
) : Parcelable