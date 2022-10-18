package com.tarasovvp.blacklister.ui.main.filter_add

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.setMargins
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.ItemContactFilterBinding
import com.tarasovvp.blacklister.databinding.ItemHeaderBinding
import com.tarasovvp.blacklister.extensions.EMPTY
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.model.HeaderDataItem
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class ContactFilterAdapter(private val contactClick: (String) -> Unit) : BaseAdapter<BaseAdapter.MainData>() {

    var searchQueryMap = Pair(String.EMPTY, String.EMPTY)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        return if (viewType == HeaderDataItem.HEADER_TYPE) {
            HeaderViewHolder(
                DataBindingUtil.inflate<ItemHeaderBinding>(LayoutInflater.from(parent.context),
                    R.layout.item_header,
                    parent,
                    false).root
            )
        } else {
            ViewHolder(
                DataBindingUtil.inflate<ItemContactFilterBinding>(LayoutInflater.from(parent.context),
                    R.layout.item_contact_filter,
                    parent,
                    false).root
            )
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        super.onBindViewHolder(holder, position)
        if (holder is ContactFilterAdapter.ViewHolder) {
            holder.bindData(
                position
            )
        } else {
            holder.itemView.layoutParams.height = 0
            (holder.itemView.layoutParams as RecyclerView.LayoutParams).setMargins(0)
        }
    }

    internal inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        fun bindData(position: Int) {
            val mainData = getDataInPosition(position)
            Log.e("filterAddTAG",
                "ContactFilterAdapter bindData mainData is Contact ${mainData is Contact} mainData is Filter ${mainData is Filter} mainData $mainData searchQuery $searchQueryMap")
            DataBindingUtil.bind<ItemContactFilterBinding>(itemView)?.apply {
                if (mainData is Contact) {
                    filter = null
                    this.contact = mainData
                    contact?.searchText = searchQueryMap.first
                    root.setSafeOnClickListener {
                        contactClick.invoke(mainData.phone)
                    }
                } else if (mainData is Filter) {
                    contact = null
                    this.filter = mainData
                    filter?.searchText = if (filter?.isTypeContain().isTrue()) searchQueryMap.first else String.format("%s%s", searchQueryMap.second, searchQueryMap.first)
                    root.setSafeOnClickListener {
                        contactClick.invoke(mainData.filter)
                    }
                }
            }
        }
    }
}