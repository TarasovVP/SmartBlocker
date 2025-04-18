package com.tarasovvp.smartblocker.presentation.main.number.list.listcall

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.ItemCallBinding
import com.tarasovvp.smartblocker.databinding.ItemHeaderBinding
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.HEADER_TYPE
import com.tarasovvp.smartblocker.presentation.base.BaseAdapter
import com.tarasovvp.smartblocker.presentation.uimodels.CallWithFilterUIModel
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.highlightedSpanned
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import com.tarasovvp.smartblocker.utils.extensions.setSafeOnClickListener

class CallAdapter(val callClickListener: CallClickListener) :
    BaseAdapter<CallWithFilterUIModel>() {
    var isDeleteMode: Boolean = false
    var searchQuery = String.EMPTY

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        return if (viewType == HEADER_TYPE) {
            HeaderViewHolder(
                DataBindingUtil.inflate<ItemHeaderBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_header,
                    parent,
                    false,
                ).root,
            )
        } else {
            ViewHolder(
                DataBindingUtil.inflate<ItemCallBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_call,
                    parent,
                    false,
                ).root,
            )
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        super.onBindViewHolder(holder, position)
        if (holder is ViewHolder) {
            holder.bindData(
                position,
            )
        }
    }

    internal inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        fun bindData(position: Int) {
            val callWithFilter = getDataInPosition(position)
            DataBindingUtil.bind<ItemCallBinding>(itemView)?.apply {
                callWithFilter.isDeleteMode = isDeleteMode
                callWithFilter.searchText = searchQuery
                callWithFilter.highlightedSpanned =
                    callWithFilter.number.highlightedSpanned(
                        searchQuery,
                        null,
                        ContextCompat.getColor(itemView.context, R.color.text_color_black),
                    )
                callWithFilter.isExtract = false
                this.callWithFilter = callWithFilter
                root.setSafeOnClickListener {
                    if (isDeleteMode) {
                        if (callWithFilter.isCallFiltered()) {
                            itemCallDelete.isChecked = itemCallDelete.isChecked.isTrue().not()
                        } else {
                            callClickListener.onCallDeleteInfoClick()
                        }
                    } else {
                        callClickListener.onCallClick(
                            callWithFilter.apply {
                                callWithFilter.searchText = String.EMPTY
                                callWithFilter.highlightedSpanned =
                                    callWithFilter.number.highlightedSpanned(
                                        String.EMPTY,
                                        null,
                                        ContextCompat.getColor(
                                            itemView.context,
                                            R.color.text_color_black,
                                        ),
                                    )
                            },
                        )
                    }
                }
                root.setOnLongClickListener {
                    if (callWithFilter.isDeleteMode.not()) {
                        if (callWithFilter.isCallFiltered()) {
                            callWithFilter.isCheckedForDelete = callWithFilter.isCallFiltered()
                            callClickListener.onCallLongClick()
                        } else {
                            callClickListener.onCallDeleteInfoClick()
                        }
                    }
                    return@setOnLongClickListener callWithFilter.isDeleteMode.not()
                }
                itemCallDelete.setOnCheckedChangeListener { _, checked ->
                    callWithFilter.isCheckedForDelete = checked
                    callClickListener.onCallDeleteCheckChange(callWithFilter)
                }
                executePendingBindings()
            }
        }
    }
}
