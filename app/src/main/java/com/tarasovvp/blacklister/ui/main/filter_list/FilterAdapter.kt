package com.tarasovvp.blacklister.ui.main.filter_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.tarasovvp.blacklister.databinding.ItemFilterBinding
import com.tarasovvp.blacklister.databinding.ItemHeaderBinding
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.model.HeaderDataItem
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class FilterAdapter(val filterClickListener: FilterClickListener) :
    BaseAdapter<Filter>() {

    var isDeleteMode: Boolean = false

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        return if (viewType == HeaderDataItem.HEADER_TYPE) {
            HeaderViewHolder(
                ItemHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false).root
            )
        } else {
            ViewHolder(
                ItemFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false).root
            )
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        super.onBindViewHolder(holder, position)
        if (holder is FilterAdapter.ViewHolder) {
            holder.bindData(position)
        }
    }

    internal inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        fun bindData(position: Int) {
            val filter = getDataInPosition(position)
            ItemFilterBinding.bind(itemView).apply {
                itemFilterValue.text = filter.filter
                itemFilterStart.isVisible = filter.start
                itemFilterContain.isVisible = filter.contain
                itemFilterEnd.isVisible = filter.end
                itemFilterDelete.isVisible = isDeleteMode
                itemFilterArrow.isVisible = isDeleteMode.not()
                itemFilterDelete.isChecked = filter.isCheckedForDelete
                root.setSafeOnClickListener {
                    if (isDeleteMode) {
                        itemFilterDelete.isChecked =
                            itemFilterDelete.isChecked.isTrue().not()
                    } else {
                        filterClickListener.onNumberClick(filter)
                    }
                }
                root.setOnLongClickListener {
                    filterClickListener.onNumberLongClick()
                    return@setOnLongClickListener true
                }
                itemFilterDelete.setOnCheckedChangeListener { _, checked ->
                    filter.isCheckedForDelete = checked
                    filterClickListener.onNumberDeleteCheckChange(filter)
                }
            }
        }
    }
}