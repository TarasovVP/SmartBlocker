package com.tarasovvp.blacklister.ui.number_data.call_detail

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.FragmentCallDetailBinding
import com.tarasovvp.blacklister.extensions.isNull
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.model.Call
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.ui.number_data.NumberData
import com.tarasovvp.blacklister.ui.number_data.NumberDataAdapter

class CallDetailFragment : BaseFragment<FragmentCallDetailBinding, CallDetailViewModel>() {

    override var layoutId = R.layout.fragment_call_detail
    override val viewModelClass = CallDetailViewModel::class.java

    private val args: CallDetailFragmentArgs by navArgs()

    private var numberDataAdapter: NumberDataAdapter? = null
    private var numberDataList: ArrayList<NumberData> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setContactAdapter()
    }

    private fun initViews() {
        args.contact?.apply {
            binding?.contact = this
            binding?.executePendingBindings()
            if (numberDataAdapter?.numberDataList.isNull()) {
                viewModel.blockedCallsByNumber(this.trimmedPhone)
            }
        }
        args.filter?.apply {
            binding?.filter = this
            if (numberDataAdapter?.numberDataList.isNull()) {
                viewModel.blockedCallsByFilter(this.filter)
            }
        }
    }

    private fun setContactAdapter() {
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

    private fun checkFilterListEmptiness() {
        binding?.callDetailFilterListEmpty?.emptyStateTitle?.text =
            getString(R.string.filter_by_call_empty_state)
        binding?.callDetailFilterListDescription?.isVisible = numberDataList.isNotEmpty()
        binding?.callDetailFilterListEmpty?.emptyStateContainer?.isVisible =
            numberDataList.isEmpty()
    }

    override fun observeLiveData() {
        viewModel.callListLiveData.safeSingleObserve(viewLifecycleOwner) { callList ->
            this.numberDataList = callList
            checkFilterListEmptiness()
            numberDataAdapter?.numberDataList = callList
            numberDataAdapter?.notifyDataSetChanged()
        }
    }
}