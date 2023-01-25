package com.tarasovvp.smartblocker.ui.main.number.list.list_call

import com.tarasovvp.smartblocker.models.Call

interface CallClickListener {
    fun onCallClick(call: Call)
    fun onCallLongClick()
    fun onCallDeleteCheckChange(call: Call)
    fun onCallDeleteInfoClick()
}