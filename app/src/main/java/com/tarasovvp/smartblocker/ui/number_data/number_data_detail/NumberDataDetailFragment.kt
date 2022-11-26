package com.tarasovvp.smartblocker.ui.number_data.number_data_detail

import android.annotation.SuppressLint
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants
import com.tarasovvp.smartblocker.databinding.FragmentNumberDataDetailBinding
import com.tarasovvp.smartblocker.enums.EmptyState
import com.tarasovvp.smartblocker.enums.FilterCondition
import com.tarasovvp.smartblocker.extensions.*
import com.tarasovvp.smartblocker.models.*
import com.tarasovvp.smartblocker.ui.base.BaseDetailFragment
import com.tarasovvp.smartblocker.models.NumberData
import com.tarasovvp.smartblocker.ui.number_data.NumberDataAdapter
import com.tarasovvp.smartblocker.utils.setSafeOnClickListener

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
                is FilteredCall,
                is LogCall,
                -> call = (args.numberData as Call).apply { isExtract = true }
            }
            numberDataDetailFilterListEmpty.emptyState =
                EmptyState.EMPTY_STATE_FILTERS_BY_CONTACT
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
            numberDataDetailItemContact.root.setSafeOnClickListener {
                findNavController().navigate(NumberDataDetailFragmentDirections.startCallListFragment(
                    searchQuery = getNumber()))
            }
            numberDataDetailItemCall.root.setSafeOnClickListener {
                findNavController().navigate(NumberDataDetailFragmentDirections.startCallListFragment(
                    searchQuery = getNumber()))
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
        }
    }

    override fun getData() {
        viewModel.filterListWithNumber(when (args.numberData) {
            is Contact -> binding?.contact?.trimmedPhone.orEmpty()
            is LogCall -> binding?.call?.number.orEmpty()
            else -> String.EMPTY
        })
    }

    private fun getNumber(): String {
        return if (binding?.contact.isNull()) binding?.call?.number.orEmpty() else binding?.contact?.trimmedPhone.orEmpty()
    }

    private fun createFilter(conditionIndex: Int) {
        filter = Filter(filter = getNumber(),
            conditionType = conditionIndex,
            filterType = Constants.BLOCKER)
        val phoneNumber = if (getNumber().getPhoneNumber(String.EMPTY)
                .isNull()
        ) getNumber().getPhoneNumber(context?.getUserCountry().orEmpty()
            .uppercase()) else getNumber().getPhoneNumber(String.EMPTY)
        if (phoneNumber.isNull() || conditionIndex == FilterCondition.FILTER_CONDITION_CONTAIN.index) {
            startAddFilterScreen()
        } else {
            viewModel.getCountryCode(phoneNumber?.countryCode)
        }
    }

    private fun startAddFilterScreen() {
        findNavController().navigate(NumberDataDetailFragmentDirections.startFilterAddFragment(
            filterAdd = filter))
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