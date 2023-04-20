package com.tarasovvp.smartblocker.domain.models.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.LOG_CALLS
import com.tarasovvp.smartblocker.utils.extensions.getPhoneNumber
import com.tarasovvp.smartblocker.utils.extensions.isValidPhoneNumber
import com.tarasovvp.smartblocker.infrastructure.prefs.SharedPrefs
import kotlinx.parcelize.Parcelize

@Entity(tableName = LOG_CALLS, indices = [Index(value = ["callDate"], unique = true)])
@Parcelize
data class LogCall(
    @PrimaryKey override var callId: Int = 0
) : Call(), Parcelable {

    fun phoneNumberValue(): String {
        val phoneNumber = SharedPrefs.countryCode?.country?.let { number.getPhoneNumber(it.uppercase()) }
        return if (phoneNumber.isValidPhoneNumber()) String.format("+%s%s", phoneNumber?.countryCode, phoneNumber?.nationalNumber) else number
    }
}