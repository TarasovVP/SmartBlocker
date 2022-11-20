package com.tarasovvp.smartblocker.model

import android.os.Parcelable
import com.tarasovvp.smartblocker.extensions.EMPTY
import kotlinx.android.parcel.Parcelize

@Parcelize
open class NumberData : Parcelable {
    var numberData: String = String.EMPTY
    var searchText = String.EMPTY
}