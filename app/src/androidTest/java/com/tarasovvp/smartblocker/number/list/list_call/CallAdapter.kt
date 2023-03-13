package com.tarasovvp.smartblocker.ui.main.number.list.list_call

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants.HEADER_TYPE
import com.tarasovvp.smartblocker.databinding.ItemCallBinding
import com.tarasovvp.smartblocker.databinding.ItemHeaderBinding
import com.tarasovvp.smartblocker.extensions.EMPTY
import com.tarasovvp.smartblocker.extensions.highlightedSpanned
import com.tarasovvp.smartblocker.extensions.isTrue
import com.tarasovvp.smartblocker.extensions.setSafeOnClickListener
import com.tarasovvp.smartblocker.models.CallWithFilter
import com.tarasovvp.smartblocker.ui.base.BaseAdapter

class CallAdapter(val callClickListener: CallClickListener) :
    BaseAdapter<CallWithFilter>() {

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
            val callWithFilter = getDataInPosition(position)
            DataBindingUtil.bind<ItemCallBinding>(itemView)?.apply {
                callWithFilter.call?.isDeleteMode = isDeleteMode
                callWithFilter.searchText = searchQuery
                callWithFilter.highlightedSpanned = callWithFilter.call?.number.highlightedSpanned(searchQuery, null, ContextCompat.getColor(itemView.context, R.color.text_color_black))
                callWithFilter.call?.isExtract = false
                this.callWithFilter = callWithFilter
                root.setSafeOnClickListener {
                    if (isDeleteMode) {
                        if (callWithFilter.call?.isCallFiltered().isTrue()) {
                            itemCallDelete.isChecked = itemCallDelete.isChecked.isTrue().not()
                        } else {
                            callClickListener.onCallDeleteInfoClick()
                        }
                    } else {
                        callClickListener.onCallClick(callWithFilter.apply {
                            callWithFilter.searchText = String.EMPTY
                            callWithFilter.highlightedSpanned =
                                callWithFilter.call?.number.highlightedSpanned(String.EMPTY, null, ContextCompat.getColor(itemView.context, R.color.text_color_black))
                        })
                    }
                }
                root.setOnLongClickListener {
                    if (callWithFilter.call?.isDeleteMode.isTrue().not()) {
                        if (callWithFilter.call?.isCallFiltered().isTrue()) {
                            callWithFilter.call?.isCheckedForDelete = callWithFilter.call?.isCallFiltered().isTrue()
                            callClickListener.onCallLongClick()
                        } else {
                            callClickListener.onCallDeleteInfoClick()
                        }
                    }
                    return@setOnLongClickListener callWithFilter.call?.isDeleteMode.isTrue().not()
                }
                itemCallDelete.setOnCheckedChangeListener { _, checked ->
                    callWithFilter.call?.isCheckedForDelete = checked
                    callClickListener.onCallDeleteCheckChange(callWithFilter)
                }
                executePendingBindings()
            }
        }
    }
}