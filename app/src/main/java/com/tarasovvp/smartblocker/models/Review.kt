package com.tarasovvp.smartblocker.models

import android.os.Parcelable
import com.tarasovvp.smartblocker.extensions.EMPTY
import kotlinx.parcelize.Parcelize

@Parcelize
data class Review(
    var user: String = String.EMPTY,
    var message: String = String.EMPTY,
    var time: Long = 0
) : Parcelable