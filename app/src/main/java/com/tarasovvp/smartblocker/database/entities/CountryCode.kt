package com.tarasovvp.smartblocker.database.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude
import com.tarasovvp.smartblocker.constants.Constants.COUNTRY_CODES
import com.tarasovvp.smartblocker.extensions.EMPTY
import com.tarasovvp.smartblocker.local.SharedPrefs
import kotlinx.parcelize.Parcelize
import java.util.*

@Entity(tableName = COUNTRY_CODES)
@Parcelize
class CountryCode(
    @PrimaryKey var country: String = String.EMPTY,
    var countryCode: String = String.EMPTY,
    var flagEmoji: String = String.EMPTY,
    var numberFormat: String = String.EMPTY
) : Parcelable {
    @Exclude
    fun countryEmoji(): String = String.format("%s %s", flagEmoji, country)

    @Exclude
    fun countryNameEmoji(): String = String.format("%s %s", flagEmoji, Locale(SharedPrefs.appLang.orEmpty(), country).displayCountry)
}