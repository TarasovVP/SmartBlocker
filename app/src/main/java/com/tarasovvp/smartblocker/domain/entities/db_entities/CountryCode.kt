package com.tarasovvp.smartblocker.domain.entities.db_entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.Exclude
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.COUNTRY_CODES
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.COUNTRY_CODE_DEFAULT
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.COUNTRY_DEFAULT
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.NUMBER_FORMAT_DEFAULT
import com.tarasovvp.smartblocker.infrastructure.prefs.SharedPrefs
import com.tarasovvp.smartblocker.utils.extensions.flagEmoji
import kotlinx.parcelize.Parcelize
import java.util.*

@Entity(tableName = COUNTRY_CODES)
@Parcelize
data class CountryCode(
    @PrimaryKey var country: String = COUNTRY_DEFAULT,
    var countryCode: String = COUNTRY_CODE_DEFAULT,
    var numberFormat: String = NUMBER_FORMAT_DEFAULT
) : Parcelable {

    @Exclude
    fun countryEmoji(): String = String.format("%s %s", country.flagEmoji(), country)

    @Exclude
    fun countryNameEmoji(): String = String.format("%s %s", country.flagEmoji(), Locale(SharedPrefs.appLang.orEmpty(), country).displayCountry)
}
