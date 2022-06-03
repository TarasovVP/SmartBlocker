package com.tarasovvp.blacklister.ui.main.contactlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.tarasovvp.blacklister.databinding.ItemContactBinding
import com.tarasovvp.blacklister.databinding.ItemHeaderBinding
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.loadCircleImage
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.model.HeaderDataItem
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class ContactAdapter(private val contactClick: (Contact, View) -> Unit) :
    BaseAdapter<Contact>() {

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
                ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false).root
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

        var binding: ItemContactBinding? = ItemContactBinding.bind(itemView)

        fun bindData(position: Int) {
            val contact = getDataInPosition(position)
            binding?.itemContactName?.text = contact.name
            binding?.itemContactNumber?.text = contact.phone
            binding?.itemContactAvatar?.loadCircleImage(contact.photoUrl)
            binding?.itemContactBlackListIcon?.isVisible = contact.isBlackList.isTrue()
            binding?.root?.setSafeOnClickListener { view ->
                binding?.itemContactMenu?.apply {
                    contactClick.invoke(contact, this)
                }
            }
        }
    }
}