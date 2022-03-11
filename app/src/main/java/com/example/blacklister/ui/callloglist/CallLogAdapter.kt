package com.example.blacklister.ui.callloglist

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.blacklister.databinding.ItemCallLogBinding
import com.example.blacklister.model.CallLog
import com.example.blacklister.ui.base.BaseAdapter
import com.example.blacklister.ui.base.BaseViewHolder

class CallLogAdapter(private val listener: CallLogClickListener) :
    BaseAdapter<CallLog, BaseViewHolder<CallLog>>(ArrayList()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallLogViewHolder {
        val binding =
            ItemCallLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CallLogViewHolder(binding, listener)
    }
}