package com.tarasovvp.blacklister.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude
import com.tarasovvp.blacklister.constants.Constants.BLACK_FILTER
import com.tarasovvp.blacklister.extensions.*
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
        return filter?.filterType == BLACK_FILTER
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
