package com.example.blacklister.ui.callloglist

import com.example.blacklister.databinding.ItemCallLogBinding
import com.example.blacklister.model.CallLog
import com.example.blacklister.ui.base.BaseViewHolder
import com.example.blacklister.utils.setSafeOnClickListener
import java.lang.ref.WeakReference

class CallLogViewHolder(private val binding: ItemCallLogBinding, listener: CallLogClickListener) :
    BaseViewHolder<CallLog>(binding.root) {

    private val weakListener = WeakReference(listener)

    override fun bind(item: CallLog) {
        binding.itemCallLogName.text = item.name
        binding.itemCallLogNumber.text = item.phone
        binding.itemCallLogTypeIcon.setImageResource(item.callLogIcon())
        binding.root.setSafeOnClickListener {
            weakListener.get()?.onCallLogClicked(item)
        }
    }
}