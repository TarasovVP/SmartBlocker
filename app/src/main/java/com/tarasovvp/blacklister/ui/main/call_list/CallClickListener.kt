package com.tarasovvp.blacklister.ui.main.call_list

import android.view.View
import com.tarasovvp.blacklister.model.Call

interface CallClickListener {
    fun onCallClick(number: String)
    fun onCallLongClick()
    fun onCallDeleteCheckChange(call: Call)
    fun onCallDeleteInfoClick(view: View)
}