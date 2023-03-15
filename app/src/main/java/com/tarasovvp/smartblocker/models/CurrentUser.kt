package com.tarasovvp.smartblocker.models

import android.os.Parcelable
import com.tarasovvp.smartblocker.database.entities.Filter
import com.tarasovvp.smartblocker.database.entities.FilteredCall
import kotlinx.parcelize.Parcelize

@Parcelize
data class CurrentUser(
    var filterList: ArrayList<Filter> = arrayListOf(),
    var filteredCallList: ArrayList<FilteredCall> = arrayListOf()
) : Parcelable