package com.tarasovvp.blacklister.model

import com.tarasovvp.blacklister.extensions.EMPTY
import com.tarasovvp.blacklister.ui.base.BaseAdapter

data class HeaderDataItem(
    override val headerType: Int = HEADER_TYPE,
    val header: String = String.EMPTY,
) : BaseAdapter.HeaderData {

    companion object {
        const val HEADER_TYPE = 1
    }
}