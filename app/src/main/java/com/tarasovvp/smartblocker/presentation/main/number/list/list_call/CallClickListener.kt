package com.tarasovvp.smartblocker.presentation.main.number.list.list_call

import com.tarasovvp.smartblocker.presentation.ui_models.CallWithFilterUIModel


interface CallClickListener {
    fun onCallClick(callWithFilterUIModel: CallWithFilterUIModel)
    fun onCallLongClick()
    fun onCallDeleteCheckChange(callWithFilterUIModel: CallWithFilterUIModel)
    fun onCallDeleteInfoClick()
}