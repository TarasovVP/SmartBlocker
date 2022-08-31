package com.tarasovvp.blacklister.ui.main.contact_list

import androidx.navigation.fragment.findNavController
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.FragmentContactListBinding
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.ui.base.BaseListFragment
import java.util.*

open class ContactListFragment :
    BaseListFragment<FragmentContactListBinding, ContactListViewModel, Contact>() {

    override var layoutId = R.layout.fragment_contact_list
    override val viewModelClass = ContactListViewModel::class.java

    private var contactList: List<Contact>? = null

    override fun createAdapter(): BaseAdapter<Contact>? {
        return context?.let {
            ContactAdapter { phone ->
                findNavController().navigate(ContactListFragmentDirections.startContactDetailFragment(
                    phone = phone))
            }
        }
    }

    override fun initView() {
        swipeRefresh = binding?.contactListRefresh
        recyclerView = binding?.contactListRecyclerView
        emptyListText = binding?.contactListEmpty
        binding?.contactListCheck?.setOnCheckedChangeListener { _, _ ->
            searchDataList()
        }
    }

    override fun onResume() {
        super.onResume()
        binding?.contactListCheck?.isChecked = false
    }

    override fun observeLiveData() {
        with(viewModel) {
            contactLiveData.safeSingleObserve(viewLifecycleOwner) { contactList ->
                this@ContactListFragment.contactList = contactList
                searchDataList()
            }
            contactHashMapLiveData.safeSingleObserve(viewLifecycleOwner) { contactHashMap ->
                contactHashMap?.let { setDataList(it) }
            }
        }
        (activity as MainActivity).apply {
            mainViewModel.successAllDataLiveData.safeSingleObserve(this) {
                viewModel.getContactList()
            }
        }
    }

    override fun searchDataList() {
        val filteredContactList = contactList?.filter { contact ->
            (contact.name?.lowercase(Locale.getDefault())?.contains(
                searchQuery?.lowercase(Locale.getDefault()).orEmpty()
            ).isTrue() || contact.phone.lowercase(Locale.getDefault()).contains(
                searchQuery?.lowercase(Locale.getDefault()).orEmpty()
            )
                .isTrue()) && (if (binding?.contactListCheck?.isChecked.isTrue()) contact.isBlackFilter else true)
        }.orEmpty()
        checkDataListEmptiness(filteredContactList)
        viewModel.getHashMapFromContactList(filteredContactList)
    }

    override fun getData() {
        viewModel.getContactList()
    }
}