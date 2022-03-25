package com.example.blacklister.ui.contactlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.blacklister.databinding.ItemContactBinding
import com.example.blacklister.databinding.ItemHeaderBinding
import com.example.blacklister.extensions.loadCircleImage
import com.example.blacklister.model.Contact
import com.example.blacklister.model.HeaderDataItem
import com.example.blacklister.ui.base.BaseAdapter
import com.example.blacklister.utils.setSafeOnClickListener

class ContactAdapter(private val contactClick: (Contact) -> Unit) :
    BaseAdapter<Contact>() {

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
                ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false).root
            )
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
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

        var binding: ItemContactBinding? = ItemContactBinding.bind(itemView)

        fun bindData(position: Int) {
            val contact = getDataInPosition(position)
            binding?.itemContactName?.text = contact.name
            binding?.itemContactNumber?.text = contact.phone
            binding?.itemContactAvatar?.loadCircleImage(contact.photoUrl)
            binding?.itemContactBlackListIcon?.isVisible = contact.isBlackList == true
            binding?.root?.setSafeOnClickListener {
                contact.let { clickedContact -> contactClick.invoke(clickedContact) }
            }
        }
    }
}