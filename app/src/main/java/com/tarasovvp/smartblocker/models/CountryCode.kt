package com.tarasovvp.smartblocker.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude
import com.tarasovvp.smartblocker.extensions.EMPTY
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
class CountryCode(
    @PrimaryKey var country: String = String.EMPTY,
    var countryCode: String = String.EMPTY,
    var flagEmoji: String = String.EMPTY,
    var numberFormat: String = String.EMPTY,
) : Parcelable {
    @Exclude
    fun countryEmoji(): String = String.format("%s %s", flagEmoji, country)
}