package com.tarasovvp.smartblocker.ui.number_data.list.filter_list

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.ItemFilterBinding
import com.tarasovvp.smartblocker.databinding.ItemHeaderBinding
import com.tarasovvp.smartblocker.extensions.EMPTY
import com.tarasovvp.smartblocker.extensions.highlightedSpanned
import com.tarasovvp.smartblocker.extensions.isNotTrue
import com.tarasovvp.smartblocker.models.Filter
import com.tarasovvp.smartblocker.ui.base.BaseAdapter
import com.tarasovvp.smartblocker.utils.setSafeOnClickListener

class FilterAdapter(val filterClickListener: FilterClickListener) :
    BaseAdapter<Filter>() {

    var isDeleteMode: Boolean = false
    var searchQuery = String.EMPTY

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        return if (viewType == HEADER_TYPE) {
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
        position: Int,
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
                filter.highlightedSpanned = filter.filter.highlightedSpanned(searchQuery, null)
                this.filter = filter
                root.setSafeOnClickListener {
                    if (isDeleteMode) {
                        itemFilterDelete.isChecked = itemFilterDelete.isChecked.isNotTrue()
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