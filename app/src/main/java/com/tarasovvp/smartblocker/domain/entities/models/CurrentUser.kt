package com.tarasovvp.smartblocker.domain.entities.models

import android.os.Parcelable
import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.entities.db_entities.FilteredCall
import kotlinx.parcelize.Parcelize

@Parcelize
data class CurrentUser(
    var filterList: ArrayList<Filter> = arrayListOf(),
    var filteredCallList: ArrayList<FilteredCall> = arrayListOf(),
    var isBlockerTurnOn: Boolean = true,
    var isBlockHidden: Boolean = false,
    var countryCode: CountryCode = CountryCode(),
    var isReviewVoted: Boolean = false,
) : Parcelable
