package com.tarasovvp.smartblocker.domain.models.database_views

import android.os.Parcelable
import androidx.room.DatabaseView
import com.tarasovvp.smartblocker.domain.models.entities.CallWithFilter
import kotlinx.parcelize.Parcelize

@DatabaseView("SELECT * FROM log_calls LEFT JOIN filters ON log_calls.filter = filters.filter LEFT JOIN FilterWithCountryCode ON filters.filter = FilterWithCountryCode.filter")
@Parcelize
class LogCallWithFilter : Parcelable, CallWithFilter()
