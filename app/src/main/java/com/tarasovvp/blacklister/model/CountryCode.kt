package com.tarasovvp.blacklister.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tarasovvp.blacklister.extensions.EMPTY
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
open class CountryCode(
    @PrimaryKey var country: String = String.EMPTY,
    var countryCode: Int? = 0,
    var flagEmoji: String? = String.EMPTY,
) : Parcelable {
    fun countryEmoji(): String = String.format("%s %s", flagEmoji, country)
}
