package com.tarasovvp.smartblocker.presentation.main.number.list.listfilter

import com.tarasovvp.smartblocker.presentation.uimodels.FilterWithFilteredNumberUIModel

interface FilterClickListener {
    fun onFilterClick(filterWithFilteredNumberUIModel: FilterWithFilteredNumberUIModel)

    fun onFilterLongClick()

    fun onFilterDeleteCheckChange(filterWithFilteredNumberUIModel: FilterWithFilteredNumberUIModel)
}
