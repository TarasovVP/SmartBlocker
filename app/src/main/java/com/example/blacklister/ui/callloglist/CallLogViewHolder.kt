package com.example.blacklister.ui.callloglist

import androidx.core.view.isVisible
import com.example.blacklister.databinding.ItemCallLogBinding
import com.example.blacklister.extensions.dateFromMilliseconds
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
        binding.itemCallLogTime.text = item.time?.dateFromMilliseconds()
        binding.itemCallLogBlackNumberIcon.isVisible = item.isBlackList
        binding.itemCallLogTypeIcon.setImageResource(item.callLogIcon())
        binding.root.setSafeOnClickListener {
            weakListener.get()?.onCallLogClicked(item)
        }
    }
}