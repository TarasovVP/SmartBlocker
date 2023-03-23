package com.tarasovvp.smartblocker.domain.models

import android.os.Parcelable
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import kotlinx.parcelize.Parcelize

@Parcelize
data class InfoData(
    var title: String = String.EMPTY,
    var description: String = String.EMPTY
) : Parcelable