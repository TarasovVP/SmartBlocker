package com.tarasovvp.smartblocker.presentation.main.number.list.list_filter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.HEADER_TYPE
import com.tarasovvp.smartblocker.databinding.ItemFilterBinding
import com.tarasovvp.smartblocker.databinding.ItemHeaderBinding
import com.tarasovvp.smartblocker.presentation.base.BaseAdapter
import com.tarasovvp.smartblocker.utils.extensions.*

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
        if (holder is ViewHolder) {
            holder.bindData(position)
        }
        super.onBindViewHolder(holder, position)
    }

    internal inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        fun bindData(position: Int) {
            val filterWithCountryCode = getDataInPosition(position)
            DataBindingUtil.bind<ItemFilterBinding>(itemView)?.apply {
                filterWithCountryCode.isDeleteMode = isDeleteMode
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
                    if (filterWithCountryCode.isDeleteMode.isTrue().not()) {
                        filterWithCountryCode.isCheckedForDelete = true
                        filterClickListener.onFilterLongClick()
                    }
                    return@setOnLongClickListener filterWithCountryCode.isDeleteMode.isTrue().not()
                }
                itemFilterDelete.setOnCheckedChangeListener { _, checked ->
                    filterWithCountryCode.isCheckedForDelete = checked
                    filterClickListener.onFilterDeleteCheckChange(filterWithCountryCode)
                }
                executePendingBindings()
            }
        }
    }
}