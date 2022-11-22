package com.tarasovvp.smartblocker.model

import android.os.Parcelable
import android.text.SpannableString
import androidx.room.Ignore
import com.google.firebase.database.Exclude
import com.tarasovvp.smartblocker.extensions.EMPTY
import kotlinx.android.parcel.Parcelize

@Parcelize
open class NumberData : Parcelable {
    var numberData: String = String.EMPTY
    var searchText: String = String.EMPTY

    @get:Exclude
    @Ignore
    var highlightedSpanned: SpannableString? = null
}