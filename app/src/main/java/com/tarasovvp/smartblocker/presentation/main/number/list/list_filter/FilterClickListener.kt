package com.tarasovvp.smartblocker.presentation.main.number.list.list_filter

import com.tarasovvp.smartblocker.presentation.ui_models.FilterWithFilteredNumberUIModel

interface FilterClickListener {
    fun onFilterClick(filterWithFilteredNumberUIModel: FilterWithFilteredNumberUIModel)

    fun onFilterLongClick()

    fun onFilterDeleteCheckChange(filterWithFilteredNumberUIModel: FilterWithFilteredNumberUIModel)
}
