package com.example.blacklister.ui.contactlist

import com.example.blacklister.databinding.ItemContactBinding
import com.example.blacklister.extensions.loadCircleImage
import com.example.blacklister.model.Contact
import com.example.blacklister.ui.base.BaseViewHolder
import com.example.blacklister.utils.setSafeOnClickListener
import java.lang.ref.WeakReference

class ContactViewHolder(private val binding: ItemContactBinding, private val listener: ContactClickListener) :
    BaseViewHolder<Contact>(binding.root) {

    private val weakListener = WeakReference(listener)

    override fun bind(item: Contact) {
        binding.itemContactName.text = item.name
        binding.itemContactNumber.text = item.phone
        binding.itemContactAvatar.loadCircleImage(item.photoUrl)
        binding.root.setSafeOnClickListener {
            weakListener.get()?.onContactClicked(item)
        }
    }
}