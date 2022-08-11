package com.tarasovvp.blacklister.ui.main.filter_list

import com.tarasovvp.blacklister.model.Filter

interface FilterClickListener {
    fun onNumberClick(filter: Filter)
    fun onNumberLongClick()
    fun onNumberDeleteCheckChange(filter: Filter)
}