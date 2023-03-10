package com.tarasovvp.smartblocker.ui.main.number.list.list_filter

import com.tarasovvp.smartblocker.models.FilterWithCountryCode

interface FilterClickListener {
    fun onFilterClick(filter: FilterWithCountryCode)
    fun onFilterLongClick()
    fun onFilterDeleteCheckChange(filter: FilterWithCountryCode)
}