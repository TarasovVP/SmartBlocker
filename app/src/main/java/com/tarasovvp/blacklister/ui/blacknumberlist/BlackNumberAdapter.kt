package com.tarasovvp.blacklister.ui.blacknumberlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tarasovvp.blacklister.databinding.ItemBlackNumberBinding
import com.tarasovvp.blacklister.databinding.ItemHeaderBinding
import com.tarasovvp.blacklister.model.BlackNumber
import com.tarasovvp.blacklister.model.HeaderDataItem
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class BlackNumberAdapter(private val blackNumberClick: (BlackNumber) -> Unit) :
    BaseAdapter<BlackNumber>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
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
        position: Int
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

        var binding: ItemBlackNumberBinding? = ItemBlackNumberBinding.bind(itemView)

        fun bindData(position: Int) {
            val blackNumber = getDataInPosition(position)
            binding?.itemBlackListNumber?.text = blackNumber.blackNumber
            binding?.root?.setSafeOnClickListener {
                blackNumber.let { clickedContact -> blackNumberClick.invoke(clickedContact) }
            }
        }
    }
}