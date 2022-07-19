package com.tarasovvp.blacklister.ui.main.whitenumberlist

import com.tarasovvp.blacklister.model.WhiteNumber

interface WhiteNumberClickListener {
    fun onWhiteNumberClick(whiteNumber: WhiteNumber)
    fun onWhiteNumberLongClick()
    fun onWhiteNumberDeleteCheckChange(whiteNumber: WhiteNumber)
}