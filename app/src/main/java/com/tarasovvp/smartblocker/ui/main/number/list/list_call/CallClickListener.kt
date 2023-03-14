package com.tarasovvp.smartblocker.ui.main.number.list.list_call

import com.tarasovvp.smartblocker.models.CallWithFilter

interface CallClickListener {
    fun onCallClick(callWithFilter: CallWithFilter)
    fun onCallLongClick()
    fun onCallDeleteCheckChange(callWithFilter: CallWithFilter)
    fun onCallDeleteInfoClick()
}