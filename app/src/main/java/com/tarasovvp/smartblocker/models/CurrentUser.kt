package com.tarasovvp.smartblocker.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CurrentUser(
    var filterList: ArrayList<Filter> = arrayListOf(),
    var filteredCallList: ArrayList<FilteredCall> = arrayListOf()
) : Parcelable