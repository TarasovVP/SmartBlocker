package com.tarasovvp.blacklister.ui.main.whitenumberlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.tarasovvp.blacklister.databinding.ItemBlackNumberBinding
import com.tarasovvp.blacklister.databinding.ItemHeaderBinding
import com.tarasovvp.blacklister.model.HeaderDataItem
import com.tarasovvp.blacklister.model.WhiteNumber
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class WhiteNumberAdapter(private val blackNumberClick: (WhiteNumber?, View) -> Unit) :
    BaseAdapter<WhiteNumber>() {

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
                ItemBlackNumberBinding.inflate(
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
        if (holder is WhiteNumberAdapter.ViewHolder) {
            holder.bindData(
                position
            )
        }
    }

    internal inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        var binding: ItemBlackNumberBinding? = ItemBlackNumberBinding.bind(itemView)

        fun bindData(position: Int) {
            val blackNumber = getDataInPosition(position)
            binding?.itemBlackListNumber?.text = blackNumber.whiteNumber
            binding?.itemBlackListStart?.isVisible = blackNumber.isStart
            binding?.itemBlackListContain?.isVisible = blackNumber.isContain
            binding?.itemBlackListEnd?.isVisible = blackNumber.isEnd
            binding?.root?.setSafeOnClickListener {
                binding?.itemBlackListMenu?.apply {
                    blackNumberClick.invoke(blackNumber, this)
                }
            }
        }
    }
}