package com.tarasovvp.smartblocker.ui.number_data.filtered_calls_detail

import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentBlockerCallsDetailBinding
import com.tarasovvp.smartblocker.enums.EmptyState
import com.tarasovvp.smartblocker.extensions.safeSingleObserve
import com.tarasovvp.smartblocker.ui.base.BaseDetailFragment
import com.tarasovvp.smartblocker.models.NumberData
import com.tarasovvp.smartblocker.ui.number_data.NumberDataAdapter

class FilteredCallsDetailFragment :
    BaseDetailFragment<FragmentBlockerCallsDetailBinding, FilteredCallsDetailViewModel>() {

    override var layoutId = R.layout.fragment_blocker_calls_detail
    override val viewModelClass = FilteredCallsDetailViewModel::class.java

    private val args: FilteredCallsDetailFragmentArgs by navArgs()

    private var filteredCallAdapter: NumberDataAdapter? = null
    private var filteredCallList: ArrayList<NumberData> = ArrayList()

    override fun setClickListeners() = Unit

    override fun initViews() {
        binding?.apply {
            filter = args.filter
            filteredCallsDetailEmpty.emptyState = EmptyState.EMPTY_STATE_BLOCKED_CALLS
            executePendingBindings()
        }
    }

    override fun createAdapter() {
        filteredCallAdapter =
            filteredCallAdapter ?: NumberDataAdapter(filteredCallList) { numberData ->
                findNavController().navigate(FilteredCallsDetailFragmentDirections.startNumberDataDetailFragment(numberData))
            }
        filteredCallAdapter?.isFilteredCallDetails = true
        binding?.filteredCallsDetailList?.adapter = filteredCallAdapter
    }

    override fun observeLiveData() {
        viewModel.callListLiveData.safeSingleObserve(viewLifecycleOwner) { callList ->
            binding?.filteredCallsDetailEmpty?.emptyStateContainer?.isVisible = callList.isEmpty()
            binding?.filteredCallsDetailDescription?.isVisible = callList.isNotEmpty()
            if (this.filteredCallList == callList) return@safeSingleObserve
            this.filteredCallList = callList
            filteredCallAdapter?.numberDataList = callList
            filteredCallAdapter?.notifyDataSetChanged()
        }
    }

    override fun getData() {
        binding?.filter?.let { viewModel.filteredCallsByFilter(it.filter) }
    }
}