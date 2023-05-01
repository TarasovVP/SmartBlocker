package com.tarasovvp.smartblocker.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MainProgress(
    var progressDescription: Int = 0,
    var progressMax: Int = 0,
    var progressPosition: Int = 0
) : Parcelable