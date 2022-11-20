package com.tarasovvp.smartblocker.model

import android.os.Parcelable
import com.tarasovvp.smartblocker.extensions.EMPTY
import com.tarasovvp.smartblocker.ui.base.BaseAdapter
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HeaderDataItem(
    val header: String = String.EMPTY
) : BaseAdapter.HeaderData(), Parcelable