package com.tarasovvp.smartblocker.ui.number_data.contact_list

import android.util.Log
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants
import com.tarasovvp.smartblocker.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.constants.Constants.PERMISSION
import com.tarasovvp.smartblocker.databinding.FragmentContactListBinding
import com.tarasovvp.smartblocker.enums.Info
import com.tarasovvp.smartblocker.extensions.*
import com.tarasovvp.smartblocker.models.Contact
import com.tarasovvp.smartblocker.ui.base.BaseAdapter
import com.tarasovvp.smartblocker.ui.base.BaseListFragment
import com.tarasovvp.smartblocker.ui.number_data.call_list.CallListFragmentDirections
import com.tarasovvp.smartblocker.utils.setSafeOnClickListener
import java.util.*

open class ContactListFragment :
    BaseListFragment<FragmentContactListBinding, ContactListViewModel, Contact>() {

    override var layoutId = R.layout.fragment_contact_list
    override val viewModelClass = ContactListViewModel::class.java

    private var contactList: List<Contact>? = null
    private var conditionFilterIndexes: ArrayList<Int>? = null

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

    override fun initView() {
        binding?.apply {
            swipeRefresh = contactListRefresh
            recyclerView = contactListRecyclerView
            emptyStateContainer = contactListEmpty
            contactListRecyclerView.hideKeyboardWithLayoutTouch()
        }
        setContactConditionFilter()
        setClickListeners()
        setFragmentResultListeners()
    }

    private fun setContactConditionFilter() {
        conditionFilterIndexes = conditionFilterIndexes ?: arrayListOf()
        binding?.contactListCheck?.apply {
            isSelected = true
            val callFilteringText = arrayListOf<String>()
            if (conditionFilterIndexes.isNullOrEmpty())
                callFilteringText.add(getString(R.string.filter_no_filter))
            else {
                if (conditionFilterIndexes?.contains(BLOCKER).isTrue())
                    callFilteringText.add(context.getString(R.string.with_blocker_filter))
                if (conditionFilterIndexes?.contains(PERMISSION).isTrue())
                    callFilteringText.add(context.getString(R.string.with_permission_filter))
            }
            text = callFilteringText.joinToString()
            isChecked = conditionFilterIndexes.isNullOrEmpty().not()
            isEnabled =
                adapter?.itemCount.orZero() > 0 || conditionFilterIndexes.isNullOrEmpty().not()
        }
    }

    private fun setClickListeners() {
        binding?.contactListCheck?.setSafeOnClickListener {
            binding?.root?.hideKeyboard()
            binding?.contactListCheck?.isChecked = binding?.contactListCheck?.isChecked.isTrue().not()
            findNavController().navigate(
                CallListFragmentDirections.startNumberDataFilteringDialog(filteringList = conditionFilterIndexes.orEmpty()
                    .toIntArray()))
        }
        binding?.contactListInfo?.setSafeOnClickListener {
            binding?.contactListInfo?.showPopUpWindow(Info.INFO_CONTACT_LIST)
        }
    }

    private fun setFragmentResultListeners() {
        setFragmentResultListener(Constants.FILTER_CONDITION_LIST) { _, bundle ->
            conditionFilterIndexes = bundle.getIntegerArrayList(Constants.FILTER_CONDITION_LIST)
            setContactConditionFilter()
            searchDataList()
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
        return conditionFilterIndexes.isNullOrEmpty().not()
    }

    override fun searchDataList() {
        (adapter as? ContactAdapter)?.searchQuery = searchQuery.orEmpty()
        val filteredContactList = contactList?.filter { contact ->
            (contact.name?.lowercase(Locale.getDefault())?.contains(
                searchQuery?.lowercase(Locale.getDefault()).orEmpty()
            ).isTrue() || contact.trimmedPhone.lowercase(Locale.getDefault()).contains(
                searchQuery?.lowercase(Locale.getDefault()).orEmpty()
            )
                .isTrue()) && (contact.filter?.isBlocker().isTrue() && conditionFilterIndexes?.contains(BLOCKER).isTrue() ||
                    contact.filter?.isPermission().isTrue() && conditionFilterIndexes?.contains(PERMISSION).isTrue()
                    || conditionFilterIndexes.isNullOrEmpty())
        }.orEmpty()
        Log.e("adapterTAG",
            "ContactListFragment searchDataList filteredContactList.size ${filteredContactList.size} contactListCheck?.isChecked ${binding?.contactListCheck?.isChecked.isTrue()}")
        binding?.contactListCheck?.isEnabled =
            filteredContactList.isNotEmpty() || binding?.contactListCheck?.isChecked.isTrue()
        checkDataListEmptiness(filteredContactList.isEmpty())
        viewModel.getHashMapFromContactList(filteredContactList,
            swipeRefresh?.isRefreshing.isTrue())
    }

    override fun getData() {
        Log.e("adapterTAG", "ContactListFragment getData()")
        viewModel.getContactList(swipeRefresh?.isRefreshing.isTrue())
    }
}