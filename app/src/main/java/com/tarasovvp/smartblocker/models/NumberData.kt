package com.tarasovvp.smartblocker.models

import android.os.Parcelable
import android.text.SpannableString
import androidx.room.Ignore
import com.google.firebase.database.Exclude
import com.tarasovvp.smartblocker.extensions.EMPTY
import kotlinx.parcelize.Parcelize

@Parcelize
open class NumberData : Parcelable {
    var numberData: String = String.EMPTY

    @get:Exclude
    @Ignore
    var searchText: String = String.EMPTY

    @get:Exclude
    @Ignore
    var highlightedSpanned: SpannableString? = null
}