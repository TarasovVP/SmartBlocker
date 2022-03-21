package com.example.blacklister.ui.callloglist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.blacklister.databinding.ItemCallLogBinding
import com.example.blacklister.databinding.ItemHeaderBinding
import com.example.blacklister.model.CallLog
import com.example.blacklister.ui.base.BaseAdapter
import com.example.blacklister.utils.HeaderDataItem
import com.example.blacklister.utils.setSafeOnClickListener

class CallLogAdapter(private val callLogClick: (CallLog) -> Unit) :
    BaseAdapter<CallLog>() {

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
                ItemCallLogBinding.inflate(LayoutInflater.from(parent.context), parent, false).root
            )
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        super.onBindViewHolder(holder, position)
        if (holder is CallLogAdapter.ViewHolder) {
            holder.bindData(
                position
            )
        }
    }

    internal inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding: ItemCallLogBinding? = ItemCallLogBinding.bind(itemView)
        fun bindData(position: Int) {
            val callLog = getDataInPosition(position)
            binding?.itemCallLogName?.text = callLog.name
            binding?.itemCallLogNumber?.text = callLog.phone
            binding?.itemCallLogTime?.text = callLog.dateTimeFromTime()
            binding?.itemCallLogBlackNumberIcon?.isVisible = callLog.isBlackList
            binding?.itemCallLogTypeIcon?.setImageResource(callLog.callLogIcon())
            binding?.root?.setSafeOnClickListener {
                callLog.let { clickedContact -> callLogClick.invoke(clickedContact) }
            }
        }
    }
}