package com.example.blacklister.ui.numberlist

import com.example.blacklister.databinding.ItemBlackNumberBinding
import com.example.blacklister.model.BlackNumber
import com.example.blacklister.ui.base.BaseViewHolder
import com.example.blacklister.utils.setSafeOnClickListener
import java.lang.ref.WeakReference

class BlackNumberViewHolder(
    private val binding: ItemBlackNumberBinding,
    listener: BlackNumberClickListener
) :
    BaseViewHolder<BlackNumber>(binding.root) {

    private val weakListener = WeakReference(listener)

    override fun bind(item: BlackNumber) {
        binding.itemBlackListNumber.text = item.blackNumber
        binding.root.setSafeOnClickListener {
            weakListener.get()?.onBlackNumberClicked(item)
        }
    }
}