package com.tarasovvp.blacklister.ui.number_data.number_data_detail

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.databinding.FragmentNumberDetailBinding
import com.tarasovvp.blacklister.enums.Condition
import com.tarasovvp.blacklister.extensions.isNull
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.ui.number_data.NumberData
import com.tarasovvp.blacklister.ui.number_data.NumberDataAdapter
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class NumberDataDetailFragment :
    BaseFragment<FragmentNumberDetailBinding, NumberDataDetailViewModel>() {

    override var layoutId = R.layout.fragment_number_detail
    override val viewModelClass = NumberDataDetailViewModel::class.java
    private val args: NumberDataDetailFragmentArgs by navArgs()

    private var contactFilterAdapter: NumberDataAdapter? = null
    private var filterList: ArrayList<NumberData>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(args.contact)
        setContactAdapter()
        setClickListeners()
        if (contactFilterAdapter?.numberDataList.isNull()) {
            viewModel.filterListWithNumber(binding?.contact?.trimmedPhone.orEmpty())
        } else {
            checkFilterListEmptiness()
        }
    }

    private fun initViews(contact: Contact?) {
        binding?.contact = contact
        binding?.contactDetailItemContact?.itemContactCallList?.isVisible = true
    }

    private fun setContactAdapter() {
        contactFilterAdapter =
            contactFilterAdapter ?: NumberDataAdapter(filterList) { filter ->
                findNavController().navigate(NumberDataDetailFragmentDirections.startFilterDetailFragment(
                    filter as Filter))
            }
        binding?.contactDetailFilterList?.adapter = contactFilterAdapter
    }

    private fun setClickListeners() {
        binding?.contactDetailAddFilter?.setSafeOnClickListener {
            findNavController().navigate(NumberDataDetailFragmentDirections.startFilterAddFragment(
                filter = Filter(filter = binding?.contact?.trimmedPhone.orEmpty(),
                    conditionType = Condition.CONDITION_TYPE_FULL.index,
                    filterType = Constants.BLACK_FILTER)))
        }
        binding?.contactDetailItemContact?.itemContactCallList?.setSafeOnClickListener {
            findNavController().navigate(NumberDataDetailFragmentDirections.startCallDetailFragment(
                contact = binding?.contact))
        }
    }

    private fun checkFilterListEmptiness() {
        binding?.contactDetailFilterListEmpty?.emptyStateTitle?.text =
            getString(R.string.filter_by_contact_empty_state)
        binding?.contactDetailFilterListDescription?.isVisible = filterList?.isNotEmpty().isTrue()
        binding?.contactDetailFilterListEmpty?.emptyStateContainer?.isVisible =
            filterList?.isEmpty().isTrue()
    }

    override fun observeLiveData() {
        viewModel.filterListLiveData.safeSingleObserve(viewLifecycleOwner) { filterList ->
            this.filterList = filterList
            checkFilterListEmptiness()
            contactFilterAdapter?.numberDataList = filterList
            contactFilterAdapter?.notifyDataSetChanged()
        }
    }

}