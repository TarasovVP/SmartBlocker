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
import com.tarasovvp.blacklister.extensions.orZero
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
    private var contactList: ArrayList<NumberData>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(args.filter)
        setContactAdapter()
        setClickListeners()
        setFragmentResultListeners()
        if (numberDataAdapter?.numberDataList.isNull()) {
            binding?.filter?.let { viewModel.getQueryContactList(it) }
        } else {
            checkContactListEmptiness()
        }
    }

    private fun initViews(filter: Filter?) {
        (activity as MainActivity).toolbar?.title = getString(filter?.filterTypeTitle().orZero())
        binding?.filter = filter
        binding?.filterDetailItemFilter?.itemFilterCallList?.isVisible = true
        binding?.filterDetailChangeFilter?.filter = Filter(filterType = if (filter?.isBlackFilter()
                .isTrue()
        ) WHITE_FILTER else BLACK_FILTER).apply { filterAction = FilterAction.FILTER_ACTION_CHANGE }
        binding?.filterDetailDeleteFilter?.filter =
            Filter(filterType = filter?.filterType.orZero()).apply {
                filterAction = FilterAction.FILTER_ACTION_DELETE
            }
        binding?.filterDetailContactListDescription?.text =
            if (filter?.isBlackFilter().isTrue()) getString(
                R.string.contact_list_with_blocker) else getString(R.string.contact_list_with_allow)
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
        binding?.filterDetailItemFilter?.itemFilterCallList?.setSafeOnClickListener {
            findNavController().navigate(FilterDetailFragmentDirections.startCallDetailFragment(
                filter = binding?.filter))
        }
    }

    private fun setContactAdapter() {
        numberDataAdapter =
            numberDataAdapter ?: NumberDataAdapter(contactList) { contact ->
                findNavController().navigate(FilterDetailFragmentDirections.startNumberDataDetailFragment(
                    contact as Contact))
            }
        binding?.filterDetailContactList?.adapter = numberDataAdapter
    }

    private fun checkContactListEmptiness() {
        binding?.filterDetailContactListEmpty?.emptyStateTitle?.text =
            if (binding?.filter?.isBlackFilter()
                    .isTrue()
            ) getString(R.string.contact_by_blocker_empty_state) else getString(R.string.contact_by_allowing_empty_state)
        binding?.filterDetailContactListDescription?.isVisible = contactList?.isNotEmpty().isTrue()
        binding?.filterDetailContactListEmpty?.emptyStateContainer?.isVisible = contactList?.isEmpty().isTrue()
    }
    override fun observeLiveData() {
        with(viewModel) {
            contactListLiveData.safeSingleObserve(viewLifecycleOwner) { contactList ->
                this@FilterDetailFragment.contactList = contactList
                checkContactListEmptiness()
                numberDataAdapter?.numberDataList = contactList
                numberDataAdapter?.notifyDataSetChanged()
            }
            filterActionLiveData.safeSingleObserve(viewLifecycleOwner) { filter ->
                (activity as MainActivity).apply {
                    showMessage(String.format(getString(filter.filterActionDescription()),
                        binding?.filter?.filter.orEmpty()), false)
                    getAllData()
                    if (filter.filterAction == FilterAction.FILTER_ACTION_DELETE) {
                        findNavController().navigate(if (binding?.filter?.isBlackFilter().isTrue())
                            FilterDetailFragmentDirections.startBlackFilterListFragment()
                        else FilterDetailFragmentDirections.startWhiteFilterListFragment())
                    } else {
                        mainViewModel.successAllDataLiveData.safeSingleObserve(viewLifecycleOwner) {
                            initViews(filter)
                            viewModel.getQueryContactList(filter)
                        }
                    }
                }
            }
        }
    }
}