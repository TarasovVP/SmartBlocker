package com.tarasovvp.blacklister.ui.main.contact_list

import android.content.Context
import android.util.Log
import androidx.core.view.isInvisible
import androidx.navigation.fragment.findNavController
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.FragmentContactListBinding
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.ui.base.BaseListFragment
import com.tarasovvp.blacklister.ui.main.call_list.CallAdapter
import java.util.*

open class ContactListFragment :
    BaseListFragment<FragmentContactListBinding, ContactListViewModel, Contact>() {

    override var layoutId = R.layout.fragment_contact_list
    override val viewModelClass = ContactListViewModel::class.java

    private var contactList: List<Contact>? = null

    override fun createAdapter(): BaseAdapter<Contact>? {
        Log.e("adapterTAG", "ContactListFragment createAdapter  contactList?.size ${contactList?.size}")
        return context?.let {
            ContactAdapter { phone ->
                findNavController().navigate(ContactListFragmentDirections.startContactDetailFragment(
                    number = phone))
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.e("adapterTAG", "ContactListFragment onAttach adapter $adapter")
    }

    override fun onResume() {
        super.onResume()
        Log.e("adapterTAG", "ContactListFragment onResume adapter $adapter itemCount ${adapter?.itemCount}")
    }

    override fun initView() {
        binding?.apply {
            swipeRefresh = contactListRefresh
            recyclerView = contactListRecyclerView
            emptyStateContainer = contactListEmpty
            contactListCheck.setOnCheckedChangeListener { compoundButton, checked ->
                Log.e("adapterTAG", "ContactListFragment setOnCheckedChangeListener compoundButton.isPressed ${compoundButton.isPressed} checked $checked")
                if (compoundButton.isPressed) {
                    searchDataList()
                }
            }
        }
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
        (adapter as? ContactAdapter)?.searchQuery = searchQuery.orEmpty()
        val filteredContactList = contactList?.filter { contact ->
            (contact.name?.lowercase(Locale.getDefault())?.contains(
                searchQuery?.lowercase(Locale.getDefault()).orEmpty()
            ).isTrue() || contact.phone.lowercase(Locale.getDefault()).contains(
                searchQuery?.lowercase(Locale.getDefault()).orEmpty()
            ).isTrue()) && (if (binding?.contactListCheck?.isChecked.isTrue()) contact.isBlackFilter() else true)
        }.orEmpty()
        Log.e("adapterTAG", "ContactListFragment searchDataList filteredContactList.size ${filteredContactList.size} contactListCheck?.isChecked ${binding?.contactListCheck?.isChecked.isTrue()}")
        binding?.contactListCheck?.isInvisible = (filteredContactList.isNotEmpty() || binding?.contactListCheck?.isChecked.isTrue()).not()
        checkDataListEmptiness(filteredContactList, binding?.contactListCheck?.isChecked.isTrue())
        viewModel.getHashMapFromContactList(filteredContactList)
    }

    override fun getData() {
        Log.e("adapterTAG", "ContactListFragment getData()")
        viewModel.getContactList()
    }
}