package com.tarasovvp.smartblocker.ui.number_data.call_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.ItemCallBinding
import com.tarasovvp.smartblocker.databinding.ItemHeaderBinding
import com.tarasovvp.smartblocker.extensions.EMPTY
import com.tarasovvp.smartblocker.extensions.highlightedSpanned
import com.tarasovvp.smartblocker.extensions.isTrue
import com.tarasovvp.smartblocker.models.Call
import com.tarasovvp.smartblocker.ui.base.BaseAdapter
import com.tarasovvp.smartblocker.utils.setSafeOnClickListener

class CallAdapter(val callClickListener: CallClickListener) :
    BaseAdapter<Call>() {

    var isDeleteMode: Boolean = false
    var searchQuery = String.EMPTY

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        return if (viewType == HEADER_TYPE) {
            HeaderViewHolder(
                DataBindingUtil.inflate<ItemHeaderBinding>(LayoutInflater.from(parent.context),
                    R.layout.item_header,
                    parent,
                    false).root
            )
        } else {
            ViewHolder(
                DataBindingUtil.inflate<ItemCallBinding>(LayoutInflater.from(parent.context),
                    R.layout.item_call,
                    parent,
                    false).root
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
            DataBindingUtil.bind<ItemCallBinding>(itemView)?.apply {
                call.isDeleteMode = isDeleteMode
                call.searchText = searchQuery
                call.highlightedSpanned = call.number.highlightedSpanned(searchQuery, null)
                call.isExtract = false
                this.call = call
                root.setSafeOnClickListener {
                    if (isDeleteMode) {
                        if (call.isBlockedCall()) {
                            itemCallDelete.isChecked = itemCallDelete.isChecked.isTrue().not()
                        } else {
                            callClickListener.onCallDeleteInfoClick()
                        }
                    } else {
                        callClickListener.onCallClick(call.apply {
                            searchText = String.EMPTY
                            call.highlightedSpanned = call.number.highlightedSpanned(String.EMPTY, null)
                        })
                    }
                }
                root.setOnLongClickListener {
                    if (call.isDeleteMode.not()) {
                        if (call.isBlockedCall()) {
                            call.isCheckedForDelete = call.isBlockedCall()
                            callClickListener.onCallLongClick()
                        } else {
                            callClickListener.onCallDeleteInfoClick()
                        }
                    }
                    return@setOnLongClickListener call.isDeleteMode.not()
                }
                itemCallDelete.setOnCheckedChangeListener { _, checked ->
                    call.isCheckedForDelete = checked
                    callClickListener.onCallDeleteCheckChange(call)
                }
                executePendingBindings()
            }
        }
    }
}