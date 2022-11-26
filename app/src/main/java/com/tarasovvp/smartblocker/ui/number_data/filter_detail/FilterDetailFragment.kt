package com.tarasovvp.smartblocker.ui.number_data.filter_detail

import android.util.Log
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.constants.Constants.FILTER_ACTION
import com.tarasovvp.smartblocker.constants.Constants.PERMISSION
import com.tarasovvp.smartblocker.databinding.FragmentFilterDetailBinding
import com.tarasovvp.smartblocker.enums.EmptyState
import com.tarasovvp.smartblocker.enums.FilterAction
import com.tarasovvp.smartblocker.extensions.isTrue
import com.tarasovvp.smartblocker.extensions.safeSingleObserve
import com.tarasovvp.smartblocker.models.Filter
import com.tarasovvp.smartblocker.models.NumberData
import com.tarasovvp.smartblocker.ui.MainActivity
import com.tarasovvp.smartblocker.ui.base.BaseDetailFragment
import com.tarasovvp.smartblocker.ui.number_data.NumberDataAdapter
import com.tarasovvp.smartblocker.utils.setSafeOnClickListener

class FilterDetailFragment :
    BaseDetailFragment<FragmentFilterDetailBinding, FilterDetailViewModel>() {

    override var layoutId = R.layout.fragment_filter_detail
    override val viewModelClass = FilterDetailViewModel::class.java
    private val args: FilterDetailFragmentArgs by navArgs()

    private var numberDataAdapter: NumberDataAdapter? = null
    private var contactList: ArrayList<NumberData>? = null

    override fun initViews() {
        Log.e("filterDetailTAG",
            "FilterDetailFragment initViews before args.filter ${args.filterDetail} binding?.filter ${binding?.filter}")
        binding?.apply {
            args.filterDetail?.let { filter ->
                (activity as MainActivity).toolbar?.title = getString(filter.filterTypeTitle())
                this.filter = filter

                Log.e("filterDetailTAG",
                    "FilterDetailFragment initViews after args.filter $filter binding?.filter ${this.filter}")
                filterDetailAddFilter.filter =
                    Filter(filterType = filter.filterType).apply {
                        filterAction =
                            if (filter.isBlackFilter()) FilterAction.FILTER_ACTION_BLOCKER_ADD else FilterAction.FILTER_ACTION_PERMISSION_ADD
                    }
                filterDetailChangeFilter.filter =
                    Filter(filterType = if (filter.isBlackFilter()) PERMISSION else BLOCKER).apply {
                        filterAction =
                            if (filter.isBlackFilter()) FilterAction.FILTER_ACTION_BLOCKER_TRANSFER else FilterAction.FILTER_ACTION_PERMISSION_TRANSFER
                    }
                filterDetailDeleteFilter.filter =
                    Filter(filterType = filter.filterType).apply {
                        filterAction =
                            if (filter.isBlackFilter()) FilterAction.FILTER_ACTION_BLOCKER_DELETE else FilterAction.FILTER_ACTION_PERMISSION_DELETE
                    }
                filterDetailContactListDescription.text =
                    if (filter.isBlackFilter()) getString(R.string.contact_list_with_blocker) else getString(
                        R.string.contact_list_with_allow)

            }
            filterDetailContactListEmpty.emptyState = if (filter?.isBlackFilter()
                    .isTrue()
            ) EmptyState.EMPTY_STATE_FILTERS_CONTACTS_BY_BLOCKER else EmptyState.EMPTY_STATE_FILTERS_CONTACTS_BY_PERMISSION
            contactList?.let {
                filterDetailContactListEmpty.root.isVisible = it.isEmpty()
            }
            executePendingBindings()
        }
        setFragmentResultListeners()
    }

    private fun setFragmentResultListeners() {
        binding?.filter?.let { filter ->
            setFragmentResultListener(FILTER_ACTION) { _, bundle ->
                when (val filterAction = bundle.getSerializable(FILTER_ACTION) as FilterAction) {
                    FilterAction.FILTER_ACTION_BLOCKER_TRANSFER,
                    FilterAction.FILTER_ACTION_PERMISSION_TRANSFER,
                    -> viewModel.updateFilter(filter.apply {
                        filterType = if (this.isBlackFilter()) PERMISSION else BLOCKER
                        this.filterAction = filterAction
                    })
                    FilterAction.FILTER_ACTION_BLOCKER_DELETE,
                    FilterAction.FILTER_ACTION_PERMISSION_DELETE,
                    -> viewModel.deleteFilter(filter.apply {
                        this.filterAction = filterAction
                    })
                    FilterAction.FILTER_ACTION_BLOCKER_ADD,
                    FilterAction.FILTER_ACTION_PERMISSION_ADD,
                    -> viewModel.insertFilter(filter.apply {
                        numberData = this.filter
                        this.filterAction = filterAction
                        filterWithoutCountryCode = extractFilterWithoutCountryCode()
                    })
                    else -> Unit
                }
            }
        }
    }

    override fun setClickListeners() {
        binding?.apply {
            filterDetailChangeFilter.root.setSafeOnClickListener {
                startFilterActionDialog(if (filter?.isBlackFilter().isTrue()) FilterAction.FILTER_ACTION_BLOCKER_TRANSFER else FilterAction.FILTER_ACTION_PERMISSION_TRANSFER)
            }
            filterDetailDeleteFilter.root.setSafeOnClickListener {
                startFilterActionDialog(if (filter?.isBlackFilter().isTrue()) FilterAction.FILTER_ACTION_BLOCKER_DELETE else FilterAction.FILTER_ACTION_PERMISSION_DELETE)
            }
            filterDetailAddFilter.root.setSafeOnClickListener {
                startFilterActionDialog(if (filter?.isBlackFilter().isTrue()) FilterAction.FILTER_ACTION_BLOCKER_ADD else FilterAction.FILTER_ACTION_PERMISSION_ADD)
            }
            filterDetailItemFilter.itemFilterDetailPreview.setSafeOnClickListener {
                findNavController().navigate(FilterDetailFragmentDirections.startBlockerCallsDetailFragment(
                    filter = filter))
            }
        }
    }

    private fun startFilterActionDialog(filterAction: FilterAction) {
        findNavController().navigate(FilterDetailFragmentDirections.startFilterActionDialog(
            filterNumber = String.format(getString(R.string.number_value), binding?.filter?.filter),
            filterAction = filterAction))
    }

    override fun createAdapter() {
        numberDataAdapter =
            numberDataAdapter ?: NumberDataAdapter(contactList) { numberData ->
                findNavController().navigate(FilterDetailFragmentDirections.startNumberDataDetailFragment(
                    numberData))
            }
        binding?.filterDetailContactList?.adapter = numberDataAdapter
    }

    override fun getData() {
        binding?.filter?.let { viewModel.getQueryContactCallList(it) }
    }

    override fun observeLiveData() {
        with(viewModel) {
            numberDataListLiveData.safeSingleObserve(viewLifecycleOwner) { contactList ->
                binding?.filterDetailContactListEmpty?.root?.isVisible =
                    contactList.isEmpty().isTrue()
                binding?.filterDetailContactListDescription?.isVisible =
                    contactList.isNotEmpty().isTrue()
                if (this@FilterDetailFragment.contactList == contactList) return@safeSingleObserve
                this@FilterDetailFragment.contactList = contactList.apply {
                    forEach {
                        it.searchText = binding?.filter?.filter.orEmpty()
                    }
                }
                numberDataAdapter?.numberDataList = contactList
                numberDataAdapter?.notifyDataSetChanged()
            }
            filterActionLiveData.safeSingleObserve(viewLifecycleOwner) { filter ->
                handleSuccessFilterAction(filter)
            }
        }
    }

    private fun handleSuccessFilterAction(filter: Filter) {
        (activity as MainActivity).apply {
            showMessage(String.format(filter.filterAction?.successText?.let { getString(it) }
                .orEmpty(), binding?.filter?.filter.orEmpty()), false)
            getAllData()
            if (filter.isChangeFilterAction()) {
                mainViewModel.successAllDataLiveData.safeSingleObserve(viewLifecycleOwner) {
                    initViews()
                    viewModel.getQueryContactCallList(filter)
                }
            } else {
                findNavController().navigate(if (binding?.filter?.isBlackFilter()
                        .isTrue()
                ) FilterDetailFragmentDirections.startBlackFilterListFragment()
                else FilterDetailFragmentDirections.startWhiteFilterListFragment())
            }
        }
    }
}