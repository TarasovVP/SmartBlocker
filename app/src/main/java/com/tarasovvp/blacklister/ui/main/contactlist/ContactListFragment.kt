package com.tarasovvp.blacklister.ui.main.contactlist

import androidx.navigation.fragment.findNavController
import com.tarasovvp.blacklister.databinding.FragmentContactListBinding
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.ui.base.BaseListFragment
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
            contactLiveData.safeSingleObserve(viewLifecycleOwner, { contactList ->
                this@ContactListFragment.contactList = contactList
                if (!checkDataListEmptiness(contactList)) {
                    getHashMapFromContactList(contactList)
                }
            })
            contactHashMapLiveData.safeSingleObserve(viewLifecycleOwner, { contactHashMap ->
                contactHashMap?.let { setDataList(it) }
            })
        }
    }

    override fun getDataList() {
        viewModel.getContactList()
    }

    override fun filterDataList() {
        val filteredContactList = contactList?.filter { callLog ->
            callLog.name?.lowercase(Locale.getDefault())?.contains(
                searchableEditText?.text.toString()
                    .lowercase(Locale.getDefault())
            ).isTrue() || callLog.phone?.lowercase(Locale.getDefault())?.contains(
                searchableEditText?.text.toString()
                    .lowercase(Locale.getDefault())
            ).isTrue()
        } as ArrayList<Contact>
        if (!checkDataListEmptiness(filteredContactList)) {
            viewModel.getHashMapFromContactList(filteredContactList)
        }
    }
}