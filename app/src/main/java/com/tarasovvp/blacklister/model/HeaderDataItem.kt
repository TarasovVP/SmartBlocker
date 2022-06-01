package com.tarasovvp.blacklister.model

import com.tarasovvp.blacklister.ui.base.BaseAdapter

data class HeaderDataItem(
    override val headerType: Int,
    val header: String,
) : BaseAdapter.HeaderData {

    companion object {
        const val HEADER_TYPE = 1
    }
}