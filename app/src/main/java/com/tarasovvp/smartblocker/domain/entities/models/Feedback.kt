package com.tarasovvp.smartblocker.domain.entities.models

import android.os.Parcelable
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import kotlinx.parcelize.Parcelize

@Parcelize
data class Feedback(
    var user: String = String.EMPTY,
    var message: String = String.EMPTY,
    var time: Long = 0,
) : Parcelable
