package com.example.blacklister.ui.contactlist

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.blacklister.databinding.ItemContactBinding
import com.example.blacklister.model.Contact
import com.example.blacklister.ui.base.BaseAdapter
import com.example.blacklister.ui.base.BaseViewHolder

class ContactAdapter : BaseAdapter<Contact, BaseViewHolder<Contact>>(ArrayList()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding =
            ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(binding)
    }
}