package com.tarasovvp.blacklister.ui.main.filter_add

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
import com.tarasovvp.blacklister.constants.Constants.WHITE_FILTER
import com.tarasovvp.blacklister.databinding.FragmentFilterAddBinding
import com.tarasovvp.blacklister.enums.AddFilterState
import com.tarasovvp.blacklister.enums.Condition
import com.tarasovvp.blacklister.extensions.*
import com.tarasovvp.blacklister.model.*
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

open class FilterAddFragment :
    BaseFragment<FragmentFilterAddBinding, FilterAddViewModel>() {

    override var layoutId = R.layout.fragment_filter_add
    override val viewModelClass = FilterAddViewModel::class.java
    private val args: FilterAddFragmentArgs by navArgs()

    private var contactFilterAdapter: ContactFilterAdapter? = null
    private var contactFilterList: ArrayList<BaseAdapter.MainData> = ArrayList()
    private var countryCodeList: ArrayList<CountryCode>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.filter = args.filter?.apply {
            addFilterState = AddFilterState.ADD_FILTER_INVALID
        }
        setToolbar()
        setClickListeners()
        setFilterTextChangeListener()
        setFragmentResultListeners()
        setContactAdapter()
        if (binding?.filter?.isTypeContain().isTrue()) {
            viewModel.getContactsData()
        } else {
            viewModel.getCountryCodeAndContactsData()
        }
    }

    private fun setContactAdapter() {
        if (contactFilterAdapter.isNull()) {
            contactFilterAdapter = ContactFilterAdapter(contactFilterList) { phone ->
                binding?.apply {
                    val phoneNumber =
                        phone.getPhoneNumber(binding?.filter?.countryCode?.country.orEmpty())
                    binding?.filterToInput = true
                    Log.e("filterAddTAG",
                        "BaseAddFragment ContactFilterAdapter contactClick phoneNumber $phoneNumber")
                    if ((phoneNumber?.nationalNumber.toString() == filterAddInput.getRawText() && String.format(
                            COUNTRY_CODE_START,
                            phoneNumber?.countryCode.toString()) == filterAddCountryCodeValue.text.toString()).not()
                    ) {
                        filter = filter?.apply {
                            this.filter =
                                phoneNumber?.nationalNumber?.toString() ?: phone.digitsTrimmed()
                        }
                        filterAddCountryCodeSpinner.setSelection(countryCodeList?.indexOfFirst {
                            it.countryCode == if (phoneNumber?.countryCode.isNull()) binding?.filter?.countryCode?.countryCode else String.format(
                                COUNTRY_CODE_START,
                                phoneNumber?.countryCode.toString())
                        }.orZero())
                    }
                }
                Log.e("filterAddTAG",
                    "BaseAddFragment ContactFilterAdapter contactClick contact $phone")
            }
            binding?.filterAddContactList?.adapter = contactFilterAdapter
        }
    }

    private fun setFragmentResultListeners() {
        setFragmentResultListener(CHANGE_FILTER) { _, _ ->
            binding?.filter = binding?.filter?.apply {
                filterType = if (binding?.filter?.isBlackFilter().isTrue()) WHITE_FILTER else BLACK_FILTER
            }
            filterContactList(binding?.filter?.filter.orEmpty())
            setToolbar()
        }
        setFragmentResultListener(AddFilterState.ADD_FILTER_ADD.name) { _, _ ->
            binding?.filter?.let {
                viewModel.insertFilter(it)
            }
        }
        setFragmentResultListener(AddFilterState.ADD_FILTER_CHANGE.name) { _, _ ->
            binding?.filter?.let {
                viewModel.updateFilter(it)
            }
        }
        setFragmentResultListener(AddFilterState.ADD_FILTER_DELETE.name) { _, _ ->
            binding?.filter?.let {
                viewModel.deleteFilter(it)
            }
        }
    }

    private fun setToolbar() {
        (activity as MainActivity).apply {
            toolbar?.apply {
                title =
                    getString(Condition.getTitleByIndex(binding?.filter?.conditionType.orZero()))
                menu?.clear()
                inflateMenu(R.menu.toolbar_filter)
                menu?.findItem(R.id.filter_menu_item)?.apply {
                    icon = ContextCompat.getDrawable(context,
                        if (binding?.filter?.isBlackFilter()
                                .isTrue()
                        ) R.drawable.ic_black_to_white_filter else R.drawable.ic_white_to_black_filter)
                    setOnMenuItemClickListener {
                        findNavController().navigate(FilterAddFragmentDirections.startFilterActionDialog(filter = binding?.filter, filterAction = CHANGE_FILTER))
                        return@setOnMenuItemClickListener true
                    }
                }
            }
        }
    }

    private fun setFilterTextChangeListener() {
        binding?.apply {
            this.root.context.hideKeyboardWithLayoutTouch(container)
            this.root.context.hideKeyboardWithLayoutTouch(filterAddContactList)
            this.root.context.hideKeyboardWithLayoutTouch(filterAddCountryCodeSpinner)
            filterAddInput.doAfterTextChanged {
                filterToInput = false
                filter = filter?.apply {
                    filter = if (this.isTypeContain()) filterAddInput.inputText() else filterAddInput.getRawText()
                    filterContactList(this.filter)
                }
            }
        }
    }

    private fun setClickListeners() {
        binding?.apply {
            filterAddConditionsInfo.setSafeOnClickListener {
                filterAddConditionsInfo.showPopUpWindow(Info(title = getString(R.string.add_conditions_title),
                    description = getString(R.string.add_conditions_info),
                    icon = R.drawable.ic_logo))
            }
            filterAddSubmit.setSafeOnClickListener {
                if (BlackListerApp.instance?.isLoggedInUser().isTrue() && BlackListerApp.instance?.isNetworkAvailable.isTrue().not()) {
                    showMessage(getString(R.string.unavailable_network_repeat), true)
                } else {
                    findNavController().navigate(FilterAddFragmentDirections.startFilterActionDialog(filter = filter?.apply { filter = addFilter() }, filterAction = filter?.addFilterState?.name))
                }
            }
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            countryCodeListLiveData.safeSingleObserve(viewLifecycleOwner) { countryCodeList ->
                Log.e("filterAddTAG",
                    "BaseAddFragment observeLiveData countryCodeLiveData countryCodeList.size ${countryCodeList.size}")
                this@FilterAddFragment.countryCodeList = ArrayList(countryCodeList)
                setCountrySpinner()
            }
            mainDataListLiveData.safeSingleObserve(viewLifecycleOwner) { mainDataList ->
                Log.e("filterAddTAG",
                    "BaseAddFragment observeLiveData filterListLiveData filterList.size ${mainDataList.size}")
                this@FilterAddFragment.contactFilterList = ArrayList(mainDataList)
                contactFilterAdapter?.contactFilterList = this@FilterAddFragment.contactFilterList
                viewModel.hideMainProgress()
            }
            insertFilterLiveData.safeSingleObserve(viewLifecycleOwner) { number ->
                handleSuccessFilterAction(String.format(getString(R.string.filter_added), number))
            }
            updateFilterLiveData.safeSingleObserve(viewLifecycleOwner) { number ->
                handleSuccessFilterAction(String.format(getString(R.string.filter_update), number))
            }
            deleteFilterLiveData.safeSingleObserve(viewLifecycleOwner) {
                handleSuccessFilterAction(String.format(getString(R.string.delete_filter_from_list), binding?.filter?.filter.orEmpty()))
            }
        }
    }

    private fun filterContactList(searchQuery: String) {
        val filteredContactList = contactFilterList?.filter { mainData ->
            (when (mainData) {
                is Contact -> mainData.trimmedPhone.contains(searchQuery).isTrue()
                is Filter -> mainData.filter.contains(searchQuery).isTrue()
                else -> false
            })
        }
        binding?.apply {
            contactFilterAdapter?.searchQueryMap = searchQuery to filter?.countryCode?.countryCode.orEmpty()
            val existingFilter = filteredContactList.find { (it is Filter) && it.filter == filter?.addFilter() && it.conditionType == filter?.conditionType } as? Filter
            filter?.addFilterState = when (existingFilter?.filterType) {
                null -> if (filter?.isInValidPhoneNumber().isTrue()) AddFilterState.ADD_FILTER_INVALID else AddFilterState.ADD_FILTER_ADD
                filter?.filterType -> AddFilterState.ADD_FILTER_DELETE
                else -> AddFilterState.ADD_FILTER_CHANGE
            }
            filteredContactList.toMutableList().moveToFirst(existingFilter?.apply {
                addFilterState = binding?.filter?.addFilterState
            }).let { contactList ->
                contactFilterAdapter?.contactFilterList = ArrayList(contactList)
                contactFilterAdapter?.notifyDataSetChanged()
            }
            filterAddContactList.isVisible = filteredContactList.isEmpty().not()
            filterAddEmptyList.emptyStateContainer.isVisible = filteredContactList.isEmpty()
            filterAddEmptyList.emptyStateTitle.text =
                getString(R.string.no_result_with_list_query)
        }
        Log.e("filterAddTAG",
            "BaseAddFragment filterContactList filteredContactList?.size ${filteredContactList.size}")
    }

    private fun setCountrySpinner() {
        countryCodeList?.add(CountryCode())
        val countryAdapter = context?.let {
            ArrayAdapter(it,
                android.R.layout.simple_spinner_item,
                countryCodeList?.map { countryCode -> countryCode.countryEmoji() }.orEmpty())
        }
        binding?.filterAddCountryCodeSpinner?.apply {
            adapter = countryAdapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    spinner: AdapterView<*>?,
                    tv: View?,
                    position: Int,
                    id: Long,
                ) {
                    countryCodeList?.get(position)?.let { selectedCountryCode ->
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
                    Log.e("filterAddTAG",
                        "BaseAddFragment OnItemSelectedListener countryCode ${binding?.filter?.countryCode} itemFilter?.filter ${binding?.itemFilter?.filter}")
                }

                override fun onNothingSelected(p0: AdapterView<*>?) = Unit
            }
            val countryCodeIndex = countryCodeList?.indexOfFirst {
                it.country == if (binding?.filter?.isTypeContain()
                        .isTrue()
                ) String.EMPTY else context.getUserCountry()?.uppercase()
            }.orZero()
            setSelection(countryCodeIndex)
        }
    }

    private fun handleSuccessFilterAction(message: String) {
        (activity as MainActivity).apply {
            showMessage(message, false)
            getAllData()
            mainViewModel.successAllDataLiveData.safeSingleObserve(viewLifecycleOwner) {
                if (binding?.filter?.isTypeContain().isTrue()) {
                    viewModel.getContactsData()
                } else {
                    viewModel.getCountryCodeAndContactsData()
                }
            }
        }
    }
}
