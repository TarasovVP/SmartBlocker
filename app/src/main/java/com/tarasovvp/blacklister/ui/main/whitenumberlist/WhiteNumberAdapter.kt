package com.tarasovvp.blacklister.ui.main.whitenumberlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.ItemHeaderBinding
import com.tarasovvp.blacklister.databinding.ItemNumberBinding
import com.tarasovvp.blacklister.model.HeaderDataItem
import com.tarasovvp.blacklister.model.WhiteNumber
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class WhiteNumberAdapter(private val whiteNumberClick: (WhiteNumber?, View) -> Unit) :
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
        if (holder is WhiteNumberAdapter.ViewHolder) {
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
            binding?.itemNumberValue?.text = blackNumber.whiteNumber
            binding?.itemNumberStart?.isVisible = blackNumber.isStart
            binding?.itemNumberContain?.isVisible = blackNumber.isContain
            binding?.itemNumberEnd?.isVisible = blackNumber.isEnd
            binding?.itemNumberAvatar?.setImageResource(R.drawable.ic_white_number)
            binding?.root?.setSafeOnClickListener {
                binding?.itemNumberMenu?.apply {
                    whiteNumberClick.invoke(blackNumber, this)
                }
            }
        }
    }
}