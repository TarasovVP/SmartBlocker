package com.tarasovvp.smartblocker.presentation.main.number.list.listcall

import com.tarasovvp.smartblocker.presentation.uimodels.CallWithFilterUIModel

interface CallClickListener {
    fun onCallClick(callWithFilter: CallWithFilterUIModel)

    fun onCallLongClick()

    fun onCallDeleteCheckChange(callWithFilter: CallWithFilterUIModel)

    fun onCallDeleteInfoClick()
}
