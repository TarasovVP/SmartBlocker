package com.tarasovvp.smartblocker.ui.number_data.list.call_list

import com.tarasovvp.smartblocker.models.Call

interface CallClickListener {
    fun onCallClick(call: Call)
    fun onCallLongClick()
    fun onCallDeleteCheckChange(call: Call)
    fun onCallDeleteInfoClick()
}