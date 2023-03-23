package com.tarasovvp.smartblocker.domain.models

import android.os.Parcelable
import com.tarasovvp.smartblocker.data.database.entities.Filter
import com.tarasovvp.smartblocker.data.database.entities.FilteredCall
import kotlinx.parcelize.Parcelize

@Parcelize
data class CurrentUser(
    var filterList: ArrayList<Filter> = arrayListOf(),
    var filteredCallList: ArrayList<FilteredCall> = arrayListOf()
) : Parcelable