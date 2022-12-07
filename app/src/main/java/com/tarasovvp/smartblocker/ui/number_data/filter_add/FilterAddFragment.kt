package com.tarasovvp.smartblocker.ui.number_data.filter_add

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.smartblocker.BlackListerApp
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants.COUNTRY_CODE_START
import com.tarasovvp.smartblocker.constants.Constants.DEFAULT_FILTER
import com.tarasovvp.smartblocker.constants.Constants.FILTER_ACTION
import com.tarasovvp.smartblocker.constants.Constants.PLUS_CHAR
import com.tarasovvp.smartblocker.databinding.FragmentFilterAddBinding
import com.tarasovvp.smartblocker.enums.EmptyState
import com.tarasovvp.smartblocker.enums.FilterAction
import com.tarasovvp.smartblocker.extensions.*
import com.tarasovvp.smartblocker.models.CountryCode
import com.tarasovvp.smartblocker.models.Filter
import com.tarasovvp.smartblocker.models.NumberData
import com.tarasovvp.smartblocker.ui.MainActivity
import com.tarasovvp.smartblocker.ui.base.BaseDetailFragment
import com.tarasovvp.smartblocker.ui.number_data.NumberDataAdapter
import com.tarasovvp.smartblocker.utils.setSafeOnClickListener

