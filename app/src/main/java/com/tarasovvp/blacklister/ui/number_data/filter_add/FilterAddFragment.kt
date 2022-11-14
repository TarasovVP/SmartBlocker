package com.tarasovvp.blacklister.ui.number_data.filter_add

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.BLACK_FILTER
import com.tarasovvp.blacklister.constants.Constants.CHANGE_FILTER
import com.tarasovvp.blacklister.constants.Constants.COUNTRY_CODE_START
import com.tarasovvp.blacklister.constants.Constants.PLUS_CHAR
import com.tarasovvp.blacklister.constants.Constants.WHITE_FILTER
import com.tarasovvp.blacklister.databinding.FragmentFilterAddBinding
import com.tarasovvp.blacklister.enums.EmptyState
import com.tarasovvp.blacklister.enums.FilterAction
import com.tarasovvp.blacklister.extensions.*
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.model.CountryCode
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.model.LogCall
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.ui.number_data.NumberData
import com.tarasovvp.blacklister.ui.number_data.NumberDataAdapter
import com.tarasovvp.blacklister.ui.number_data.filter_detail.FilterDetailFragmentDirections
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

open class FilterAddFragment :
    BaseFragment<FragmentFilterAddBinding, FilterAddViewModel>() {

    override var layoutId = R.layout.fragment_filter_add
    override val viewModelClass = FilterAddViewModel::class.java
    private val args: FilterAddFragmentArgs by navArgs()

    private var numberDataAdapter: NumberDataAdapter? = null
    private var numberDataList: ArrayList<NumberData> = ArrayList()
    private var countryCodeList: ArrayList<CountryCode> = arrayListOf()
    private var addFilter: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("filterDetailTAG",
            "FilterAddFragment onViewCreated before args.filter ${args.filterAdd} binding?.filter ${binding?.filter}")
        binding?.filter = args.filterAdd?.apply {
            filterAction = filterAction ?: FilterAction.FILTER_ACTION_INVALID
            if (filter.isNotEmpty() && isTypeContain().not()) {
                addFilter = filter
                filter = String.EMPTY
                isPreview = false
            }
        }
        Log.e("filterDetailTAG",
            "FilterAddFragment onViewCreated after args.filter ${args.filterAdd} binding?.filter ${binding?.filter}")
        binding?.filterAddEmptyList?.emptyState =
            if (binding?.filter?.isBlackFilter()
                    .isTrue()
            ) EmptyState.EMPTY_STATE_FILTERS_CONTACTS_BY_BLOCKER
            else EmptyState.EMPTY_STATE_FILTERS_CONTACTS_BY_PERMISSION
        binding?.executePendingBindings()
        setToolbar()
        setClickListeners()
        setFragmentResultListeners()
        setFilterTextChangeListener()
        setContactAdapter()
        if (binding?.filter?.isTypeContain().isTrue().not()) {
            viewModel.getCountryCodeList()
        }
        viewModel.getNumberDataList()
    }

    private fun setToolbar() {
        (activity as MainActivity).apply {
            toolbar?.apply {
                title =
                    getString(if (binding?.filter?.isBlackFilter()
                            .isTrue()
                    ) R.string.blocker else R.string.allow)
                menu?.clear()
                inflateMenu(R.menu.toolbar_filter)
                menu?.findItem(R.id.filter_menu_item)?.apply {
                    icon = ContextCompat.getDrawable(context,
                        if (binding?.filter?.isBlackFilter()
                                .isTrue()
                        ) R.drawable.ic_black_to_white_filter else R.drawable.ic_white_to_black_filter)
                    setOnMenuItemClickListener {
                        findNavController().navigate(FilterAddFragmentDirections.startFilterActionDialog(
                            filter = binding?.filter,
                            filterAction = CHANGE_FILTER))
                        return@setOnMenuItemClickListener true
                    }
                }
            }
        }
    }

    private fun setClickListeners() {
        binding?.apply {
            filterAddConditionsDescription.setSafeOnClickListener {
                filter?.conditionTypeInfo()
                    ?.let { info -> filterAddConditionsDescription.showPopUpWindow(info) }
            }
            filterAddItemFilter.root.setSafeOnClickListener {
                findNavController().navigate(FilterAddFragmentDirections.startFilterDetailFragment(
                    filterDetail = filter?.apply { isPreview = true}))
            }
            filterAddSubmit.root.setSafeOnClickListener {
                if (BlackListerApp.instance?.isLoggedInUser()
                        .isTrue() && BlackListerApp.instance?.isNetworkAvailable.isTrue().not()
                ) {
                    showMessage(getString(R.string.unavailable_network_repeat), true)
                } else {
                    findNavController().navigate(FilterAddFragmentDirections.startFilterActionDialog(
                        filter = filter?.apply { filter = addFilter() },
                        filterAction = filter?.filterAction?.name))
                }
            }
        }
    }

    private fun setFragmentResultListeners() {
        setFragmentResultListener(CHANGE_FILTER) { _, _ ->
            binding?.filter = binding?.filter?.apply {
                filterType =
                    if (binding?.filter?.isBlackFilter().isTrue()) WHITE_FILTER else BLACK_FILTER
            }
            filterContactFilterList(binding?.filter?.filter.orEmpty())
            setToolbar()
        }
        setFragmentResultListener(FilterAction.FILTER_ACTION_ADD.name) { _, _ ->
            binding?.filter?.let {
                viewModel.insertFilter(it)
            }
        }
        setFragmentResultListener(FilterAction.FILTER_ACTION_CHANGE.name) { _, _ ->
            binding?.filter?.let {
                viewModel.updateFilter(it)
            }
        }
        setFragmentResultListener(FilterAction.FILTER_ACTION_DELETE.name) { _, _ ->
            binding?.filter?.let {
                viewModel.deleteFilter(it)
            }
        }
    }

    private fun setFilterTextChangeListener() {
        binding?.apply {
            container.hideKeyboardWithLayoutTouch()
            filterAddContactList.hideKeyboardWithLayoutTouch()
            filterAddCountryCodeSpinner.hideKeyboardWithLayoutTouch()
            filterAddInput.doAfterTextChanged {
                filterToInput = false
                filter = filter?.apply {
                    filter =
                        if (this.isTypeContain()) filterAddInput.inputText() else filterAddInput.getRawText()
                    filterContactFilterList(this.filter)
                }
            }
        }
    }

    private fun setContactAdapter() {
            numberDataAdapter = numberDataAdapter ?: NumberDataAdapter(numberDataList) { numberData ->
                val number =
                    when (numberData) {
                        is Filter -> numberData.filter
                        is Contact -> numberData.number
                        is LogCall -> numberData.number
                        else -> String.EMPTY
                    }
                binding?.apply {
                    binding?.filterToInput = true
                    if (filter?.isTypeContain().isTrue()) {
                        filter = filter?.apply {
                            this.filter =
                                number.digitsTrimmed().replace(PLUS_CHAR.toString(), String.EMPTY)
                        }
                    } else {
                        val phoneNumber =
                            number.getPhoneNumber(filter?.countryCode?.country.orEmpty())
                        Log.e("filterAddTAG",
                            "BaseAddFragment ContactFilterAdapter contactClick phoneNumber $phoneNumber")
                        if ((phoneNumber?.nationalNumber.toString() == filterAddInput.getRawText() &&
                                    String.format(COUNTRY_CODE_START,
                                        phoneNumber?.countryCode.toString()) == filterAddCountryCodeValue.text.toString()).not()
                        ) {
                            filter = filter?.apply {
                                this.filter =
                                    phoneNumber?.nationalNumber?.toString()
                                        ?: number.digitsTrimmed()
                            }
                            filterAddCountryCodeSpinner.setSelection(countryCodeList.indexOfFirst {
                                it.countryCode == if (phoneNumber?.countryCode.isNull()) binding?.filter?.countryCode?.countryCode else String.format(
                                    COUNTRY_CODE_START,
                                    phoneNumber?.countryCode.toString())
                            }.orZero())
                            Log.e("filterAddTAG",
                                "BaseAddFragment ContactFilterAdapter filterAddCountryCodeSpinner filter ${filter?.filter} countryCode ${filter?.countryCode?.countryCode}")
                        }
                    }
                }
                Log.e("filterAddTAG",
                    "BaseAddFragment ContactFilterAdapter contactClick contact $numberData")
            }
            binding?.filter?.let { numberDataAdapter?.addingFilter = it }
            binding?.filterAddContactList?.adapter = numberDataAdapter
    }

    private fun setCountrySpinner() {
        binding?.filterAddCountryCodeSpinner?.apply {
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    spinner: AdapterView<*>?,
                    tv: View?,
                    position: Int,
                    id: Long,
                ) {
                    countryCodeList[position].let { selectedCountryCode ->
                        binding?.filter = binding?.filter?.apply {
                            countryCode = selectedCountryCode
                        }
                    }
                    if (binding?.filter?.isTypeFull().isTrue()) {
                        binding?.filterAddInput?.setNumberMask(binding?.filter?.conditionTypeFullHint()
                            .orEmpty())
                    } else if (binding?.filter?.isTypeStart().isTrue()) {
                        binding?.filterAddInput?.setNumberMask(binding?.filter?.conditionTypeStartHint()
                            .orEmpty())
                    }
                    binding?.filterAddInput?.post {
                        if (addFilter.isNotNull()) {
                            binding?.filterToInput = true
                            binding?.filterAddInput?.setText(addFilter.orEmpty())
                            addFilter = null
                        }
                    }
                    Log.e("filterAddTAG",
                        "BaseAddFragment OnItemSelectedListener countryCode ${binding?.filter?.countryCode?.countryCode} binding?.filter ${binding?.filter?.filter} args.filter ${this@FilterAddFragment.args.filterAdd?.filter}")
                }

                override fun onNothingSelected(p0: AdapterView<*>?) = Unit
            }
            val countryAdapter = context?.let {
                ArrayAdapter(it,
                    android.R.layout.simple_spinner_item,
                    countryCodeList.map { countryCode -> countryCode.countryEmoji() })
            }
            binding?.filterAddCountryCodeSpinner?.adapter = countryAdapter
            val countryCodeIndex = countryCodeList.indexOfFirst {
                it.country == if (binding?.filter?.countryCode?.country.isNullOrEmpty()) context.getUserCountry()
                    ?.uppercase() else binding?.filter?.countryCode?.country
            }.orZero()
            setSelection(countryCodeIndex)
        }
    }

    private fun filterContactFilterList(searchQuery: String) {
        val filteredContactList = numberDataList.filter { mainData ->
            (when (mainData) {
                is Contact -> mainData.trimmedPhone.contains(searchQuery).isTrue()
                is Filter -> mainData.filter.contains(searchQuery).isTrue()
                is LogCall -> mainData.number.contains(searchQuery).isTrue()
                else -> false
            })
        }
        binding?.apply {
            filter?.let { numberDataAdapter?.addingFilter = it }
            val existingFilter =
                filteredContactList.find { (it is Filter) && it.filter == filter?.addFilter() && it.conditionType == filter?.conditionType } as? Filter
            filter?.filterAction = when (existingFilter?.filterType) {
                null -> if (filter?.isInValidPhoneNumber()
                        .isTrue()
                ) FilterAction.FILTER_ACTION_INVALID else FilterAction.FILTER_ACTION_ADD
                filter?.filterType -> FilterAction.FILTER_ACTION_DELETE
                else -> FilterAction.FILTER_ACTION_CHANGE
            }
            filteredContactList.toMutableList().moveToFirst(existingFilter?.apply {
                filterAction = binding?.filter?.filterAction
            }).let { contactList ->
                numberDataAdapter?.numberDataList = ArrayList(contactList)
                numberDataAdapter?.notifyDataSetChanged()
            }
            filterAddContactList.isVisible = filteredContactList.isEmpty().not()
            filterAddEmptyList.root.isVisible = filteredContactList.isEmpty()
            filter?.let { numberDataAdapter?.addingFilter = it }
        }
        Log.e("filterAddTAG",
            "BaseAddFragment filterContactList filteredContactList?.size ${filteredContactList.size}")
    }

    override fun observeLiveData() {
        with(viewModel) {
            countryCodeListLiveData.safeSingleObserve(viewLifecycleOwner) { countryCodeList ->
                Log.e("filterAddTAG",
                    "BaseAddFragment observeLiveData countryCodeLiveData countryCodeList.size ${countryCodeList.size}")
                this@FilterAddFragment.countryCodeList = ArrayList(countryCodeList)
                setCountrySpinner()
            }
            numberDataListLiveData.safeSingleObserve(viewLifecycleOwner) { mainDataList ->
                Log.e("filterAddTAG",
                    "BaseAddFragment observeLiveData filterListLiveData filterList.size ${mainDataList.size}")
                this@FilterAddFragment.numberDataList = ArrayList(mainDataList)
                numberDataAdapter?.numberDataList = this@FilterAddFragment.numberDataList
                numberDataAdapter?.notifyDataSetChanged()
                binding?.filterToInput = true
                binding?.filter = binding?.filter
                viewModel.hideMainProgress()
            }
            filterActionLiveData.safeSingleObserve(viewLifecycleOwner) { filter ->
                handleSuccessFilterAction(filter)
            }
        }
    }

    private fun handleSuccessFilterAction(filter: Filter) {
        (activity as MainActivity).apply {
            showMessage(String.format(getString(filter.filterActionSuccessText()),
                filter.filter), false)
            getAllData()
            Log.e("filterAddTAG",
                "BaseAddFragment handleSuccessFilterAction message ${
                    String.format(getString(filter.filterActionSuccessText()),
                        filter.filter)
                }")
            if (filter.filterAction == FilterAction.FILTER_ACTION_CHANGE) {
                mainViewModel.successAllDataLiveData.safeSingleObserve(viewLifecycleOwner) {
                    viewModel.getNumberDataList()
                }
            } else {
                findNavController().navigate(if (binding?.filter?.isBlackFilter().isTrue())
                    FilterAddFragmentDirections.startBlackFilterListFragment()
                else FilterAddFragmentDirections.startWhiteFilterListFragment())
            }
        }
    }
}
