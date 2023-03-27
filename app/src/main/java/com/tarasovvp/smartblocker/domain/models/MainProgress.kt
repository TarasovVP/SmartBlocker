package com.tarasovvp.smartblocker.domain.models

import android.os.Parcelable
import com.tarasovvp.smartblocker.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class MainProgress(
    var progressDescription: Int = R.string.progress_data_collect,
    var progressMax: Int = 0,
    var progressPosition: Int = 0
) : Parcelable