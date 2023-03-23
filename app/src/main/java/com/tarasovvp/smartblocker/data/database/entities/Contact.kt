package com.tarasovvp.smartblocker.data.database.entities

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcelable
import androidx.core.content.ContextCompat
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.CONTACTS
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PLUS_CHAR
import com.tarasovvp.smartblocker.infrastructure.prefs.SharedPrefs
import com.tarasovvp.smartblocker.utils.extensions.*
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Entity(tableName = CONTACTS)
@Parcelize
data class Contact(
    @PrimaryKey var id: String = String.EMPTY,
    var name: String? = String.EMPTY,
    var photoUrl: String? = String.EMPTY,
    var number: String = String.EMPTY,
    var filter: String = String.EMPTY
) : Parcelable {

    @IgnoredOnParcel
    var trimmedPhone = number.digitsTrimmed()

    fun isNameEmpty(): Boolean {
        return name.isNullOrEmpty()
    }

    fun placeHolder(context: Context): Drawable? {
        return if (name.nameInitial().isEmpty()) ContextCompat.getDrawable(context,
            R.drawable.ic_contact) else context.getInitialDrawable(name.nameInitial())
    }

    fun isFilterNullOrEmpty(): Boolean {
        return filter.isEmpty()
    }

    fun phoneNumber(): PhoneNumber? {
        return SharedPrefs.country?.let { trimmedPhone.getPhoneNumber(it.uppercase()) }
    }

    fun phoneNumberValue(): String {
        val phoneNumber = phoneNumber()
        return if (phoneNumber.isValidPhoneNumber()) String.format("+%s%s", phoneNumber?.countryCode, phoneNumber?.nationalNumber) else number
    }

    fun phoneNumberValidity(): Int? {
        return when {
            phoneNumber().isValidPhoneNumber().not() -> R.string.details_number_invalid
            number.startsWith(PLUS_CHAR).not() -> R.string.details_number_incomplete
            else -> null
        }
    }

}
