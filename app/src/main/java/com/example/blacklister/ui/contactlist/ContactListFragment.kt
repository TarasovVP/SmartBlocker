package com.example.blacklister.ui.contactlist

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.blacklister.databinding.ContactListFragmentBinding
import com.example.blacklister.model.Contact
import com.example.blacklister.ui.base.BaseAdapter
import com.example.blacklister.ui.base.BaseListFragment

class ContactListFragment :
    BaseListFragment<ContactListFragmentBinding, ContactListViewModel, Contact>() {

    override fun getViewBinding() = ContactListFragmentBinding.inflate(layoutInflater)

    override val viewModelClass = ContactListViewModel::class.java

    override fun createAdapter(): BaseAdapter<Contact, *>? {
        return context?.let {
            ContactAdapter(object : ContactClickListener {
                override fun onContactClicked(contact: Contact) {
                    findNavController().navigate(
                        ContactListFragmentDirections.startContactDetail(
                            contact = contact
                        )
                    )
                }
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefresh = binding?.contactListRefresh
        binding?.contactListRecyclerView?.initRecyclerView()
    }

    override fun observeLiveData() {
        with(viewModel) {
            contactLiveData?.observe(viewLifecycleOwner, { contactList ->
                onInitialDataLoaded(contactList)
            })
        }
    }

    override fun getDataList() {
        viewModel.getContactList()
    }
}