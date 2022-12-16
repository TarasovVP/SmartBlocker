package com.tarasovvp.smartblocker.ui.number_data.filter_add

import android.util.Log
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.smartblocker.BlackListerApp
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants.COUNTRY_CODE
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

    override fun initViews() {
        setFilterTextChangeListener()
        Log.e("filterAddTAG",
            "FilterAddFragment onViewCreated before args.filter ${args.filterAdd} binding?.filter ${binding?.filter}")
        binding?.filter = args.filterAdd?.apply {
            filterAction = filterAction ?: FilterAction.FILTER_ACTION_INVALID
            isPreview = false
        }
        Log.e("filterAddTAG",
            "FilterAddFragment onViewCreated after args.filter ${args.filterAdd} binding?.filter ${binding?.filter}")
        binding?.filterAddEmptyList?.emptyState =
            if (binding?.filter?.isBlocker()
                    .isTrue()
            ) EmptyState.EMPTY_STATE_FILTERS_CONTACTS_BY_BLOCKER
            else EmptyState.EMPTY_STATE_FILTERS_CONTACTS_BY_PERMISSION
        binding?.executePendingBindings()
    }

    override fun setClickListeners() {
        binding?.apply {
            filterAddItemFilter.itemFilterDetailContainer.setSafeOnClickListener {
                findNavController().navigate(FilterAddFragmentDirections.startFilterDetailFragment(
                    filterDetail = filter?.apply { isPreview = true }))
            }
            filterAddSubmit.setSafeOnClickListener {
                if (BlackListerApp.instance?.isLoggedInUser()
                        .isTrue() && BlackListerApp.instance?.isNetworkAvailable.isNotTrue()
                ) {
                    showMessage(getString(R.string.unavailable_network_repeat), true)
                } else {
                    findNavController().navigate(FilterAddFragmentDirections.startFilterActionDialog(
                        filterNumber = filter?.addFilter(),
                        filterAction = filter?.filterAction ?: if (filter?.isBlocker()
                                .isTrue()
                        ) FilterAction.FILTER_ACTION_BLOCKER_ADD else FilterAction.FILTER_ACTION_PERMISSION_ADD))
                }
            }
            filterAddCountryCodeSpinner.setSafeOnClickListener {
                Log.e("countryCodeTAG",
                    "FilterAddDialog filterAddCountryCodeSpinner currentDestination?.displayName ${findNavController().currentDestination?.displayName}")
                findNavController().navigate(FilterAddFragmentDirections.startCountryCodeSearchDialog())
            }
        }
    }

    override fun getData() {
        viewModel.getNumberDataList()
        if (binding?.filter?.isTypeContain().isNotTrue()) {
            if (binding?.filter?.countryCode?.countryCode.isNullOrEmpty()) {
                viewModel.getCountryCodeWithCountry(context?.getUserCountry())
            } else {
                binding?.filter?.countryCode?.let { setCountryCode(it) }
            }
        } else {
            binding?.filterAddInput?.hint = getString(R.string.enter_filter)
        }
    }

    override fun setFragmentResultListeners() {
        binding?.filter?.let { filter ->
            setFragmentResultListener(COUNTRY_CODE) { _, bundle ->
                bundle.parcelable<CountryCode>(COUNTRY_CODE)?.let { setCountryCode(it) }
            }
            setFragmentResultListener(FILTER_ACTION) { _, bundle ->
                when (val filterAction =
                    bundle.serializable<FilterAction>(FILTER_ACTION)) {
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
                    filter = filterAddInput.inputText().replace("#", String.EMPTY)
                        .replace(" ", String.EMPTY)
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
                        filterAddCountryCodeSpinner.text =
                            if (phoneNumber?.countryCode.isNull()) binding?.filter?.countryCode?.countryCode else String.format(
                                COUNTRY_CODE_START,
                                phoneNumber?.countryCode.toString())
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

    private fun setCountryCode(countryCode: CountryCode) {
        binding?.apply {
            filter = filter?.apply {
                this.countryCode = countryCode
                if (isTypeFull().isTrue()) {
                    filterAddInput.setNumberMask(conditionTypeFullHint())
                } else if (isTypeStart().isTrue()) {
                    filterAddInput.setNumberMask(conditionTypeStartHint())
                }
            }
            Log.e("filterAddTAG",
                "BaseAddFragment OnItemSelectedListener countryCode ${filter?.countryCode?.countryCode} binding?.filter ${filter?.filter} args.filter ${this@FilterAddFragment.args.filterAdd?.filter}")

            filterAddCountryCodeSpinner.text = countryCode.countryEmoji()
            viewModel.filterNumberDataList(filter, numberDataList)
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
            countryCodeLiveData.safeSingleObserve(viewLifecycleOwner) { countryCode ->
                Log.e("filterAddTAG",
                    "BaseAddFragment observeLiveData countryCodeLiveData countryCode $countryCode")
                setCountryCode(countryCode)
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
                        DEFAULT_FILTER -> if (isInValidPhoneNumber().isTrue()) FilterAction.FILTER_ACTION_INVALID else if (isBlocker()) FilterAction.FILTER_ACTION_BLOCKER_ADD else FilterAction.FILTER_ACTION_PERMISSION_ADD
                        filterType -> if (isBlocker()) FilterAction.FILTER_ACTION_BLOCKER_DELETE else FilterAction.FILTER_ACTION_PERMISSION_DELETE
                        else -> if (isBlocker()) FilterAction.FILTER_ACTION_BLOCKER_TRANSFER else FilterAction.FILTER_ACTION_PERMISSION_TRANSFER
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
        Log.e("destinationTAG",
            "FilterAddFragment before handleSuccessFilterAction currentDestination ${findNavController().currentDestination?.displayName}")
        (activity as MainActivity).apply {
            showMessage(String.format(filter.filterAction?.successText?.let { getString(it) }
                .orEmpty(),
                filter.filter), false)
            getAllData()
            Log.e("destinationTAG",
                "FilterAddFragment handleSuccessFilterAction currentDestination ${findNavController().currentDestination?.displayName}")
            findNavController().navigate(if (binding?.filter?.isBlocker().isTrue())
                FilterAddFragmentDirections.startBlackFilterListFragment()
            else FilterAddFragmentDirections.startWhiteFilterListFragment())
        }
    }
}
