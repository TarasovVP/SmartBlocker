package com.tarasovvp.blacklister.ui.number_data.filter_detail

import android.util.Log
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.BLACK_FILTER
import com.tarasovvp.blacklister.constants.Constants.WHITE_FILTER
import com.tarasovvp.blacklister.databinding.FragmentFilterDetailBinding
import com.tarasovvp.blacklister.enums.EmptyState
import com.tarasovvp.blacklister.enums.FilterAction
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseDetailFragment
import com.tarasovvp.blacklister.ui.number_data.NumberData
import com.tarasovvp.blacklister.ui.number_data.NumberDataAdapter
import com.tarasovvp.blacklister.ui.number_data.filter_add.FilterAddFragmentDirections
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

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
                        filterAction = FilterAction.FILTER_ACTION_ADD
                    }
                filterDetailChangeFilter.filter =
                    Filter(filterType = if (filter.isBlackFilter()) WHITE_FILTER else BLACK_FILTER).apply {
                        filterAction = FilterAction.FILTER_ACTION_CHANGE
                    }
                filterDetailDeleteFilter.filter =
                    Filter(filterType = filter.filterType).apply {
                        filterAction = FilterAction.FILTER_ACTION_DELETE
                    }
                filterDetailContactListDescription.text =
                    if (filter.isBlackFilter()) getString(R.string.contact_list_with_blocker) else getString(
                        R.string.contact_list_with_allow)

            }
            filterDetailContactListEmpty.emptyState = if (filter?.isBlackFilter()
                    .isTrue()
            ) EmptyState.EMPTY_STATE_FILTERS_CONTACTS_BY_BLOCKER else EmptyState.EMPTY_STATE_FILTERS_CONTACTS_BY_PERMISSION
            filterDetailContactListEmpty.root.isVisible = contactList.isNullOrEmpty()
            executePendingBindings()
        }
        setFragmentResultListeners()
    }

    private fun setFragmentResultListeners() {
        setFragmentResultListener(FilterAction.FILTER_ACTION_CHANGE.name) { _, _ ->
            binding?.filter?.let {
                viewModel.updateFilter(it.apply {
                    filterType = if (this.isBlackFilter()) WHITE_FILTER else BLACK_FILTER
                })
            }
        }
        setFragmentResultListener(FilterAction.FILTER_ACTION_DELETE.name) { _, _ ->
            binding?.filter?.let {
                viewModel.deleteFilter(it)
            }
        }
        setFragmentResultListener(FilterAction.FILTER_ACTION_ADD.name) { _, _ ->
            binding?.filter?.let {
                viewModel.insertFilter(it)
            }
        }
    }

    override fun setClickListeners() {
        binding?.apply {
            filterDetailChangeFilter.root.setSafeOnClickListener {
                findNavController().navigate(FilterDetailFragmentDirections.startFilterActionDialog(
                    filter = filter,
                    filterAction = FilterAction.FILTER_ACTION_CHANGE.name))
            }
            filterDetailDeleteFilter.root.setSafeOnClickListener {
                findNavController().navigate(FilterDetailFragmentDirections.startFilterActionDialog(
                    filter = filter,
                    filterAction = FilterAction.FILTER_ACTION_DELETE.name))
            }
            filterDetailAddFilter.root.setSafeOnClickListener {
                findNavController().navigate(FilterAddFragmentDirections.startFilterActionDialog(
                    filter = filter?.apply { filter = addFilter() },
                    filterAction = FilterAction.FILTER_ACTION_ADD.name))
            }
            filterDetailItemFilter.itemFilterDetailPreview.setSafeOnClickListener {
                findNavController().navigate(FilterDetailFragmentDirections.startBlockerCallsDetailFragment(
                    filter = filter))
            }
        }
    }

    override fun createAdapter() {
        numberDataAdapter =
            numberDataAdapter ?: NumberDataAdapter(contactList) { numberData ->
                findNavController().navigate(FilterDetailFragmentDirections.startNumberDataDetailFragment(
                    numberData))
            }
        binding?.filter?.let { numberDataAdapter?.addingFilter = it }
        binding?.filterDetailContactList?.adapter = numberDataAdapter
    }

    override fun getData() {
        binding?.filter?.let { viewModel.getQueryContactCallList(it) }
    }

    override fun observeLiveData() {
        with(viewModel) {
            contactCallListLiveData.safeSingleObserve(viewLifecycleOwner) { contactList ->
                binding?.filterDetailContactListEmpty?.root?.isVisible =
                    contactList.isEmpty().isTrue()
                binding?.filterDetailContactListDescription?.isVisible =
                    contactList.isNotEmpty().isTrue()
                if (this@FilterDetailFragment.contactList == contactList) return@safeSingleObserve
                this@FilterDetailFragment.contactList = contactList
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
            showMessage(String.format(getString(filter.filterActionSuccessText()),
                binding?.filter?.filter.orEmpty()), false)
            getAllData()
            if (filter.filterAction == FilterAction.FILTER_ACTION_CHANGE) {
                mainViewModel.successAllDataLiveData.safeSingleObserve(viewLifecycleOwner) {
                    initViews()
                    viewModel.getQueryContactCallList(filter)
                }
            } else {
                findNavController().navigate(if (binding?.filter?.isBlackFilter().isTrue())
                    FilterDetailFragmentDirections.startBlackFilterListFragment()
                else FilterDetailFragmentDirections.startWhiteFilterListFragment())
            }
        }
    }
}