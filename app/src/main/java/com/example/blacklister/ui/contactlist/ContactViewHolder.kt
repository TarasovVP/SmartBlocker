package com.example.blacklister.ui.contactlist

import com.example.blacklister.databinding.ItemContactBinding
import com.example.blacklister.extensions.loadCircleImage
import com.example.blacklister.model.Contact
import com.example.blacklister.ui.base.BaseViewHolder

class ContactViewHolder(private val binding: ItemContactBinding) :
    BaseViewHolder<Contact>(binding.root) {

    override fun bind(item: Contact) {
        binding.itemContactName.text = item.name
        binding.itemContactNumber.text = item.phone
        binding.itemContactAvatar.loadCircleImage(item.photoUrl)
    }
}