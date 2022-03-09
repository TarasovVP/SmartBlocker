package com.example.blacklister.ui.contactlist

import android.util.Log
import androidx.core.view.isVisible
import com.example.blacklister.databinding.ItemContactBinding
import com.example.blacklister.extensions.loadCircleImage
import com.example.blacklister.model.Contact
import com.example.blacklister.ui.base.BaseViewHolder
import com.example.blacklister.utils.setSafeOnClickListener
import java.lang.ref.WeakReference

class ContactViewHolder(private val binding: ItemContactBinding, listener: ContactClickListener) :
    BaseViewHolder<Contact>(binding.root) {

    private val weakListener = WeakReference(listener)

    override fun bind(item: Contact) {
        binding.itemContactName.text = item.name
        binding.itemContactNumber.text = item.phone
        binding.itemContactAvatar.loadCircleImage(item.photoUrl)
        binding.itemContactBlackListIcon.isVisible = item.isBlackList
        binding.root.setSafeOnClickListener {
            weakListener.get()?.onContactClicked(item)
        }
    }
}