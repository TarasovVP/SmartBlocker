package com.tarasovvp.smartblocker.models

import android.os.Parcelable
import com.tarasovvp.smartblocker.extensions.EMPTY
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InfoData(
    var title: String = String.EMPTY,
    var description: String = String.EMPTY
) : Parcelable