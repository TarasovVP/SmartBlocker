package com.tarasovvp.blacklister.ui.main.filter_add

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.setMargins
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.ItemContactSmallBinding
import com.tarasovvp.blacklister.databinding.ItemHeaderBinding
import com.tarasovvp.blacklister.extensions.EMPTY
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.model.HeaderDataItem
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class ContactFilterAdapter(private val contactClick: (String) -> Unit) : BaseAdapter<Contact>() {

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
                DataBindingUtil.inflate<ItemContactSmallBinding>(LayoutInflater.from(parent.context),
                    R.layout.item_contact_small,
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
        if (holder is ContactFilterAdapter.ViewHolder) {
            holder.bindData(
                position
            )
        } else {
            holder.itemView.layoutParams.height = 0
            (holder.itemView.layoutParams as RecyclerView.LayoutParams).setMargins(0)
        }
    }

    internal inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        fun bindData(position: Int) {
            val contact = getDataInPosition(position)
            DataBindingUtil.bind<ItemContactSmallBinding>(itemView)?.apply {
                contact.searchText = searchQuery
                this.contact = contact
                root.setSafeOnClickListener {
                    contactClick.invoke(contact.phone)
                }
            }
        }
    }
}