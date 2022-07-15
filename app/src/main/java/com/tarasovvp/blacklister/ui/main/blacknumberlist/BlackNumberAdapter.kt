package com.tarasovvp.blacklister.ui.main.blacknumberlist

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.ItemHeaderBinding
import com.tarasovvp.blacklister.databinding.ItemNumberBinding
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.model.BlackNumber
import com.tarasovvp.blacklister.model.HeaderDataItem
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class BlackNumberAdapter(val blackNumberClickListener: BlackNumberClickListener) :
    BaseAdapter<BlackNumber>() {

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
                ItemNumberBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ).root
            )
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        super.onBindViewHolder(holder, position)
        if (holder is BlackNumberAdapter.ViewHolder) {
            holder.bindData(
                position
            )
        }
    }

    internal inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        var binding: ItemNumberBinding? = ItemNumberBinding.bind(itemView)

        fun bindData(position: Int) {
            val blackNumber = getDataInPosition(position)
            binding?.itemNumberValue?.text = blackNumber.number
            binding?.itemNumberStart?.isVisible = blackNumber.start
            binding?.itemNumberContain?.isVisible = blackNumber.contain
            binding?.itemNumberEnd?.isVisible = blackNumber.end
            binding?.itemNumberAvatar?.setImageResource(R.drawable.ic_black_number)
            binding?.itemNumberDelete?.isVisible = isDeleteMode
            binding?.itemNumberArrow?.isVisible = isDeleteMode.not()
            binding?.itemNumberDelete?.isChecked = blackNumber.isCheckedForDelete
            binding?.root?.setSafeOnClickListener {
                if (isDeleteMode) {
                    binding?.itemNumberDelete?.isChecked = binding?.itemNumberDelete?.isChecked.isTrue().not()
                } else {
                    blackNumberClickListener.onBlackNumberClick(blackNumber)
                }
            }
            binding?.root?.setOnLongClickListener {
                blackNumberClickListener.onBlackNumberLongClick()
                return@setOnLongClickListener true
            }
            binding?.itemNumberDelete?.setOnCheckedChangeListener { _, checked ->
                blackNumber.isCheckedForDelete = checked
                blackNumberClickListener.onBlackNumberDeleteCheckChange(blackNumber)
            }
            Log.e("deleteTAG", "BlackNumberAdapter isDeleteMode $isDeleteMode")
        }
    }
}