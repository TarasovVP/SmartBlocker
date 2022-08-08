package com.tarasovvp.blacklister.model

import android.os.Parcelable
import com.tarasovvp.blacklister.R
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Info(
    var title: String = "",
    var description: String = "",
    var icon: Int = R.drawable.ic_test,
) : Parcelable