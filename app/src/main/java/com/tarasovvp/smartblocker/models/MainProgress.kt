package com.tarasovvp.smartblocker.models

import android.os.Parcelable
import com.tarasovvp.smartblocker.extensions.EMPTY
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MainProgress(
    var progressDescription: String = String.EMPTY,
    var progressMax: Int = 0,
    var progressPosition: Int = 0
) : Parcelable