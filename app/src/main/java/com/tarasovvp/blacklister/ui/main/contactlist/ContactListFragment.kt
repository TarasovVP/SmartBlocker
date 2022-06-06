package com.tarasovvp.blacklister.ui.main.contactlist

import androidx.appcompat.widget.PopupMenu
import androidx.navigation.fragment.findNavController
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.databinding.FragmentContactListBinding
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.extensions.showPopUpMenu
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
            ContactAdapter { contact, view ->
                val listener = PopupMenu.OnMenuItemClickListener { item ->
                    when (item?.itemId) {
                        R.id.change -> {
                            findNavController().navigate(ContactListFragmentDirections.startInfoDialog(
                                contact = contact))
                        }
                        R.id.details -> {
                            findNavController().navigate(ContactListFragmentDirections.startNumberDetailFragment(
                                number = contact.phone))
                        }
                    }
                    true
                }
                it.showPopUpMenu(if (contact.isBlackList) R.menu.number_delete_menu else R.menu.number_add_menu,
                    view,
                    listener)
            }
        }
    }

    override fun initView() {
        swipeRefresh = binding?.contactListRefresh
        recyclerView = binding?.contactListRecyclerView
        searchableEditText = binding?.contactListSearch
        emptyListText = binding?.contactListEmpty
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Contact>(Constants.CONTACT)
            ?.safeSingleObserve(viewLifecycleOwner) { blackNumber ->
                viewModel.updateContact(blackNumber)
            }
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

    override fun searchDataList() {
        val filteredContactList = contactList?.filter { contact ->
            (contact.name?.lowercase(Locale.getDefault())?.contains(
                searchableEditText?.text.toString()
                    .lowercase(Locale.getDefault())
            ).isTrue() || contact.phone?.lowercase(Locale.getDefault())?.contains(
                searchableEditText?.text.toString()
                    .lowercase(Locale.getDefault())
            )
                .isTrue()) && (if (binding?.contactListCheck?.isChecked.isTrue()) contact.isBlackList else true)
        } as? ArrayList<Contact>
        filteredContactList?.apply {
            if (!checkDataListEmptiness(this)) {
                viewModel.getHashMapFromContactList(this)
            }
        }
    }
}