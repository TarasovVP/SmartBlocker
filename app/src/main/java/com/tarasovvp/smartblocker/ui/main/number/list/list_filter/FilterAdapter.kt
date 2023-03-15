package com.tarasovvp.smartblocker.ui.main.number.list.list_filter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants.HEADER_TYPE
import com.tarasovvp.smartblocker.databinding.ItemFilterBinding
import com.tarasovvp.smartblocker.databinding.ItemHeaderBinding
import com.tarasovvp.smartblocker.extensions.*
import com.tarasovvp.smartblocker.database.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.ui.base.BaseAdapter

class FilterAdapter(val filterClickListener: FilterClickListener) :
    BaseAdapter<FilterWithCountryCode>() {

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
        position: Int) {
        if (holder is FilterAdapter.ViewHolder) {
            holder.bindData(position)
        }
        super.onBindViewHolder(holder, position)
    }

    internal inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        fun bindData(position: Int) {
            val filterWithCountryCode = getDataInPosition(position)
            DataBindingUtil.bind<ItemFilterBinding>(itemView)?.apply {
                filterWithCountryCode.filter?.isDeleteMode = isDeleteMode
                filterWithCountryCode.searchText = searchQuery
                filterWithCountryCode.highlightedSpanned = filterWithCountryCode.filter?.filter.highlightedSpanned(searchQuery, null, ContextCompat.getColor(itemView.context, R.color.text_color_black))
                this.filterWithCountryCode = filterWithCountryCode
                root.setSafeOnClickListener {
                    if (isDeleteMode) {
                        itemFilterDelete.isChecked = itemFilterDelete.isChecked.isNotTrue()
                    } else {
                        filterClickListener.onFilterClick(filterWithCountryCode)
                    }
                }
                root.setOnLongClickListener {
                    if (filterWithCountryCode.filter?.isDeleteMode.isTrue().not()) {
                        filterWithCountryCode.filter?.isCheckedForDelete = true
                        filterClickListener.onFilterLongClick()
                    }
                    return@setOnLongClickListener filterWithCountryCode.filter?.isDeleteMode.isTrue().not()
                }
                itemFilterDelete.setOnCheckedChangeListener { _, checked ->
                    filterWithCountryCode.filter?.isCheckedForDelete = checked
                    filterClickListener.onFilterDeleteCheckChange(filterWithCountryCode)
                }
                executePendingBindings()
            }
        }
    }
}