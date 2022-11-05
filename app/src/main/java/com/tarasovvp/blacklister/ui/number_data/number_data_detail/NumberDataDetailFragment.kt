package com.tarasovvp.blacklister.ui.number_data.number_data_detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.databinding.FragmentNumberDataDetailBinding
import com.tarasovvp.blacklister.enums.FilterCondition
import com.tarasovvp.blacklister.enums.EmptyState
import com.tarasovvp.blacklister.extensions.*
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.ui.number_data.NumberData
import com.tarasovvp.blacklister.ui.number_data.NumberDataAdapter
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class NumberDataDetailFragment :
    BaseFragment<FragmentNumberDataDetailBinding, NumberDataDetailViewModel>() {

    override var layoutId = R.layout.fragment_number_data_detail
    override val viewModelClass = NumberDataDetailViewModel::class.java
    private val args: NumberDataDetailFragmentArgs by navArgs()

    private var contactFilterAdapter: NumberDataAdapter? = null
    private var filterList: ArrayList<NumberData>? = null
    private var filter: Filter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(args.contact)
        setContactAdapter()
        setClickListeners()
        viewModel.filterListWithNumber(binding?.contact?.trimmedPhone.orEmpty())
    }

    private fun initViews(contact: Contact?) {
        binding?.contact = contact
        binding?.numberDataDetailFilterListEmpty?.emptyState = EmptyState.EMPTY_STATE_FILTERS_BY_CONTACT
        binding?.executePendingBindings()
        binding?.numberDataDetailItemContact?.itemContactCallList?.isVisible = true
    }

    private fun setContactAdapter() {
        contactFilterAdapter =
            contactFilterAdapter ?: NumberDataAdapter(filterList) { filter ->
                findNavController().navigate(NumberDataDetailFragmentDirections.startFilterDetailFragment(
                    filter as Filter))
            }
        binding?.numberDataDetailFilterList?.adapter = contactFilterAdapter
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setClickListeners() {
        binding?.numberDataDetailItemContact?.itemContactCallList?.setSafeOnClickListener {
            findNavController().navigate(NumberDataDetailFragmentDirections.startCallDetailFragment(
                contact = binding?.contact))
        }
        binding?.numberDataDetailAddFilter?.setSafeOnClickListener {
            setAddFilterConditions(binding?.numberDataDetailAddFilterFull?.isShown.isTrue())
        }
        binding?.numberDataDetailAddFilterFull?.setSafeOnClickListener {
            createFilter(FilterCondition.FILTER_CONDITION_FULL.index)
        }
        binding?.numberDataDetailAddFilterStart?.setSafeOnClickListener {
            createFilter(FilterCondition.FILTER_CONDITION_START.index)
        }
        binding?.numberDataDetailAddFilterContain?.setSafeOnClickListener {
            createFilter(FilterCondition.FILTER_CONDITION_CONTAIN.index)
        }
        binding?.numberDataDetailItemContact?.root?.setSafeOnClickListener {
            if (binding?.numberDataDetailAddFilterFull?.isShown.isTrue()) {
                setAddFilterConditions(true)
            }
        }
        binding?.numberDataDetailFilterListDescription?.setSafeOnClickListener {
            if (binding?.numberDataDetailAddFilterFull?.isShown.isTrue()) {
                setAddFilterConditions(true)
            }
        }
        binding?.numberDataDetailFilterList?.setOnTouchListener { v, event ->
            if (binding?.numberDataDetailAddFilterFull?.isShown.isTrue()) {
                setAddFilterConditions(true)
            }
            v?.onTouchEvent(event) ?: true
        }
    }

    private fun createFilter(conditionIndex: Int) {
            filter = Filter(filter = binding?.contact?.trimmedPhone.orEmpty(),
                conditionType = conditionIndex,
                filterType = Constants.BLACK_FILTER)
        val phoneNumber = if (binding?.contact?.trimmedPhone.orEmpty().getPhoneNumber(String.EMPTY).isNull()) binding?.contact?.trimmedPhone.orEmpty().getPhoneNumber(context?.getUserCountry().orEmpty().uppercase()) else binding?.contact?.trimmedPhone.orEmpty().getPhoneNumber(String.EMPTY)
        if (phoneNumber.isNull() || conditionIndex == FilterCondition.FILTER_CONDITION_CONTAIN.index) {
            startAddFilterScreen()
        } else {
            viewModel.getCountryCode(phoneNumber?.countryCode)
        }
    }

    private fun startAddFilterScreen() {
        findNavController().navigate(NumberDataDetailFragmentDirections.startFilterAddFragment(
            filter = filter))
    }

    private fun setAddFilterConditions(isShown: Boolean) {
        binding?.apply {
            numberDataDetailAddFilter.text =
                getString(if (isShown) R.string.add else R.string.close)
            if (isShown) numberDataDetailAddFilterFull.hide() else numberDataDetailAddFilterFull.show()
            if (isShown) numberDataDetailAddFilterStart.hide() else numberDataDetailAddFilterStart.show()
            if (isShown) numberDataDetailAddFilterContain.hide() else numberDataDetailAddFilterContain.show()
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            filterListLiveData.safeSingleObserve(viewLifecycleOwner) { filterList ->
                binding?.numberDataDetailFilterListEmpty?.root?.isVisible = filterList.isEmpty().isTrue()
                binding?.numberDataDetailFilterListDescription?.isVisible = filterList.isNotEmpty().isTrue()
                if (this@NumberDataDetailFragment.filterList == filterList) return@safeSingleObserve
                this@NumberDataDetailFragment.filterList = filterList
                contactFilterAdapter?.numberDataList = filterList
                contactFilterAdapter?.notifyDataSetChanged()
            }
            countryCodeLiveData.safeSingleObserve(viewLifecycleOwner) { countryCode ->
                filter?.countryCode = countryCode
                filter?.filter = filter?.filterToInput().orEmpty()
                startAddFilterScreen()
            }
        }

    }

}