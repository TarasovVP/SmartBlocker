package com.tarasovvp.smartblocker.presentation.main.number.list.listfilter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.ItemFilterBinding
import com.tarasovvp.smartblocker.databinding.ItemHeaderBinding
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.HEADER_TYPE
import com.tarasovvp.smartblocker.presentation.base.BaseAdapter
import com.tarasovvp.smartblocker.presentation.uimodels.FilterWithFilteredNumberUIModel
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.highlightedSpanned
import com.tarasovvp.smartblocker.utils.extensions.isNotTrue
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import com.tarasovvp.smartblocker.utils.extensions.setSafeOnClickListener

class FilterAdapter(val filterClickListener: FilterClickListener) :
    BaseAdapter<FilterWithFilteredNumberUIModel>() {
    var isDeleteMode: Boolean = false
    var searchQuery = String.EMPTY

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        return if (viewType == HEADER_TYPE) {
            HeaderViewHolder(
                DataBindingUtil.inflate<ItemHeaderBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_header,
                    parent,
                    false,
                ).root,
            )
        } else {
            ViewHolder(
                DataBindingUtil.inflate<ItemFilterBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_filter,
                    parent,
                    false,
                ).root,
            )
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        if (holder is ViewHolder) {
            holder.bindData(position)
        }
        super.onBindViewHolder(holder, position)
    }

    internal inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        fun bindData(position: Int) {
            val filterWithFilteredNumberUIModel = getDataInPosition(position)
            DataBindingUtil.bind<ItemFilterBinding>(itemView)?.apply {
                filterWithFilteredNumberUIModel.isDeleteMode = isDeleteMode
                filterWithFilteredNumberUIModel.searchText = searchQuery
                filterWithFilteredNumberUIModel.highlightedSpanned =
                    filterWithFilteredNumberUIModel.filter.highlightedSpanned(
                        searchQuery,
                        null,
                        ContextCompat.getColor(itemView.context, R.color.text_color_black),
                    )
                this.filterWithFilteredNumberUIModel = filterWithFilteredNumberUIModel
                root.setSafeOnClickListener {
                    if (isDeleteMode) {
                        itemFilterDelete.isChecked = itemFilterDelete.isChecked.isNotTrue()
                    } else {
                        filterClickListener.onFilterClick(filterWithFilteredNumberUIModel)
                    }
                }
                root.setOnLongClickListener {
                    if (filterWithFilteredNumberUIModel.isDeleteMode.isTrue().not()) {
                        filterWithFilteredNumberUIModel.isCheckedForDelete = true
                        filterClickListener.onFilterLongClick()
                    }
                    return@setOnLongClickListener filterWithFilteredNumberUIModel.isDeleteMode.isTrue()
                        .not()
                }
                itemFilterDelete.setOnCheckedChangeListener { _, checked ->
                    filterWithFilteredNumberUIModel.isCheckedForDelete = checked
                    filterClickListener.onFilterDeleteCheckChange(filterWithFilteredNumberUIModel)
                }
                executePendingBindings()
            }
        }
    }
}
