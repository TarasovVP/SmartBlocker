package com.example.blacklister.ui.contactlist

import com.example.blacklister.databinding.ContactListFragmentBinding
import com.example.blacklister.ui.base.BaseFragment

class ContactListFragment : BaseFragment<ContactListFragmentBinding, ContactListViewModel>() {

    override fun getViewBinding() = ContactListFragmentBinding.inflate(layoutInflater)

    override val viewModelClass = ContactListViewModel::class.java

}