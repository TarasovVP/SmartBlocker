package com.tarasovvp.smartblocker.presentation.main.number.list.list_call

import com.tarasovvp.smartblocker.domain.entities.models.CallWithFilter


interface CallClickListener {
    fun onCallClick(callWithFilter: CallWithFilter)
    fun onCallLongClick()
    fun onCallDeleteCheckChange(callWithFilter: CallWithFilter)
    fun onCallDeleteInfoClick()
}