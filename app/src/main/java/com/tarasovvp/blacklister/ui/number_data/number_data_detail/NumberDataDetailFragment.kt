package com.tarasovvp.blacklister.ui.number_data.number_data_detail

import android.annotation.SuppressLint
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.databinding.FragmentNumberDataDetailBinding
import com.tarasovvp.blacklister.enums.EmptyState
import com.tarasovvp.blacklister.enums.FilterCondition
import com.tarasovvp.blacklister.extensions.*
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.model.LogCall
import com.tarasovvp.blacklister.ui.base.BaseDetailFragment
import com.tarasovvp.blacklister.ui.number_data.NumberData
import com.tarasovvp.blacklister.ui.number_data.NumberDataAdapter
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class NumberDataDetailFragment :
    BaseDetailFragment<FragmentNumberDataDetailBinding, NumberDataDetailViewModel>() {

    override var layoutId = R.layout.fragment_number_data_detail
    override val viewModelClass = NumberDataDetailViewModel::class.java
    private val args: NumberDataDetailFragmentArgs by navArgs()

    private var contactFilterAdapter: NumberDataAdapter? = null
    private var filterList: ArrayList<NumberData>? = null
    private var filter: Filter? = null

    override fun initViews() {
        binding?.apply {
            when (args.numberData) {
                is Contact -> contact = args.numberData as Contact
                is LogCall -> call = args.numberData as LogCall
            }
            numberDataDetailFilterListEmpty.emptyState =
                EmptyState.EMPTY_STATE_FILTERS_BY_CONTACT
            numberDataDetailItemContact.itemContactCallList.isVisible = true
            executePendingBindings()
        }
    }

    override fun createAdapter() {
        contactFilterAdapter =
            contactFilterAdapter ?: NumberDataAdapter(filterList) { filter ->
                findNavController().navigate(NumberDataDetailFragmentDirections.startFilterDetailFragment(
                    filter as Filter))
            }
        binding?.numberDataDetailFilterList?.adapter = contactFilterAdapter
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setClickListeners() {
        binding?.apply {
            numberDataDetailItemContact.itemContactCallList.setSafeOnClickListener {
                findNavController().navigate(NumberDataDetailFragmentDirections.startCallDetailFragment(
                    contact = binding?.contact))
            }
            numberDataDetailAddFilter.setSafeOnClickListener {
                setAddFilterConditions(numberDataDetailAddFilterFull.isShown.isTrue())
            }
            numberDataDetailAddFilterFull.setSafeOnClickListener {
                createFilter(FilterCondition.FILTER_CONDITION_FULL.index)
            }
            numberDataDetailAddFilterStart.setSafeOnClickListener {
                createFilter(FilterCondition.FILTER_CONDITION_START.index)
            }
            numberDataDetailAddFilterContain.setSafeOnClickListener {
                createFilter(FilterCondition.FILTER_CONDITION_CONTAIN.index)
            }
            numberDataDetailItemContact.root.setSafeOnClickListener {
                if (numberDataDetailAddFilterFull.isShown.isTrue()) {
                    setAddFilterConditions(true)
                }
            }
            numberDataDetailFilterListDescription.setSafeOnClickListener {
                if (numberDataDetailAddFilterFull.isShown.isTrue()) {
                    setAddFilterConditions(true)
                }
            }
            numberDataDetailFilterList.setOnTouchListener { v, event ->
                if (numberDataDetailAddFilterFull.isShown.isTrue()) {
                    setAddFilterConditions(true)
                }
                v?.onTouchEvent(event) ?: true
            }
        }
    }

    override fun getData() {
        viewModel.filterListWithNumber(binding?.contact?.trimmedPhone.orEmpty())
    }

    private fun createFilter(conditionIndex: Int) {
        filter = Filter(filter = binding?.contact?.trimmedPhone.orEmpty(),
            conditionType = conditionIndex,
            filterType = Constants.BLACK_FILTER)
        val phoneNumber = if (binding?.contact?.trimmedPhone.orEmpty().getPhoneNumber(String.EMPTY)
                .isNull()
        ) binding?.contact?.trimmedPhone.orEmpty()
            .getPhoneNumber(context?.getUserCountry().orEmpty()
                .uppercase()) else binding?.contact?.trimmedPhone.orEmpty()
            .getPhoneNumber(String.EMPTY)
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
                binding?.numberDataDetailFilterListEmpty?.root?.isVisible =
                    filterList.isEmpty().isTrue()
                binding?.numberDataDetailFilterListDescription?.isVisible =
                    filterList.isNotEmpty().isTrue()
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