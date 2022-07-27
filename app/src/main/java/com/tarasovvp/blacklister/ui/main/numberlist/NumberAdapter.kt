package com.tarasovvp.blacklister.ui.main.numberlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.ItemHeaderBinding
import com.tarasovvp.blacklister.databinding.ItemNumberBinding
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.model.HeaderDataItem
import com.tarasovvp.blacklister.model.Number
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class NumberAdapter(val numberClickListener: NumberClickListener) :
    BaseAdapter<Number>() {

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
                ItemNumberBinding.inflate(LayoutInflater.from(parent.context), parent, false).root
            )
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        super.onBindViewHolder(holder, position)
        if (holder is NumberAdapter.ViewHolder) {
            holder.bindData(position)
        }
    }

    internal inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        var binding: ItemNumberBinding? = ItemNumberBinding.bind(itemView)

        fun bindData(position: Int) {
            val number = getDataInPosition(position)
            binding?.itemNumberValue?.text = number.number
            binding?.itemNumberStart?.isVisible = number.start
            binding?.itemNumberContain?.isVisible = number.contain
            binding?.itemNumberEnd?.isVisible = number.end
            binding?.itemNumberAvatar?.setImageResource(if (number.isBlackNumber) R.drawable.ic_black_number else R.drawable.ic_white_number)
            binding?.itemNumberDelete?.isVisible = isDeleteMode
            binding?.itemNumberArrow?.isVisible = isDeleteMode.not()
            binding?.itemNumberDelete?.isChecked = number.isCheckedForDelete
            binding?.root?.setSafeOnClickListener {
                if (isDeleteMode) {
                    binding?.itemNumberDelete?.isChecked =
                        binding?.itemNumberDelete?.isChecked.isTrue().not()
                } else {
                    numberClickListener.onNumberClick(number)
                }
            }
            binding?.root?.setOnLongClickListener {
                numberClickListener.onNumberLongClick()
                return@setOnLongClickListener true
            }
            binding?.itemNumberDelete?.setOnCheckedChangeListener { _, checked ->
                number.isCheckedForDelete = checked
                numberClickListener.onNumberDeleteCheckChange(number)
            }
        }
    }
}