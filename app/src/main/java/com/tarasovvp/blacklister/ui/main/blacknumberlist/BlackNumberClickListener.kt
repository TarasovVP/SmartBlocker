package com.tarasovvp.blacklister.ui.main.blacknumberlist

import com.tarasovvp.blacklister.model.BlackNumber

interface BlackNumberClickListener {
    fun onBlackNumberClick(blackNumber: BlackNumber)
    fun onBlackNumberLongClick()
    fun onBlackNumberDeleteCheckChange(blackNumber: BlackNumber)
}