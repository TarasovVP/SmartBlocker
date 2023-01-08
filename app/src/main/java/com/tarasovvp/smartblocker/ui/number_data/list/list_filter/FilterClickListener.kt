package com.tarasovvp.smartblocker.ui.number_data.list.list_filter

import com.tarasovvp.smartblocker.models.Filter

interface FilterClickListener {
    fun onFilterClick(filter: Filter)
    fun onFilterLongClick()
    fun onFilterDeleteCheckChange(filter: Filter)
}