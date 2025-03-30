package com.tarasovvp.smartblocker.presentation.uimodels

import android.os.Parcelable
import com.tarasovvp.smartblocker.presentation.base.BaseAdapter
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import kotlinx.parcelize.Parcelize

@Parcelize
data class HeaderDataItem(
    val header: String = String.EMPTY,
) : BaseAdapter.HeaderData(), Parcelable
