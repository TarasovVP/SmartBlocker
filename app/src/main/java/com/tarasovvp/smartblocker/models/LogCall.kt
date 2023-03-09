package com.tarasovvp.smartblocker.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.i18n.phonenumbers.Phonenumber
import com.tarasovvp.smartblocker.constants.Constants.LOG_CALLS
import com.tarasovvp.smartblocker.extensions.getPhoneNumber
import com.tarasovvp.smartblocker.extensions.isValidPhoneNumber
import com.tarasovvp.smartblocker.local.SharedPrefs
import kotlinx.parcelize.Parcelize

@Entity(tableName = LOG_CALLS, indices = [Index(value = ["callDate"], unique = true)])
@Parcelize
data class LogCall(
    @PrimaryKey override var callId: Int = 0
) : Call(), Parcelable {

    fun phoneNumberValue(): String {
        val phoneNumber = SharedPrefs.countryCode?.let { number.getPhoneNumber(it.uppercase()) }
        return if (phoneNumber.isValidPhoneNumber()) String.format("+%s%s", phoneNumber?.countryCode, phoneNumber?.nationalNumber) else number
    }
}