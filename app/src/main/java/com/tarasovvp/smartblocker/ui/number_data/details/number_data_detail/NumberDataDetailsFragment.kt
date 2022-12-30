package com.tarasovvp.smartblocker.ui.number_data.details.number_data_detail

import android.annotation.SuppressLint
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants
import com.tarasovvp.smartblocker.databinding.FragmentNumberDataDetailBinding
import com.tarasovvp.smartblocker.enums.FilterCondition
import com.tarasovvp.smartblocker.enums.Info
import com.tarasovvp.smartblocker.extensions.*
import com.tarasovvp.smartblocker.models.*
import com.tarasovvp.smartblocker.ui.base.BaseDetailsFragment
import com.tarasovvp.smartblocker.ui.base.BaseNumberDataFragment
import com.tarasovvp.smartblocker.ui.number_data.details.DetailsPagerAdapter
import com.tarasovvp.smartblocker.ui.number_data.details.SingleDetailsFragment
import com.tarasovvp.smartblocker.ui.number_data.list.call_list.CallListFragmentDirections
import com.tarasovvp.smartblocker.utils.setSafeOnClickListener

class NumberDataDetailsFragment :
    BaseDetailsFragment<FragmentNumberDataDetailBinding, NumberDataDetailsViewModel>() {

    override var layoutId = R.layout.fragment_number_data_detail
    override val viewModelClass = NumberDataDetailsViewModel::class.java
    private val args: NumberDataDetailsFragmentArgs by navArgs()
    override fun setFragmentResultListeners() = Unit

    private var filtersScreen: SingleDetailsFragment? = null
    private var filteredCallsScreen: SingleDetailsFragment? = null
    private var filter: Filter? = null

    override fun initViews() {
        binding?.apply {
            contact = Contact()
            call = Call()
            when (args.numberData) {
                is Contact -> contact = args.numberData as Contact
                is LogCall,
                is FilteredCall,
                -> call = (args.numberData as Call).apply { isExtract = true }
            }
            executePendingBindings()
        }
    }

    override fun createAdapter() {
        filtersScreen = SingleDetailsFragment {
            findNavController().navigate(NumberDataDetailsFragmentDirections.startFilterDetailFragment(
                filterDetail = it as Filter))
        }
        filteredCallsScreen = SingleDetailsFragment(true) {
            findNavController().navigate(NumberDataDetailsFragmentDirections.startFilterDetailFragment(
                filterDetail = it as Filter))
        }
        val fragmentList = arrayListOf(
            filtersScreen,
            filteredCallsScreen
        )

        binding?.numberDataDetailViewPager?.adapter =
            activity?.supportFragmentManager?.let { fragmentManager ->
                DetailsPagerAdapter(
                    fragmentList,
                    fragmentManager,
                    lifecycle
                )
            }
        binding?.numberDataDetailViewPager?.offscreenPageLimit = 2
        binding?.numberDataDetailViewPager?.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding?.numberDataDetailTabs?.setImageResource(if (position == 0) R.drawable.ic_filter_details_tab_1 else R.drawable.ic_filter_details_tab_2)
            }
        })
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun setClickListeners() {
        binding?.apply {
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
        val number = when (args.numberData) {
            is Contact -> binding?.contact?.trimmedPhone.orEmpty()
            is LogCall,
            is FilteredCall,
            -> binding?.call?.number.orEmpty()
            else -> String.EMPTY
        }
        viewModel.filterListWithNumber(number)
        viewModel.filteredCallsByNumber(number)
    }

    private fun getNumber(): String {
        return if (binding?.contact?.numberData.isNullOrEmpty()) binding?.call?.number.orEmpty() else binding?.contact?.trimmedPhone.orEmpty()
    }

    private fun createFilter(conditionIndex: Int) {
        val number = getNumber()
        filter = Filter(filter = number,
            conditionType = conditionIndex,
            filterType = Constants.BLOCKER)
        val phoneNumber = if (number.getPhoneNumber(String.EMPTY)
                .isNull()
        ) number.getPhoneNumber(context?.getUserCountry().orEmpty()
            .uppercase()) else number.getPhoneNumber(String.EMPTY)
        if (phoneNumber.isNull() || conditionIndex == FilterCondition.FILTER_CONDITION_CONTAIN.index) {
            startAddFilterScreen()
        } else {
            viewModel.getCountryCode(phoneNumber?.countryCode)
        }
    }

    private fun startAddFilterScreen() {
        findNavController().navigate(NumberDataDetailsFragmentDirections.startFilterAddFragment(
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
                filtersScreen?.updateNumberDataList(filterList)
            }
            filteredCallListLiveData.safeSingleObserve(viewLifecycleOwner) { filteredCallList ->
                filteredCallsScreen?.updateNumberDataList(filteredCallList)
            }
            countryCodeLiveData.safeSingleObserve(viewLifecycleOwner) { countryCode ->
                filter?.countryCode = countryCode
                filter?.filter = filter?.filterToInput().orEmpty()
                startAddFilterScreen()
            }
        }
    }

    override fun showInfoScreen() {
        findNavController().navigate(NumberDataDetailsFragmentDirections.startInfoFragment(info = InfoData(
            title = getString(Info.INFO_NUMBER_DATA_DETAIL.title),
            description = getString(Info.INFO_NUMBER_DATA_DETAIL.description))))
    }
}