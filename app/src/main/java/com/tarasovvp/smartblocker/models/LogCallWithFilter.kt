package com.tarasovvp.smartblocker.models

import androidx.room.DatabaseView

@DatabaseView("SELECT * FROM log_calls LEFT JOIN filters ON log_calls.filter = filters.filter LEFT JOIN FilterWithCountryCode ON filters.filter = FilterWithCountryCode.filter")
class LogCallWithFilter: CallWithFilter()
