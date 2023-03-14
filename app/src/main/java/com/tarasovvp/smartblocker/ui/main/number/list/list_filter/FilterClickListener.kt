package com.tarasovvp.smartblocker.ui.main.number.list.list_filter

import com.tarasovvp.smartblocker.models.FilterWithCountryCode

interface FilterClickListener {
    fun onFilterClick(filterWithCountryCode: FilterWithCountryCode)
    fun onFilterLongClick()
    fun onFilterDeleteCheckChange(filterWithCountryCode: FilterWithCountryCode)
}