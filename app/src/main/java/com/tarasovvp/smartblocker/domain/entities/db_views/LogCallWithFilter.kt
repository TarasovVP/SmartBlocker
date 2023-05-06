package com.tarasovvp.smartblocker.domain.entities.db_views

import android.os.Parcelable
import androidx.room.DatabaseView
import com.tarasovvp.smartblocker.domain.entities.models.CallWithFilter
import kotlinx.parcelize.Parcelize

@DatabaseView("SELECT * FROM log_calls LEFT JOIN filters ON log_calls.filter = filters.filter")
@Parcelize
class LogCallWithFilter : Parcelable, CallWithFilter()
