package com.tarasovvp.blacklister.ui.callloglist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.tarasovvp.blacklister.databinding.ItemCallLogBinding
import com.tarasovvp.blacklister.databinding.ItemHeaderBinding
import com.tarasovvp.blacklister.extensions.loadCircleImage
import com.tarasovvp.blacklister.model.CallLog
import com.tarasovvp.blacklister.model.HeaderDataItem
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

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
            binding?.itemCallLogAvatar?.loadCircleImage(callLog.photoUrl)
            binding?.root?.setSafeOnClickListener {
                callLog.let { clickedContact -> callLogClick.invoke(clickedContact) }
            }
        }
    }
}