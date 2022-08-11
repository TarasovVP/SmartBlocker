package com.tarasovvp.blacklister.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CurrentUser(
    @SerializedName("whiteListPriority") var isWhiteListPriority: Boolean = false,
    @SerializedName("blackList") var blackFilterList: ArrayList<BlackFilter> = arrayListOf(),
    @SerializedName("whiteList") var whiteFilterList: ArrayList<WhiteFilter> = arrayListOf(),
) : Parcelable