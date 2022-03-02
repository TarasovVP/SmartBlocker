package com.example.blacklister.ui.contactlist

import com.example.blacklister.databinding.ContactListFragmentBinding
import com.example.blacklister.model.Contact
import com.example.blacklister.ui.base.BaseAdapter
import com.example.blacklister.ui.base.BaseFragment
import com.example.blacklister.ui.base.BaseListFragment

class ContactListFragment : BaseListFragment<ContactListFragmentBinding, ContactListViewModel, Contact>() {

    override fun getViewBinding() = ContactListFragmentBinding.inflate(layoutInflater)

    override val viewModelClass = ContactListViewModel::class.java
    override fun createAdapter(): BaseAdapter<Contact, *>? {
        return context?.let {
            ContactAdapter()
        }
    }

}