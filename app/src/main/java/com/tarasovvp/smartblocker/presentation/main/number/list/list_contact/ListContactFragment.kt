package com.tarasovvp.smartblocker.presentation.main.number.list.list_contact

import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PERMISSION
import com.tarasovvp.smartblocker.databinding.FragmentListContactBinding
import com.tarasovvp.smartblocker.domain.enums.Info
import com.tarasovvp.smartblocker.domain.models.InfoData
import com.tarasovvp.smartblocker.presentation.base.BaseAdapter
import com.tarasovvp.smartblocker.presentation.base.BaseListFragment
import com.tarasovvp.smartblocker.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
open class ListContactFragment :
    BaseListFragment<FragmentListContactBinding, ListContactViewModel, ContactWithFilter>() {

    override var layoutId = R.layout.fragment_list_contact
    override val viewModelClass = ListContactViewModel::class.java

    private var contactWithFilterList: List<ContactWithFilter>? = null
    var conditionFilterIndexes: ArrayList<Int>? = null

    override fun createAdapter(): BaseAdapter<ContactWithFilter>? {
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
            emptyStateContainer = listContactEmpty
            listContactRecyclerView.hideKeyboardWithLayoutTouch()
        }
        setContactConditionFilter()
    }

    private fun setContactConditionFilter() {
        conditionFilterIndexes = conditionFilterIndexes ?: arrayListOf()
        binding?.listContactCheck?.apply {
            isSelected = true
            val callFilteringText = arrayListOf<String>()
            if (conditionFilterIndexes.isNullOrEmpty())
                callFilteringText.add(getString(R.string.filter_no_filter))
            else {
                if (conditionFilterIndexes?.contains(BLOCKER).isTrue())
                    callFilteringText.add(context.getString(R.string.filter_contact_blocker))
                if (conditionFilterIndexes?.contains(PERMISSION).isTrue())
                    callFilteringText.add(context.getString(R.string.filter_contact_permission))
            }
            text = callFilteringText.joinToString()
            isChecked = conditionFilterIndexes.isNullOrEmpty().not()
            isEnabled =
                adapter?.itemCount.orZero() > 0 || conditionFilterIndexes.isNullOrEmpty().not()
        }
    }

    override fun setClickListeners() {
        binding?.listContactCheck?.setSafeOnClickListener {
            binding?.root?.hideKeyboard()
            binding?.listContactCheck?.isChecked =
                binding?.listContactCheck?.isChecked.isTrue().not()
            findNavController().navigate(
                ListContactFragmentDirections.startNumberDataFilteringDialog(
                    filteringList = conditionFilterIndexes.orEmpty()
                        .toIntArray()
                )
            )
        }
        binding?.listContactInfo?.setSafeOnClickListener {
            showInfoScreen()
        }
    }

    override fun setFragmentResultListeners() {
        setFragmentResultListener(Constants.FILTER_CONDITION_LIST) { _, bundle ->
            conditionFilterIndexes = bundle.getIntegerArrayList(Constants.FILTER_CONDITION_LIST)
            setContactConditionFilter()
            searchDataList()
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            contactLiveData.safeSingleObserve(viewLifecycleOwner) { contactList ->
                if (contactList == this@ListContactFragment.contactWithFilterList) {
                    checkDataListEmptiness(contactList.isEmpty())
                    return@safeSingleObserve
                }
                this@ListContactFragment.contactWithFilterList = contactList
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
        val filteredContactList = contactWithFilterList?.filter { contactWithFilter ->
            (contactWithFilter.contact?.name?.lowercase(Locale.getDefault())?.contains(
                searchQuery?.lowercase(Locale.getDefault()).orEmpty()
            ).isTrue() || contactWithFilter.contact?.trimmedPhone?.lowercase(Locale.getDefault())?.contains(
                searchQuery?.lowercase(Locale.getDefault()).orEmpty()
            )
                .isTrue()) && (contactWithFilter.filterWithCountryCode?.filter?.isBlocker()
                .isTrue() && conditionFilterIndexes?.contains(BLOCKER).isTrue() ||
                    contactWithFilter.filterWithCountryCode?.filter?.isPermission().isTrue() && conditionFilterIndexes?.contains(
                PERMISSION).isTrue()
                    || conditionFilterIndexes.isNullOrEmpty())
        }.orEmpty()
        binding?.listContactCheck?.isEnabled =
            filteredContactList.isNotEmpty() || binding?.listContactCheck?.isChecked.isTrue()
        checkDataListEmptiness(filteredContactList.isEmpty())
        viewModel.getHashMapFromContactList(filteredContactList,
            swipeRefresh?.isRefreshing.isTrue())
    }

    override fun getData() {
        viewModel.getContactsWithFilters(swipeRefresh?.isRefreshing.isTrue())
    }

    override fun showInfoScreen() {
        findNavController().navigate(
            ListContactFragmentDirections.startInfoFragment(
                info = InfoData(
                    title = getString(Info.INFO_CONTACT_LIST.title),
                    description = getString(Info.INFO_CONTACT_LIST.description)
                )
            )
        )
    }
}