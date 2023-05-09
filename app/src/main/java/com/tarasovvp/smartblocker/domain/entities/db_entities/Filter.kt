package com.tarasovvp.smartblocker.domain.entities.db_entities

import android.os.Parcelable
import androidx.room.*
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.DEFAULT_FILTER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTERS
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import kotlinx.parcelize.Parcelize

@Entity(tableName = FILTERS)
@Parcelize
data class Filter(
    @PrimaryKey var filter: String = String.EMPTY,
    var conditionType: Int = DEFAULT_FILTER,
    var filterType: Int = DEFAULT_FILTER,
    var filterName: String? = String.EMPTY,
    var countryCode: String = String.EMPTY,
    var country: String = String.EMPTY,
    var filterWithoutCountryCode: String = String.EMPTY,
    var created: Long? = null
) : Parcelable
