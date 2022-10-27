package com.tarasovvp.blacklister.ui.number_data.filter_detail

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.BLACK_FILTER
import com.tarasovvp.blacklister.constants.Constants.WHITE_FILTER
import com.tarasovvp.blacklister.databinding.FragmentFilterDetailBinding
import com.tarasovvp.blacklister.enums.FilterAction
import com.tarasovvp.blacklister.extensions.isNull
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.ui.number_data.NumberData
import com.tarasovvp.blacklister.ui.number_data.NumberDataAdapter
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class FilterDetailFragment : BaseFragment<FragmentFilterDetailBinding, FilterDetailViewModel>() {

    override var layoutId = R.layout.fragment_filter_detail
    override val viewModelClass = FilterDetailViewModel::class.java
    private val args: FilterDetailFragmentArgs by navArgs()

    private var numberDataAdapter: NumberDataAdapter? = null
    private var contactFilterList: ArrayList<NumberData>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args.filter?.apply {
            (activity as MainActivity).toolbar?.title = getString(filterTypeTitle())
            binding?.filter = this
            binding?.filterDetailChangeFilter?.filter = Filter(filterType = if (this.isBlackFilter()) WHITE_FILTER else BLACK_FILTER).apply { filterAction = FilterAction.FILTER_ACTION_CHANGE }
            binding?.filterDetailDeleteFilter?.filter = Filter(filterType = this.filterType).apply { filterAction = FilterAction.FILTER_ACTION_DELETE }
            binding?.filterDetailContactListDescription?.text =
                if (isBlackFilter().isTrue()) getString(
                    R.string.contact_list_with_blocker) else getString(R.string.contact_list_with_allow)
            if (numberDataAdapter?.numberDataList.isNull()) {
                viewModel.getQueryContactList(this)
            }
        }
        setContactAdapter()
        setClickListeners()
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
    }

    private fun setClickListeners() {
        binding?.filterDetailChangeFilter?.root?.setSafeOnClickListener {
            findNavController().navigate(FilterDetailFragmentDirections.startFilterActionDialog(
                filter = binding?.filter,
                filterAction = FilterAction.FILTER_ACTION_CHANGE.name))
        }
        binding?.filterDetailDeleteFilter?.root?.setSafeOnClickListener {
            findNavController().navigate(FilterDetailFragmentDirections.startFilterActionDialog(
                filter = binding?.filter,
                filterAction = FilterAction.FILTER_ACTION_DELETE.name))
        }
    }

    private fun setContactAdapter() {
        numberDataAdapter =
            numberDataAdapter ?: NumberDataAdapter(contactFilterList) { contact ->
                findNavController().navigate(FilterDetailFragmentDirections.startNumberDataDetailFragment(
                    contact as Contact))
            }
        binding?.filterDetailContactList?.adapter = numberDataAdapter
    }

    override fun observeLiveData() {
        with(viewModel) {
            contactListLiveData.safeSingleObserve(viewLifecycleOwner) { filterList ->
                binding?.filterDetailContactListEmpty?.emptyStateTitle?.text =
                    if (binding?.filter?.isBlackFilter().isTrue()) getString(
                        R.string.contact_by_blocker_empty_state) else getString(R.string.contact_by_allowing_empty_state)
                binding?.filterDetailContactListDescription?.isVisible = filterList.isNotEmpty()
                binding?.filterDetailContactListEmpty?.emptyStateContainer?.isVisible =
                    filterList.isEmpty()
                numberDataAdapter?.numberDataList = filterList
                numberDataAdapter?.notifyDataSetChanged()
            }
            filterActionLiveData.safeSingleObserve(viewLifecycleOwner) { filter ->
                (activity as MainActivity).apply {
                    showMessage(String.format(getString(filter.filterActionDescription()), binding?.filter?.filter.orEmpty()), false)
                    getAllData()
                    if (filter.filterAction == FilterAction.FILTER_ACTION_DELETE) {
                        findNavController().navigate(if (binding?.filter?.isBlackFilter().isTrue())
                            FilterDetailFragmentDirections.startBlackFilterListFragment()
                        else FilterDetailFragmentDirections.startWhiteFilterListFragment())
                    } else {
                        mainViewModel.successAllDataLiveData.safeSingleObserve(viewLifecycleOwner) {
                            binding?.filter = filter
                            viewModel.getQueryContactList(filter)
                        }
                    }
                }
            }
        }
    }
}