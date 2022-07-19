package com.tarasovvp.blacklister.ui.main.numberlist

import com.tarasovvp.blacklister.model.Number

interface NumberClickListener {
    fun onNumberClick(number: Number)
    fun onNumberLongClick()
    fun onNumberDeleteCheckChange(number: Number)
}