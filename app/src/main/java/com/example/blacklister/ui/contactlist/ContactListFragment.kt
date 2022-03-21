package com.example.blacklister.ui.contactlist

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.blacklister.databinding.ContactListFragmentBinding
import com.example.blacklister.extensions.hashMapFromList
import com.example.blacklister.model.Contact
import com.example.blacklister.ui.base.BaseAdapter
import com.example.blacklister.ui.base.BaseListFragment
import com.example.blacklister.utils.HeaderDataItem
import com.example.blacklister.utils.HeaderDataItem.Companion.HEADER_TYPE
import com.google.gson.Gson

class ContactListFragment :
    BaseListFragment<ContactListFragmentBinding, ContactListViewModel, Contact>() {

    override fun getViewBinding() = ContactListFragmentBinding.inflate(layoutInflater)

    override val viewModelClass = ContactListViewModel::class.java

    override fun createAdapter(): BaseAdapter<Contact>? {
        return context?.let {
            ContactAdapter { contact ->
                findNavController().navigate(
                    ContactListFragmentDirections.startContactDetail(
                        contact = contact
                    )
                )
            }
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
                val contactHashMap = contactList.hashMapFromList()
                adapter?.clearData()
                for (contactEntry in contactHashMap) {
                    dataLoaded(
                        contactEntry.value,
                        HeaderDataItem(headerType = HEADER_TYPE, header = contactEntry.key)
                    )
                }
            })
        }
    }

    override fun getDataList() {
        viewModel.getContactList()
    }
}