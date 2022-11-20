package com.tarasovvp.smartblocker.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CurrentUser(
    var filterList: ArrayList<Filter> = arrayListOf(),
    var blockedCallList: ArrayList<BlockedCall> = arrayListOf()
) : Parcelable