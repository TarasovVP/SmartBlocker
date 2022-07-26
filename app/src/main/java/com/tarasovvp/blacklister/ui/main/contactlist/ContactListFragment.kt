package com.tarasovvp.blacklister.ui.main.contactlist

import android.util.Log
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.FragmentContactListBinding
import com.tarasovvp.blacklister.extensions.isNotNull
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.ui.base.BaseListFragment
import com.tarasovvp.blacklister.ui.main.numberadd.NumberAddFragmentDirections
import com.tarasovvp.blacklister.utils.setSafeOnClickListener
import java.util.*

open class ContactListFragment :
    BaseListFragment<FragmentContactListBinding, ContactListViewModel, Contact>() {

    override fun getViewBinding() = FragmentContactListBinding.inflate(layoutInflater)

    override val viewModelClass = ContactListViewModel::class.java
    private val args: ContactListFragmentArgs by navArgs()

    private var contactList: List<Contact>? = null

    override fun createAdapter(): BaseAdapter<Contact>? {
        return context?.let {
            ContactAdapter { number ->
                findNavController().navigate(ContactListFragmentDirections.startNumberDetailFragment(
                    number = number))
            }
        }
    }

    override fun initView() {
        swipeRefresh = binding?.contactListRefresh
        recyclerView = binding?.contactListRecyclerView
        emptyListText = binding?.contactListEmpty
        priorityText = binding?.contactListPriority
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
                Log.e("allDataTAG", "ContactListFragment contactLiveData contactList contains(\"Мой зай\") ${Gson().toJson(contactList.filter { it.name?.contains("Мой зай").isTrue() })}")
                if (!checkDataListEmptiness(contactList)) {
                    getHashMapFromContactList(contactList)
                }
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
            ).isTrue() || contact.phone?.lowercase(Locale.getDefault())?.contains(
                searchQuery?.lowercase(Locale.getDefault()).orEmpty()
            )
                .isTrue()) && (if (binding?.contactListCheck?.isChecked.isTrue()) contact.isBlackList else true)
        } as? ArrayList<Contact>
        filteredContactList?.apply {
            if (!checkDataListEmptiness(this)) {
                viewModel.getHashMapFromContactList(this)
            }
        }
    }

    override fun getData() {
        Log.e("getAllDataTAG", "ContactListFragment getAllData")
        viewModel.getContactList()
    }
}