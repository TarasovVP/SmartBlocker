package com.tarasovvp.blacklister.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CurrentUser(
    var filterList: ArrayList<Filter> = arrayListOf(),
) : Parcelable