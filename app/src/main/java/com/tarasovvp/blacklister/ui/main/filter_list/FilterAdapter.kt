package com.tarasovvp.blacklister.ui.main.filter_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.tarasovvp.blacklister.R
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

        var binding: ItemFilterBinding? = ItemFilterBinding.bind(itemView)

        fun bindData(position: Int) {
            val filter = getDataInPosition(position)
            binding?.itemFilterValue?.text = filter.filter
            binding?.itemFilterStart?.isVisible = filter.start
            binding?.itemFilterContain?.isVisible = filter.contain
            binding?.itemFilterEnd?.isVisible = filter.end
            binding?.itemFilterAvatar?.setImageResource(if (filter.isBlackFilter) R.drawable.ic_black_filter else R.drawable.ic_white_filter)
            binding?.itemFilterDelete?.isVisible = isDeleteMode
            binding?.itemFilterArrow?.isVisible = isDeleteMode.not()
            binding?.itemFilterDelete?.isChecked = filter.isCheckedForDelete
            binding?.root?.setSafeOnClickListener {
                if (isDeleteMode) {
                    binding?.itemFilterDelete?.isChecked =
                        binding?.itemFilterDelete?.isChecked.isTrue().not()
                } else {
                    filterClickListener.onNumberClick(filter)
                }
            }
            binding?.root?.setOnLongClickListener {
                filterClickListener.onNumberLongClick()
                return@setOnLongClickListener true
            }
            binding?.itemFilterDelete?.setOnCheckedChangeListener { _, checked ->
                filter.isCheckedForDelete = checked
                filterClickListener.onNumberDeleteCheckChange(filter)
            }
        }
    }
}