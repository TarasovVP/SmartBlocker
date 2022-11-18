package com.tarasovvp.blacklister.ui.number_data.blocker_calls_detail

import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.FragmentBlockerCallsDetailBinding
import com.tarasovvp.blacklister.enums.EmptyState
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.ui.base.BaseDetailFragment
import com.tarasovvp.blacklister.model.NumberData
import com.tarasovvp.blacklister.ui.number_data.NumberDataAdapter

class BlockerCallsDetailFragment :
    BaseDetailFragment<FragmentBlockerCallsDetailBinding, BlockerCallsDetailViewModel>() {

    override var layoutId = R.layout.fragment_blocker_calls_detail
    override val viewModelClass = BlockerCallsDetailViewModel::class.java

    private val args: BlockerCallsDetailFragmentArgs by navArgs()

    private var blockerCallAdapter: NumberDataAdapter? = null
    private var blockerCallList: ArrayList<NumberData> = ArrayList()

    override fun setClickListeners() = Unit

    override fun initViews() {
        binding?.apply {
            filter = args.filter
            blockerCallsDetailEmpty.emptyState = EmptyState.EMPTY_STATE_BLOCKED_CALLS
            executePendingBindings()
            blockerCallsDetailFilter.itemFilterDetailPreview.isVisible = false
        }
    }

    override fun createAdapter() {
        blockerCallAdapter =
            blockerCallAdapter ?: NumberDataAdapter(blockerCallList) { numberData ->
                findNavController().navigate(BlockerCallsDetailFragmentDirections.startNumberDataDetailFragment(
                    numberData))
            }
        binding?.blockerCallsDetailList?.adapter = blockerCallAdapter
    }

    override fun observeLiveData() {
        viewModel.callListLiveData.safeSingleObserve(viewLifecycleOwner) { callList ->
            binding?.blockerCallsDetailEmpty?.emptyStateContainer?.isVisible = callList.isEmpty()
            binding?.blockerCallsDetailDescription?.isVisible = callList.isNotEmpty()
            if (this.blockerCallList == callList) return@safeSingleObserve
            this.blockerCallList = callList
            blockerCallAdapter?.numberDataList = callList
            blockerCallAdapter?.notifyDataSetChanged()
        }
    }

    override fun getData() {
        binding?.filter?.let { viewModel.blockedCallsByFilter(it) }
    }
}