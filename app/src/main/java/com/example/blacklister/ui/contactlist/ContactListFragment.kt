package com.example.blacklister.ui.contactlist

import android.util.Log
import androidx.navigation.fragment.findNavController
import com.example.blacklister.databinding.FragmentContactListBinding
import com.example.blacklister.model.Contact
import com.example.blacklister.ui.base.BaseAdapter
import com.example.blacklister.ui.base.BaseListFragment
import java.util.*

class ContactListFragment :
    BaseListFragment<FragmentContactListBinding, ContactListViewModel, Contact>() {

    override fun getViewBinding() = FragmentContactListBinding.inflate(layoutInflater)

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
        emptyListText = binding?.contactListEmpty
    }

    override fun observeLiveData() {
        with(viewModel) {
            contactLiveData.observe(viewLifecycleOwner, { contactList ->
                this@ContactListFragment.contactList = contactList
                if (!checkDataListEmptiness(contactList)) {
                    getHashMapFromContactList(contactList)
                }
                Log.e("dataTAG", "ContactListFragment observeLiveData setDataList")
            })
            contactHashMapLiveData.observe(viewLifecycleOwner, { contactHashMap ->
                contactHashMap?.let { setDataList(it) }
                Log.e("dataTAG", "ContactListFragment observeLiveData contactHashMapLiveData contactHashMap.size ${contactHashMap?.size}")
            })
        }
    }

    override fun getDataList() {
        Log.e("dataTAG", "ContactListFragment getDataList")
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
        if (!checkDataListEmptiness(filteredContactList)) {
            viewModel.getHashMapFromContactList(filteredContactList)
        }
    }
}