package com.tarasovvp.smartblocker.models

import androidx.room.DatabaseView

@DatabaseView("SELECT * FROM filtered_calls LEFT JOIN filters ON filtered_calls.filter = filters.filter")
class FilteredCallWithFilter: CallWithFilter()
