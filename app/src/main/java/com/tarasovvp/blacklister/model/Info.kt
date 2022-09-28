package com.tarasovvp.blacklister.model

import android.os.Parcelable
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.extensions.EMPTY
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Info(
    var title: String = String.EMPTY,
    var description: String = String.EMPTY,
    var icon: Int = R.drawable.ic_logo,
) : Parcelable