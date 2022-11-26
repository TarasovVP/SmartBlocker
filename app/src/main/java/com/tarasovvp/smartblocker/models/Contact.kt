package com.tarasovvp.smartblocker.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tarasovvp.smartblocker.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.extensions.*
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class Contact(
    @PrimaryKey var id: String = String.EMPTY,
    var name: String? = String.EMPTY,
    var photoUrl: String? = String.EMPTY,
    var number: String = String.EMPTY,
    var filter: Filter? = Filter(),
) : Parcelable, NumberData() {

    var trimmedPhone = number.digitsTrimmed()

    fun isBlackFilter(): Boolean {
        return filter?.filterType == BLOCKER
    }

    fun isNameEmpty(): Boolean {
        return name.isNullOrEmpty()
    }

    fun nameInitial(): String {
        return if (name.isNullOrEmpty()) String(Character.toChars(128222)) else name.nameInitial()
    }

    fun nationalNumber(country: String): String {
        return trimmedPhone.getPhoneNumber(country)?.nationalNumber.toString()
    }
}
