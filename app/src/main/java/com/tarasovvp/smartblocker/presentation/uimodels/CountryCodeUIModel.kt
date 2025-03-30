package com.tarasovvp.smartblocker.presentation.uimodels

import android.os.Parcelable
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.flagEmoji
import kotlinx.parcelize.Parcelize

@Parcelize
data class CountryCodeUIModel(
    var country: String = String.EMPTY,
    var countryCode: String = String.EMPTY,
    var numberFormat: String = String.EMPTY,
    var displayCountry: String = String.EMPTY,
) : Parcelable {
    fun countryEmoji(): String = String.format("%s %s", country.flagEmoji(), country)

    fun countryNameEmoji(): String = String.format("%s %s", country.flagEmoji(), displayCountry)
}
