package com.tarasovvp.blacklister.model

import android.os.Parcelable
import com.tarasovvp.blacklister.extensions.EMPTY
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HeaderDataItem(
    val header: String = String.EMPTY,
) : BaseAdapter.HeaderData(), Parcelable