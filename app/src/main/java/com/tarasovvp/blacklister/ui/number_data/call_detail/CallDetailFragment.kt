package com.tarasovvp.blacklister.ui.number_data.call_detail

import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.FragmentCallDetailBinding
import com.tarasovvp.blacklister.enums.EmptyState
import com.tarasovvp.blacklister.extensions.isNotNull
import com.tarasovvp.blacklister.extensions.isNull
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.model.Call
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.ui.base.BaseDetailFragment
import com.tarasovvp.blacklister.ui.number_data.NumberData
import com.tarasovvp.blacklister.ui.number_data.NumberDataAdapter

class CallDetailFragment : BaseDetailFragment<FragmentCallDetailBinding, CallDetailViewModel>() {

    override var layoutId = R.layout.fragment_call_detail
    override val viewModelClass = CallDetailViewModel::class.java

    private val args: CallDetailFragmentArgs by navArgs()

    private var numberDataAdapter: NumberDataAdapter? = null
    private var numberDataList: ArrayList<NumberData> = ArrayList()

    override fun setClickListeners() = Unit

    override fun initViews() {
        binding?.apply {
            contact = args.contact
            filter = args.filter
            callDetailFilterListEmpty.emptyState = EmptyState.EMPTY_STATE_BLOCKED_CALLS
            executePendingBindings()
        }
    }

    override fun createAdapter() {
        numberDataAdapter =
            numberDataAdapter ?: NumberDataAdapter(numberDataList) { call ->
                val contact = if (binding?.contact.isNull()) Contact(name = (call as Call).name,
                    photoUrl = call.photoUrl,
                    number = call.number,
                    filterType = call.filterType) else binding?.contact
                findNavController().navigate(CallDetailFragmentDirections.startNumberDataDetailFragment(
                    contact))
            }
        binding?.callDetailFilterList?.adapter = numberDataAdapter
    }

    override fun observeLiveData() {
        viewModel.callListLiveData.safeSingleObserve(viewLifecycleOwner) { callList ->
            binding?.callDetailFilterListEmpty?.emptyStateContainer?.isVisible =
                numberDataList.isEmpty()
            binding?.callDetailFilterListDescription?.isVisible = numberDataList.isNotEmpty()
            if (this.numberDataList == callList) return@safeSingleObserve
            this.numberDataList = callList
            numberDataAdapter?.numberDataList = callList
            numberDataAdapter?.notifyDataSetChanged()
        }
    }

    override fun getData() {
        if (binding?.contact.isNotNull()) viewModel.blockedCallsByNumber(binding?.contact?.trimmedPhone.orEmpty())
        if (binding?.filter.isNotNull()) viewModel.blockedCallsByFilter(binding?.filter?.filter.orEmpty())
    }
}