package com.tarasovvp.smartblocker.domain.models

import android.os.Parcelable
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.presentation.base.BaseAdapter
import kotlinx.parcelize.Parcelize

@Parcelize
data class HeaderData(
    val header: String = String.EMPTY
) : BaseAdapter.HeaderDataUIModel(), Parcelable