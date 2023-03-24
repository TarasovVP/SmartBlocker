package com.tarasovvp.smartblocker.domain.models.database_views

import android.os.Parcelable
import androidx.room.DatabaseView
import com.tarasovvp.smartblocker.domain.models.entities.CallWithFilter
import kotlinx.parcelize.Parcelize

@DatabaseView("SELECT * FROM filtered_calls LEFT JOIN filters ON filtered_calls.filter = filters.filter")
@Parcelize
class FilteredCallWithFilter : Parcelable, CallWithFilter()
