package com.tarasovvp.smartblocker.presentation.main.number.list.list_filter

import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.presentation.ui_models.FilterWithCountryCodeUIModel

interface FilterClickListener {
    fun onFilterClick(filterWithCountryCode: FilterWithCountryCodeUIModel)
    fun onFilterLongClick()
    fun onFilterDeleteCheckChange(filterWithCountryCode: FilterWithCountryCodeUIModel)
}