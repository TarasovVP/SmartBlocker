package com.tarasovvp.blacklister.ui.number_data.filter_list

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.ItemFilterBinding
import com.tarasovvp.blacklister.databinding.ItemHeaderBinding
import com.tarasovvp.blacklister.extensions.EMPTY
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.model.HeaderDataItem
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class FilterAdapter(val filterClickListener: FilterClickListener) :
    BaseAdapter<Filter>() {

    var isDeleteMode: Boolean = false
    var searchQuery = String.EMPTY

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
                DataBindingUtil.inflate<ItemFilterBinding>(LayoutInflater.from(parent.context),
                    R.layout.item_filter,
                    parent,
                    false).root
            )
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        if (holder is FilterAdapter.ViewHolder) {
            holder.bindData(position)
        }
        super.onBindViewHolder(holder, position)
    }

    internal inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        fun bindData(position: Int) {
            val filter = getDataInPosition(position)
            DataBindingUtil.bind<ItemFilterBinding>(itemView)?.apply {
                filter.isDeleteMode = isDeleteMode
                filter.searchText = searchQuery
                this.filter = filter
                root.setSafeOnClickListener {
                    if (isDeleteMode) {
                        itemFilterDelete.isChecked = itemFilterDelete.isChecked.isTrue().not()
                    } else {
                        filterClickListener.onFilterClick(filter)
                    }
                }
                root.setOnLongClickListener {
                    if (filter.isDeleteMode.not()) {
                        filter.isCheckedForDelete = true
                        filterClickListener.onFilterLongClick()
                    }
                    return@setOnLongClickListener filter.isDeleteMode.not()
                }
                itemFilterDelete.setOnCheckedChangeListener { _, checked ->
                    filter.isCheckedForDelete = checked
                    filterClickListener.onFilterDeleteCheckChange(filter)
                }
                executePendingBindings()
                Log.e("adapterTAG",
                    "FilterAdapter bindData position $position itemView $itemView")
            }
        }
    }
}