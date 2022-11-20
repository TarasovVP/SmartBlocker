package com.tarasovvp.smartblocker.ui.number_data.call_list

import com.tarasovvp.smartblocker.model.Call

interface CallClickListener {
    fun onCallClick(call: Call)
    fun onCallLongClick()
    fun onCallDeleteCheckChange(call: Call)
    fun onCallDeleteInfoClick()
}