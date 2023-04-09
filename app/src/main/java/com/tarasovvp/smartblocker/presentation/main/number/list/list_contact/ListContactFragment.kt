package com.tarasovvp.smartblocker.presentation.main.number.list.list_contact

import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.databinding.FragmentListContactBinding
import com.tarasovvp.smartblocker.domain.enums.Info
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.domain.models.InfoData
import com.tarasovvp.smartblocker.presentation.base.BaseAdapter
import com.tarasovvp.smartblocker.presentation.base.BaseListFragment
import com.tarasovvp.smartblocker.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint
import kotlin.collections.ArrayList

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
        binding?.listContactCheck?.apply {
            isSelected = true
            text = context?.numberDataFilteringText(conditionFilterIndexes ?: arrayListOf())
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
                    previousDestinationId = findNavController().currentDestination?.id.orZero(),
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
            (searchQuery.isNullOrBlank().not() && (contactWithFilter.contact?.name isContaining  searchQuery || contactWithFilter.contact?.trimmedPhone isContaining searchQuery))
                    && (contactWithFilter.filterWithCountryCode?.filter?.isBlocker().isTrue() && conditionFilterIndexes?.contains(NumberDataFiltering.CONTACT_WITH_BLOCKER.ordinal).isTrue()
                    || contactWithFilter.filterWithCountryCode?.filter?.isPermission().isTrue() && conditionFilterIndexes?.contains(NumberDataFiltering.CONTACT_WITH_PERMISSION.ordinal).isTrue()
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