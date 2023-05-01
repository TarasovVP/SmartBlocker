package com.tarasovvp.smartblocker.presentation.ui_models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.COUNTRY_CODE_DEFAULT
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.COUNTRY_DEFAULT
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.NUMBER_FORMAT_DEFAULT
import com.tarasovvp.smartblocker.infrastructure.prefs.SharedPrefs
import com.tarasovvp.smartblocker.utils.extensions.flagEmoji
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class CountryCodeUIModel(
    var country: String = COUNTRY_DEFAULT,
    var countryCode: String = COUNTRY_CODE_DEFAULT,
    var numberFormat: String = NUMBER_FORMAT_DEFAULT
) : Parcelable {

    fun countryEmoji(): String = String.format("%s %s", country.flagEmoji(), country)

    fun countryNameEmoji(): String = String.format("%s %s", country.flagEmoji(), Locale(SharedPrefs.appLang.orEmpty(), country).displayCountry)
}
