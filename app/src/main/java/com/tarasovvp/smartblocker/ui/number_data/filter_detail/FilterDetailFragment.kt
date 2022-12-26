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
import com.tarasovvp.smartblocker.models.Call
import com.tarasovvp.smartblocker.models.Contact
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
    private var numberDataList: ArrayList<NumberData>? = null
    private var filteredCallList: ArrayList<NumberData>? = null

    private var isFilteredCallDetails: Boolean = false

    override fun initViews() {
        Log.e("filterDetailTAG",
            "FilterDetailFragment initViews before args.filter ${args.filterDetail} binding?.filter ${binding?.filter}")
        binding?.apply {
            args.filterDetail?.let { filter ->
                (activity as MainActivity).toolbar?.title = getString(filter.filterTypeTitle())
                this.filter = filter
                Log.e("filterDetailTAG",
                    "FilterDetailFragment initViews after args.filter $filter binding?.filter ${this.filter}")
            }
            filterDetailItemFilter.isFilteredCallDetails = isFilteredCallDetails.not()
            filterDetailContactListEmpty.emptyState = if (filter?.isBlocker()
                    .isTrue()
            ) EmptyState.EMPTY_STATE_FILTERS_CONTACTS_BY_BLOCKER else EmptyState.EMPTY_STATE_FILTERS_CONTACTS_BY_PERMISSION
            numberDataList?.let {
                filterDetailContactListEmpty.root.isVisible = it.isEmpty()
            }
            executePendingBindings()
        }
    }

    override fun setFragmentResultListeners() {
        binding?.filter?.let { filter ->
            setFragmentResultListener(FILTER_ACTION) { _, bundle ->
                when (val filterAction = bundle.getSerializable(FILTER_ACTION) as FilterAction) {
                    FilterAction.FILTER_ACTION_BLOCKER_TRANSFER,
                    FilterAction.FILTER_ACTION_PERMISSION_TRANSFER,
                    -> viewModel.updateFilter(filter.apply {
                        filterType = if (this.isBlocker()) PERMISSION else BLOCKER
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
            filterDetailChangeFilter.setSafeOnClickListener {
                startFilterActionDialog(if (filter?.isBlocker()
                        .isTrue()
                ) FilterAction.FILTER_ACTION_BLOCKER_TRANSFER else FilterAction.FILTER_ACTION_PERMISSION_TRANSFER)
            }
            filterDetailDeleteFilter.setSafeOnClickListener {
                startFilterActionDialog(if (filter?.isBlocker()
                        .isTrue()
                ) FilterAction.FILTER_ACTION_BLOCKER_DELETE else FilterAction.FILTER_ACTION_PERMISSION_DELETE)
            }
            filterDetailAddFilter.setSafeOnClickListener {
                startFilterActionDialog(if (filter?.isBlocker()
                        .isTrue()
                ) FilterAction.FILTER_ACTION_BLOCKER_ADD else FilterAction.FILTER_ACTION_PERMISSION_ADD)
            }
            filterDetailItemFilter.itemFilterDetailContainer.setSafeOnClickListener {
                isFilteredCallDetails = isFilteredCallDetails.not()
                numberDataAdapter?.numberDataList =
                    if (isFilteredCallDetails) filteredCallList else numberDataList
                numberDataAdapter?.isFilteredCallDetails = isFilteredCallDetails
                filterDetailItemFilter.isFilteredCallDetails = isFilteredCallDetails.not()
                filterDetailContactListEmpty.root.isVisible =
                    numberDataAdapter?.numberDataList.isNullOrEmpty()
                numberDataAdapter?.notifyDataSetChanged()
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
            numberDataAdapter ?: NumberDataAdapter(arrayListOf()) { numberData ->
                findNavController().navigate(FilterDetailFragmentDirections.startNumberDataDetailFragment(
                    numberData))
            }
        binding?.filterDetailContactList?.adapter = numberDataAdapter
    }

    override fun getData() {
        binding?.filter?.let {
            viewModel.getQueryContactCallList(it)
            viewModel.filteredCallsByFilter(it.filter)
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            numberDataListLiveData.safeSingleObserve(viewLifecycleOwner) { numberDataList ->
                binding?.filterDetailContactListEmpty?.root?.isVisible =
                    numberDataList.isEmpty().isTrue()
                if (binding?.filter?.isPreview.isTrue()) {
                    binding?.filter = binding?.filter?.apply {
                        filteredContacts = numberDataList.size.toString()
                    }
                    this@FilterDetailFragment.numberDataList = numberDataList.onEach {
                        it.searchText = binding?.filter?.filter.orEmpty()
                        when (it) {
                            is Contact -> it.filter = binding?.filter?.apply {
                                filter = addFilter()
                            }
                            is Call -> it.filter = binding?.filter?.apply {
                                filter = addFilter()
                            }
                        }
                    }
                } else {
                    this@FilterDetailFragment.numberDataList = numberDataList
                }
                numberDataAdapter?.numberDataList = numberDataList
                numberDataAdapter?.notifyDataSetChanged()
            }
            filteredCallListLiveData.safeSingleObserve(viewLifecycleOwner) { filteredCallList ->
                binding?.filter?.filteredCalls = filteredCallList.size.toString()
                this@FilterDetailFragment.filteredCallList = filteredCallList
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
                findNavController().navigate(if (binding?.filter?.isBlocker()
                        .isTrue()
                ) FilterDetailFragmentDirections.startBlockerListFragment()
                else FilterDetailFragmentDirections.startPermissionListFragment())
            }
        }
    }
}