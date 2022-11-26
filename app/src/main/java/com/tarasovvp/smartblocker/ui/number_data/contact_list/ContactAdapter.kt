package com.tarasovvp.smartblocker.ui.number_data.contact_list

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.ItemContactBinding
import com.tarasovvp.smartblocker.databinding.ItemHeaderBinding
import com.tarasovvp.smartblocker.extensions.EMPTY
import com.tarasovvp.smartblocker.extensions.highlightedSpanned
import com.tarasovvp.smartblocker.models.Contact
import com.tarasovvp.smartblocker.ui.base.BaseAdapter
import com.tarasovvp.smartblocker.utils.setSafeOnClickListener

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
                contact.highlightedSpanned = contact.number.highlightedSpanned(searchQuery, null)
                this.contact = contact
                root.setSafeOnClickListener {
                    contactClick.invoke(contact.apply {
                        searchText = String.EMPTY
                        contact.highlightedSpanned = contact.number.highlightedSpanned( String.EMPTY, null)
                    })
                }
                executePendingBindings()
                Log.e("adapterTAG",
                    "ContactAdapter bindData itemView $itemView contact $contact")
            }
        }
    }
}