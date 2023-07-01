package com.tarasovvp.smartblocker.presentation.main.number.list.list_contact

import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentListContactBinding
import com.tarasovvp.smartblocker.domain.enums.Info
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.presentation.base.BaseAdapter
import com.tarasovvp.smartblocker.presentation.base.BaseListFragment
import com.tarasovvp.smartblocker.presentation.ui_models.ContactWithFilterUIModel
import com.tarasovvp.smartblocker.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class ListContactFragment :
    BaseListFragment<FragmentListContactBinding, ListContactViewModel, ContactWithFilterUIModel>() {

    override var layoutId = R.layout.fragment_list_contact
    override val viewModelClass = ListContactViewModel::class.java

    private var contactWithFilterList: List<ContactWithFilterUIModel>? = null

    override fun createAdapter(): BaseAdapter<ContactWithFilterUIModel>? {
        return context?.let {
            ContactAdapter { numberData ->
                findNavController().navigate(
                    ListContactFragmentDirections.startDetailsNumberDataFragment(
                    numberData)
                )
            }
        }
    }

    override fun initViews() {
        binding?.apply {
            swipeRefresh = listContactRefresh
            recyclerView = listContactRecyclerView
            filterCheck = listContactCheck
            emptyStateContainer = listContactEmpty
            listContactRecyclerView.hideKeyboardWithLayoutTouch()
        }
    }

    private fun setContactListData(contactWithFilterList: List<ContactWithFilterUIModel>) {
        binding?.listContactCheck?.isEnabled = contactWithFilterList.isNotEmpty() || binding?.listContactCheck?.isChecked.isTrue()
        checkDataListEmptiness(contactWithFilterList.isEmpty())
        setDataList(contactWithFilterList.groupBy {
            if (it.contactName.isEmpty()) String.EMPTY else it.contactName[0].toString()
        })
    }

    override fun setClickListeners() {
        binding?.listContactCheck?.setSafeOnClickListener {
            binding?.root?.hideKeyboard()
            binding?.listContactCheck?.isChecked = binding?.listContactCheck?.isChecked.isTrue().not()
            findNavController().navigate(
                ListContactFragmentDirections.startNumberDataFilteringDialog(
                    previousDestinationId = findNavController().currentDestination?.id.orZero(),
                    filteringList = filterIndexes.orEmpty().toIntArray()
                )
            )
        }
        binding?.listContactInfo?.setSafeOnClickListener {
            showInfoScreen()
        }
    }

    override fun setFragmentResultListeners() {
        setFragmentResultListener(Constants.FILTER_CONDITION_LIST) { _, bundle ->
            filterIndexes = bundle.getIntegerArrayList(Constants.FILTER_CONDITION_LIST)
            setFilterCheck()
            searchDataList()
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            contactListLiveData.safeSingleObserve(viewLifecycleOwner) { contactList ->
                this@ListContactFragment.contactWithFilterList = contactList
                searchDataList()
            }
            filteredContactListLiveData.safeSingleObserve(viewLifecycleOwner) { filteredContactList ->
                setContactListData(filteredContactList)
            }
        }
    }

    override fun isFiltered(): Boolean {
        return filterIndexes.isNullOrEmpty().not()
    }

    override fun searchDataList() {
        (adapter as? ContactAdapter)?.searchQuery = searchQuery.orEmpty()
        viewModel.getFilteredContactList(contactWithFilterList.orEmpty(), searchQuery.orEmpty(), filterIndexes ?: arrayListOf())
    }

    override fun getData() {
        viewModel.getContactsWithFilters(swipeRefresh?.isRefreshing.isTrue())
    }

    override fun showInfoScreen() {
        findNavController().navigate(
            ListContactFragmentDirections.startInfoFragment(info = Info.INFO_LIST_CONTACT))
    }
}