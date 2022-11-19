package com.tarasovvp.blacklister.model

import android.os.Parcelable
import com.tarasovvp.blacklister.extensions.EMPTY
import kotlinx.android.parcel.Parcelize

@Parcelize
open class NumberData : Parcelable {
    var numberData: String = String.EMPTY
    var searchText = String.EMPTY
}