package com.example.blacklister.ui.contactlist

import androidx.navigation.fragment.findNavController
import com.example.blacklister.databinding.ContactListFragmentBinding
import com.example.blacklister.extensions.hashMapFromList
import com.example.blacklister.model.Contact
import com.example.blacklister.ui.base.BaseAdapter
import com.example.blacklister.ui.base.BaseListFragment
import com.example.blacklister.utils.HeaderDataItem
import com.example.blacklister.utils.HeaderDataItem.Companion.HEADER_TYPE
import java.util.*

class ContactListFragment :
    BaseListFragment<ContactListFragmentBinding, ContactListViewModel, Contact>() {

    override fun getViewBinding() = ContactListFragmentBinding.inflate(layoutInflater)

    override val viewModelClass = ContactListViewModel::class.java

    private var contactList: List<Contact>? = null

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

    override fun initView() {
        swipeRefresh = binding?.contactListRefresh
        recyclerView = binding?.contactListRecyclerView
        searchableEditText = binding?.contactListSearch
    }

    override fun observeLiveData() {
        with(viewModel) {
            contactLiveData?.observe(viewLifecycleOwner, { contactList ->
                this@ContactListFragment.contactList = contactList
                setContactList(contactList)
            })
        }
    }

    private fun setContactList(contactList: List<Contact>) {
        val contactHashMap = contactList.hashMapFromList()
        adapter?.clearData()
        for (contactEntry in contactHashMap) {
            dataLoaded(
                contactEntry.value,
                HeaderDataItem(headerType = HEADER_TYPE, header = contactEntry.key)
            )
        }
        adapter?.notifyDataSetChanged()
    }

    override fun getDataList() {
        viewModel.getContactList()
    }

    override fun filterDataList() {
        val filteredContactList = contactList?.filter { callLog ->
            callLog.name?.lowercase(Locale.getDefault())?.contains(
                searchableEditText?.text.toString()
                    .lowercase(Locale.getDefault())
            ) == true || callLog.phone?.lowercase(Locale.getDefault())?.contains(
                searchableEditText?.text.toString()
                    .lowercase(Locale.getDefault())
            ) == true
        } as ArrayList<Contact>
        setContactList(filteredContactList)
    }
}