package com.tarasovvp.blacklister.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CurrentUser(
    @SerializedName("whiteListPriority") var isWhiteListPriority: Boolean = false,
    @SerializedName("blackList") var blackNumberList: ArrayList<BlackNumber> = arrayListOf(),
    @SerializedName("whiteList") var whiteNumberList: ArrayList<WhiteNumber> = arrayListOf()
) : Parcelable