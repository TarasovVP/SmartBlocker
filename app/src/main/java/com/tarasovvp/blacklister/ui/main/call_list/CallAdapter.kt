package com.tarasovvp.blacklister.ui.main.call_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.BLOCKED_CALL
import com.tarasovvp.blacklister.databinding.ItemCallBinding
import com.tarasovvp.blacklister.databinding.ItemHeaderBinding
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.loadCircleImage
import com.tarasovvp.blacklister.model.Call
import com.tarasovvp.blacklister.model.HeaderDataItem
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class CallAdapter(val callClickListener: CallClickListener) :
    BaseAdapter<Call>() {

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
                ItemCallBinding.inflate(LayoutInflater.from(parent.context), parent, false).root
            )
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        super.onBindViewHolder(holder, position)
        if (holder is CallAdapter.ViewHolder) {
            holder.bindData(
                position
            )
        }
    }

    internal inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        fun bindData(position: Int) {
            val call = getDataInPosition(position)
            ItemCallBinding.bind(itemView).apply {
                itemCallName.text =
                    if (call.phone.isNullOrEmpty()) itemView.context.getString(R.string.hidden) else call.name
                itemCallNumber.text = call.phone
                itemCallTime.text = call.dateTimeFromTime()
                itemCallTypeIcon.setImageResource(call.callIcon())
                itemCallAvatar.loadCircleImage(call.photoUrl)
                itemCallArrow.isVisible = isDeleteMode.not()
                itemCallDelete.isVisible = isDeleteMode && call.type == BLOCKED_CALL
                itemCallDelete.isChecked = call.isCheckedForDelete
                itemCallDelete.setOnCheckedChangeListener { _, checked ->
                    call.isCheckedForDelete = checked
                    callClickListener.onCallDeleteCheckChange(call)
                }
                itemCallDeleteInfo.isVisible = isDeleteMode && call.type != BLOCKED_CALL
                itemCallDeleteInfo.setSafeOnClickListener {
                    callClickListener.onCallDeleteInfoClick(it)
                }
                root.setSafeOnClickListener {
                    if (isDeleteMode) {
                        itemCallDelete.isChecked =
                            itemCallDelete.isChecked.isTrue().not()
                    } else {
                        callClickListener.onCallClick(call.phone.orEmpty())
                    }
                }
                root.setOnLongClickListener {
                    callClickListener.onCallLongClick()
                    return@setOnLongClickListener true
                }
            }
        }
    }
}