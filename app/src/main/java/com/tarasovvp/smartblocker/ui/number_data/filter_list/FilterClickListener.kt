package com.tarasovvp.smartblocker.ui.number_data.filter_list

import com.tarasovvp.smartblocker.model.Filter

interface FilterClickListener {
    fun onFilterClick(filter: Filter)
    fun onFilterLongClick()
    fun onFilterDeleteCheckChange(filter: Filter)
}