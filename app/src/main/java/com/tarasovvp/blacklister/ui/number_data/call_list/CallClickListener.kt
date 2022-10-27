package com.tarasovvp.blacklister.ui.number_data.call_list

import com.tarasovvp.blacklister.model.Call

interface CallClickListener {
    fun onCallClick(call: Call)
    fun onCallLongClick()
    fun onCallDeleteCheckChange(call: Call)
    fun onCallDeleteInfoClick()
}