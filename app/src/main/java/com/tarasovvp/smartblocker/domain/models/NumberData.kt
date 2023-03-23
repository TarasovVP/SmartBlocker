package com.tarasovvp.smartblocker.domain.models

import android.os.Parcelable
import android.text.SpannableString
import androidx.room.Ignore
import com.google.firebase.database.Exclude
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
open class NumberData : Parcelable {

    @IgnoredOnParcel
    @get:Exclude
    @Ignore
    var searchText: String = String.EMPTY

    @IgnoredOnParcel
    @get:Exclude
    @Ignore
    var highlightedSpanned: SpannableString? = null
}