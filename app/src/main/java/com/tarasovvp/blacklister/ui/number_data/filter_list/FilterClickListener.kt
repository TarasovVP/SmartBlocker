package com.tarasovvp.blacklister.ui.number_data.filter_list

import com.tarasovvp.blacklister.model.Filter

interface FilterClickListener {
    fun onFilterClick(filter: Filter)
    fun onFilterLongClick()
    fun onFilterDeleteCheckChange(filter: Filter)
}