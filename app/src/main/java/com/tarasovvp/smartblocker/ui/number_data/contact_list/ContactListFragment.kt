package com.tarasovvp.smartblocker.ui.number_data.contact_list

import android.content.Context
import android.util.Log
import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentContactListBinding
import com.tarasovvp.smartblocker.enums.Info
import com.tarasovvp.smartblocker.extensions.*
import com.tarasovvp.smartblocker.model.Contact
import com.tarasovvp.smartblocker.ui.base.BaseAdapter
import com.tarasovvp.smartblocker.ui.base.BaseListFragment
import com.tarasovvp.smartblocker.utils.setSafeOnClickListener
import java.util.*

open class ContactListFragment :
    BaseListFragment<FragmentContactListBinding, ContactListViewModel, Contact>() {

    override var layoutId = R.layout.fragment_contact_list
    override val viewModelClass = ContactListViewModel::class.java

    private var contactList: List<Contact>? = null

    override fun createAdapter(): BaseAdapter<Contact>? {
        Log.e("adapterTAG",
            "ContactListFragment createAdapter  contactList?.size ${contactList?.size}")
        return context?.let {
            ContactAdapter { numberData ->
                findNavController().navigate(ContactListFragmentDirections.startNumberDataDetailFragment(
                    numberData))
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.e("adapterTAG", "ContactListFragment onAttach adapter $adapter")
    }

    override fun onResume() {
        super.onResume()
        Log.e("adapterTAG",
            "ContactListFragment onResume adapter $adapter itemCount ${adapter?.itemCount}")
    }

    override fun initView() {
        binding?.apply {
            swipeRefresh = contactListRefresh
            recyclerView = contactListRecyclerView
            emptyStateContainer = contactListEmpty
            contactListCheck.isEnabled =
                adapter?.itemCount.orZero() > 0 || contactListCheck.isChecked
            contactListRecyclerView.hideKeyboardWithLayoutTouch()
            contactListInfo.setSafeOnClickListener {
                contactListInfo.showPopUpWindow(Info.INFO_CONTACT_LIST)
            }
            contactListCheck.setOnCheckedChangeListener { compoundButton, checked ->
                root.hideKeyboard()
                Log.e("adapterTAG",
                    "ContactListFragment setOnCheckedChangeListener compoundButton.isPressed ${compoundButton.isPressed} checked $checked")
                if (compoundButton.isPressed) {
                    searchDataList()
                }
            }
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            contactLiveData.safeSingleObserve(viewLifecycleOwner) { contactList ->
                if (contactList == this@ContactListFragment.contactList) {
                    checkDataListEmptiness(contactList.isEmpty())
                    return@safeSingleObserve
                }
                this@ContactListFragment.contactList = contactList
                searchDataList()
            }
            contactHashMapLiveData.safeSingleObserve(viewLifecycleOwner) { contactHashMap ->
                contactHashMap?.let { setDataList(it) }
            }
        }
    }

    override fun isFiltered(): Boolean {
        return binding?.contactListCheck?.isChecked.isTrue()
    }

    override fun searchDataList() {
        (adapter as? ContactAdapter)?.searchQuery = searchQuery.orEmpty()
        val filteredContactList = contactList?.filter { contact ->
            (contact.name?.lowercase(Locale.getDefault())?.contains(
                searchQuery?.lowercase(Locale.getDefault()).orEmpty()
            ).isTrue() || contact.trimmedPhone.lowercase(Locale.getDefault()).contains(
                searchQuery?.lowercase(Locale.getDefault()).orEmpty()
            )
                .isTrue()) && (if (binding?.contactListCheck?.isChecked.isTrue()) contact.isBlackFilter() else true)
        }.orEmpty()
        Log.e("adapterTAG",
            "ContactListFragment searchDataList filteredContactList.size ${filteredContactList.size} contactListCheck?.isChecked ${binding?.contactListCheck?.isChecked.isTrue()}")
        binding?.contactListCheck?.isEnabled =
            filteredContactList.isNotEmpty() || binding?.contactListCheck?.isChecked.isTrue()
        checkDataListEmptiness(filteredContactList.isEmpty())
        viewModel.getHashMapFromContactList(filteredContactList, swipeRefresh?.isRefreshing.isTrue())
    }

    override fun getData() {
        Log.e("adapterTAG", "ContactListFragment getData()")
        viewModel.getContactList(swipeRefresh?.isRefreshing.isTrue())
    }
}