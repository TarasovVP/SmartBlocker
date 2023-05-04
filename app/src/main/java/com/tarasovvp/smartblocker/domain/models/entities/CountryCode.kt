package com.tarasovvp.smartblocker.domain.models.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.COUNTRY_CODES
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.COUNTRY_CODE_DEFAULT
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.COUNTRY_DEFAULT
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.NUMBER_FORMAT_DEFAULT
import kotlinx.parcelize.Parcelize

@Entity(tableName = COUNTRY_CODES)
@Parcelize
data class CountryCode(
    @PrimaryKey var country: String = COUNTRY_DEFAULT,
    var countryCode: String = COUNTRY_CODE_DEFAULT,
    var numberFormat: String = NUMBER_FORMAT_DEFAULT
) : Parcelable
