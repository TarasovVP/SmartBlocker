package com.tarasovvp.blacklister.ui.number_data.contact_list

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.ItemContactBinding
import com.tarasovvp.blacklister.databinding.ItemHeaderBinding
import com.tarasovvp.blacklister.extensions.EMPTY
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class ContactAdapter(private val contactClick: (Contact) -> Unit) : BaseAdapter<Contact>() {

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
                DataBindingUtil.inflate<ItemContactBinding>(LayoutInflater.from(parent.context),
                    R.layout.item_contact,
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
        if (holder is ContactAdapter.ViewHolder) {
            holder.bindData(
                position
            )
        }
    }

    internal inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        fun bindData(position: Int) {
            val contact = getDataInPosition(position)
            DataBindingUtil.bind<ItemContactBinding>(itemView)?.apply {
                contact.searchText = searchQuery
                this.contact = contact
                root.setSafeOnClickListener {
                    contactClick.invoke(contact)
                }
                executePendingBindings()
                Log.e("adapterTAG",
                    "ContactAdapter bindData itemView $itemView contact $contact")
            }
        }
    }
}