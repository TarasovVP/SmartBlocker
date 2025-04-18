package com.tarasovvp.smartblocker.presentation.main.number.list.listcontact

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.ItemContactBinding
import com.tarasovvp.smartblocker.databinding.ItemHeaderBinding
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.HEADER_TYPE
import com.tarasovvp.smartblocker.presentation.base.BaseAdapter
import com.tarasovvp.smartblocker.presentation.uimodels.ContactWithFilterUIModel
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.highlightedSpanned
import com.tarasovvp.smartblocker.utils.extensions.setSafeOnClickListener

class ContactAdapter(private val contactClick: (ContactWithFilterUIModel) -> Unit) :
    BaseAdapter<ContactWithFilterUIModel>() {
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
                DataBindingUtil.inflate<ItemContactBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_contact,
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
            val contactWithFilter = getDataInPosition(position)
            DataBindingUtil.bind<ItemContactBinding>(itemView)?.apply {
                contactWithFilter.searchText = searchQuery
                contactWithFilter.highlightedSpanned =
                    contactWithFilter.number.highlightedSpanned(
                        searchQuery,
                        null,
                        ContextCompat.getColor(itemView.context, R.color.text_color_black),
                    )
                this.contactWithFilter = contactWithFilter
                root.setSafeOnClickListener {
                    contactClick.invoke(
                        contactWithFilter.apply {
                            searchText = String.EMPTY
                            highlightedSpanned =
                                number.highlightedSpanned(
                                    String.EMPTY,
                                    null,
                                    ContextCompat.getColor(itemView.context, R.color.text_color_black),
                                )
                        },
                    )
                }
                executePendingBindings()
            }
        }
    }
}
