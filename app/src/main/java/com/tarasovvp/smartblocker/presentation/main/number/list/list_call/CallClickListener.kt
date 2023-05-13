package com.tarasovvp.smartblocker.presentation.main.number.list.list_call

import com.tarasovvp.smartblocker.presentation.ui_models.CallWithFilterUIModel


interface CallClickListener {
    fun onCallClick(callWithFilter: CallWithFilterUIModel)
    fun onCallLongClick()
    fun onCallDeleteCheckChange(callWithFilter: CallWithFilterUIModel)
    fun onCallDeleteInfoClick()
}