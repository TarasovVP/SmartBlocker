package com.tarasovvp.blacklister.ui.main.call_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.ItemCallBinding
import com.tarasovvp.blacklister.databinding.ItemHeaderBinding
import com.tarasovvp.blacklister.extensions.EMPTY
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.model.Call
import com.tarasovvp.blacklister.model.HeaderDataItem
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class CallAdapter(val callClickListener: CallClickListener) :
    BaseAdapter<Call>() {

    var isDeleteMode: Boolean = false
    var searchQuery = String.EMPTY

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        return if (viewType == HeaderDataItem.HEADER_TYPE) {
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
                itemCallDeleteInfo.setSafeOnClickListener {
                    callClickListener.onCallDeleteInfoClick(it)
                }
                root.setSafeOnClickListener {
                    if (isDeleteMode) {
                        itemCallDelete.isChecked = itemCallDelete.isChecked.isTrue().not()
                    } else {
                        callClickListener.onCallClick(call)
                    }
                }
                root.setOnLongClickListener {
                    if (call.isDeleteMode.not()) {
                        call.isCheckedForDelete = true
                        callClickListener.onCallLongClick()
                    }
                    return@setOnLongClickListener call.isDeleteMode.not()
                }
                itemCallDelete.setOnCheckedChangeListener { _, checked ->
                    call.isCheckedForDelete = checked
                    callClickListener.onCallDeleteCheckChange(call)
                }
                this.call = call
                executePendingBindings()
            }
        }
    }
}