package com.tarasovvp.smartblocker.presentation.main.number.list.list_filter

import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode

interface FilterClickListener {
    fun onFilterClick(filterWithCountryCode: FilterWithCountryCode)
    fun onFilterLongClick()
    fun onFilterDeleteCheckChange(filterWithCountryCode: FilterWithCountryCode)
}