open class FilterAddFragment :
    BaseDetailFragment<FragmentFilterAddBinding, FilterAddViewModel>() {

    override var layoutId = R.layout.fragment_filter_add
    override val viewModelClass = FilterAddViewModel::class.java
    private val args: FilterAddFragmentArgs by navArgs()

    private var numberDataAdapter: NumberDataAdapter? = null
    private var numberDataList: ArrayList<NumberData> = ArrayList()
    private var countryCodeList: ArrayList<CountryCode> = arrayListOf()
    private var addFilter: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragmentResultListeners()
        setFilterTextChangeListener()
    }

    override fun initViews() {
        Log.e("filterAddTAG",
            "FilterAddFragment onViewCreated before args.filter ${args.filterAdd} binding?.filter ${binding?.filter}")
        binding?.filter = args.filterAdd?.apply {
            filterAction = filterAction ?: FilterAction.FILTER_ACTION_INVALID
            isPreview = false
            if (filter.isNotEmpty() && isTypeContain().not()) {
                addFilter = filter
                filter = String.EMPTY
            }
        }
        Log.e("filterAddTAG",
            "FilterAddFragment onViewCreated after args.filter ${args.filterAdd} binding?.filter ${binding?.filter}")
        binding?.filterAddEmptyList?.emptyState =
            if (binding?.filter?.isBlackFilter()
                    .isTrue()
            ) EmptyState.EMPTY_STATE_FILTERS_CONTACTS_BY_BLOCKER
            else EmptyState.EMPTY_STATE_FILTERS_CONTACTS_BY_PERMISSION
        binding?.executePendingBindings()
    }

    override fun setClickListeners() {
        binding?.apply {
            filterAddConditionsDescription.setSafeOnClickListener {
                filter?.conditionTypeInfo()
                    ?.let { info -> filterAddConditionsDescription.showPopUpWindow(info) }
            }
            filterAddItemFilter.root.setSafeOnClickListener {
                findNavController().navigate(FilterAddFragmentDirections.startFilterDetailFragment(
                    filterDetail = filter?.apply { isPreview = true }))
            }
            filterAddSubmit.root.setSafeOnClickListener {
                if (BlackListerApp.instance?.isLoggedInUser()
                        .isTrue() && BlackListerApp.instance?.isNetworkAvailable.isNotTrue()
                ) {
                    showMessage(getString(R.string.unavailable_network_repeat), true)
                } else {
                    findNavController().navigate(FilterAddFragmentDirections.startFilterActionDialog(
                        filterNumber = filter?.addFilter(),
                        filterAction = filter?.filterAction ?: if (filter?.isBlackFilter()
                                .isTrue()
                        ) FilterAction.FILTER_ACTION_BLOCKER_ADD else FilterAction.FILTER_ACTION_PERMISSION_ADD))
                }
            }
        }
    }

    override fun getData() {
        viewModel.getNumberDataList()
        if (binding?.filter?.isTypeContain().isNotTrue()) {
            viewModel.getCountryCodeList()
        } else {
            binding?.filterAddInput?.hint = getString(R.string.enter_filter)
        }
    }

    private fun setFragmentResultListeners() {
        binding?.filter?.let { filter ->
            setFragmentResultListener(FILTER_ACTION) { _, bundle ->
                when (val filterAction =
                    bundle.getSerializable(FILTER_ACTION) as FilterAction) {
                    FilterAction.FILTER_ACTION_BLOCKER_TRANSFER,
                    FilterAction.FILTER_ACTION_PERMISSION_TRANSFER,
                    -> viewModel.updateFilter(filter.apply {
                        this.filterAction = filterAction
                    })
                    FilterAction.FILTER_ACTION_BLOCKER_DELETE,
                    FilterAction.FILTER_ACTION_PERMISSION_DELETE,
                    -> viewModel.deleteFilter(filter.apply {
                        this.filter = addFilter()
                        this.filterAction = filterAction
                    })
                    FilterAction.FILTER_ACTION_BLOCKER_ADD,
                    FilterAction.FILTER_ACTION_PERMISSION_ADD,
                    -> viewModel.insertFilter(filter.apply {
                        numberData = filter.addFilter()
                        this.filter = addFilter()
                        filterWithoutCountryCode = extractFilterWithoutCountryCode()
                        this.filterAction = filterAction
                    })
                    else -> Unit
                }
            }
        }
    }

    private fun setFilterTextChangeListener() {
        binding?.apply {
            container.hideKeyboardWithLayoutTouch()
            filterAddContactList.hideKeyboardWithLayoutTouch()
            filterAddCountryCodeSpinner.hideKeyboardWithLayoutTouch()
            filterAddInput.setupClearButtonWithAction()
            filterAddInput.doAfterTextChanged {
                Log.e("filterAddTAG",
                    "FilterAddFragment setFilterTextChangeListener before editable $it numberFormat")
                if ((filter?.conditionTypeFullHint() == it.toString() && filter?.isTypeFull()
                        .isTrue())
                    || (filter?.conditionTypeStartHint() == it.toString() && filter?.isTypeStart()
                        .isTrue())
                ) return@doAfterTextChanged
                Log.e("filterAddTAG",
                    "FilterAddFragment setFilterTextChangeListener after filter.filter ${filter?.filter} editable $it numberFormat ${filter?.countryCode?.numberFormat} conditionTypeFullHint ${filter?.conditionTypeFullHint()}")
                filterToInput = false
                filter = filter?.apply {
                    filter =
                        if (this.isTypeContain()) filterAddInput.inputText() else filterAddInput.getRawText()
                    viewModel.checkFilterExist(this)
                }
                viewModel.filterNumberDataList(filter, numberDataList)
            }
        }
    }

    override fun createAdapter() {
        numberDataAdapter = numberDataAdapter ?: NumberDataAdapter(numberDataList) { numberData ->
            binding?.apply {
                binding?.filterToInput = true
                if (filter?.isTypeContain().isTrue()) {
                    filter = filter?.apply {
                        this.filter =
                            numberData.numberData.digitsTrimmed()
                                .replace(PLUS_CHAR.toString(), String.EMPTY)
                    }
                } else {
                    val phoneNumber =
                        numberData.numberData.getPhoneNumber(filter?.countryCode?.country.orEmpty())
                    Log.e("filterAddTAG",
                        "BaseAddFragment ContactFilterAdapter contactClick phoneNumber $phoneNumber")
                    if ((phoneNumber?.nationalNumber.toString() == filterAddInput.getRawText() &&
                                String.format(COUNTRY_CODE_START,
                                    phoneNumber?.countryCode.toString()) == filterAddCountryCodeValue.text.toString()).not()
                    ) {
                        filter = filter?.apply {
                            this.filter =
                                phoneNumber?.nationalNumber?.toString()
                                    ?: numberData.numberData.digitsTrimmed()
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

    private fun filterNumberDataList(filteredList: ArrayList<NumberData>) {
        Log.e("filterAddTAG",
            "FilterAddFragment filterNumberDataList filteredList?.size ${filteredList.size}")
        binding?.apply {
            numberDataAdapter?.numberDataList = filteredList
            numberDataAdapter?.notifyDataSetChanged()
            filterAddContactList.isVisible = filteredList.isEmpty().not()
            filterAddEmptyList.root.isVisible = filteredList.isEmpty()
            Log.e("filterAddTAG",
                "FilterAddFragment filterContactFilterList viewModel.hideProgress()")
            viewModel.hideProgress()
        }
        Log.e("filterAddTAG",
            "BaseAddFragment filterContactList filteredContactList?.size ${filteredList.size}")
    }

    override fun observeLiveData() {
        with(viewModel) {
            countryCodeListLiveData.safeSingleObserve(viewLifecycleOwner) { countryCodeList ->
                Log.e("filterAddTAG",
                    "BaseAddFragment observeLiveData countryCodeLiveData countryCodeList.size ${countryCodeList.size}")
                this@FilterAddFragment.countryCodeList = ArrayList(countryCodeList)
                setCountrySpinner()
            }
            numberDataListLiveData.safeSingleObserve(viewLifecycleOwner) { numberDataList ->
                Log.e("filterAddTAG",
                    "BaseAddFragment observeLiveData filterListLiveData filterList.size ${numberDataList.size}")
                this@FilterAddFragment.numberDataList = ArrayList(numberDataList)
                if (binding?.filter?.isTypeContain().isTrue().not()) {
                    filterNumberDataList(binding?.filter, this@FilterAddFragment.numberDataList)
                } else {
                    numberDataAdapter?.numberDataList = this@FilterAddFragment.numberDataList
                    numberDataAdapter?.notifyDataSetChanged()
                }
                binding?.filterToInput = true
                binding?.filter = binding?.filter
            }
            existingFilterLiveData.safeSingleObserve(viewLifecycleOwner) { existingFilter ->
                Log.e("filterAddTAG",
                    "BaseAddFragment observeLiveData existingFilterLiveData existingFilter $existingFilter")
                binding?.filter = binding?.filter?.apply {
                    filterAction = when (existingFilter.filterType) {
                        DEFAULT_FILTER -> if (isInValidPhoneNumber().isTrue()) FilterAction.FILTER_ACTION_INVALID else if (isBlackFilter()) FilterAction.FILTER_ACTION_BLOCKER_ADD else FilterAction.FILTER_ACTION_PERMISSION_ADD
                        filterType -> if (isBlackFilter()) FilterAction.FILTER_ACTION_BLOCKER_DELETE else FilterAction.FILTER_ACTION_PERMISSION_DELETE
                        else -> if (isBlackFilter()) FilterAction.FILTER_ACTION_BLOCKER_TRANSFER else FilterAction.FILTER_ACTION_PERMISSION_TRANSFER
                    }
                }
            }
            filteredNumberDataListLiveData.safeSingleObserve(viewLifecycleOwner) { filteredNumberDataList ->
                Log.e("filterAddTAG",
                    "BaseAddFragment observeLiveData filteredNumberDataListLiveData filteredNumberDataList.size ${filteredNumberDataList.size}")
                filterNumberDataList(filteredNumberDataList)
            }
            filterActionLiveData.safeSingleObserve(viewLifecycleOwner) { filter ->
                handleSuccessFilterAction(filter)
            }
        }
    }

    private fun handleSuccessFilterAction(filter: Filter) {
        (activity as MainActivity).apply {
            showMessage(String.format(filter.filterAction?.successText?.let { getString(it) }
                .orEmpty(),
                filter.filter), false)
            getAllData()
            findNavController().navigate(if (binding?.filter?.isBlackFilter().isTrue())
                FilterAddFragmentDirections.startBlackFilterListFragment()
            else FilterAddFragmentDirections.startWhiteFilterListFragment())
        }
    }
}
