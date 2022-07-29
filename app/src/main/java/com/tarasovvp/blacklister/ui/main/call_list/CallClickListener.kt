package com.tarasovvp.blacklister.ui.main.call_list

import com.tarasovvp.blacklister.model.Call

interface CallClickListener {
    fun onCallClick(phone: String)
    fun onCallLongClick()
    fun onCallDeleteCheckChange(call: Call)
    fun onCallDeleteInfoClick()
}