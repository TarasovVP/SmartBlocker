package com.tarasovvp.smartblocker.models

import android.os.Parcelable
import androidx.room.DatabaseView
import kotlinx.parcelize.Parcelize

@DatabaseView("SELECT * FROM filtered_calls LEFT JOIN filters ON filtered_calls.filter = filters.filter")
@Parcelize
class FilteredCallWithFilter : Parcelable, CallWithFilter()
