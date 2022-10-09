package com.tarasovvp.blacklister.ui.main.filter_add

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.constants.Constants.BLACK_FILTER
import com.tarasovvp.blacklister.constants.Constants.WHITE_FILTER
import com.tarasovvp.blacklister.databinding.FragmentFilterAddBinding
import com.tarasovvp.blacklister.enums.Condition
import com.tarasovvp.blacklister.extensions.*
import com.tarasovvp.blacklister.model.*
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.utils.DebouncingTextChangeListener
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

open class FilterAddFragment :
    BaseFragment<FragmentFilterAddBinding, FilterAddViewModel>() {

    override var layoutId = R.layout.fragment_filter_add
    override val viewModelClass = FilterAddViewModel::class.java
    private val args: FilterAddFragmentArgs by navArgs()
    private var contactAdapter: ContactFilterAdapter? = null
    private var contactList: List<Contact>? = null
    private var countryCodeList: ArrayList<CountryCode>? = null

    private var isBlackFilter: Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("filterLifeCycleTAG", "FilterAddFragment onViewCreated")
        Log.e("filterAddTAG",
            "BaseAddFragment onViewCreated filter ${args.filter?.filter} type ${args.filter?.conditionType} isFromDb ${args.filter?.isFromDb} ")
        isBlackFilter = args.filter?.isBlackFilter().isTrue()
        setToolbar()
        setClickListeners()
        viewModel.getContactList()
        if (contactAdapter.isNull()) {
            contactAdapter = ContactFilterAdapter { phone ->
                binding?.apply {
                    filter = filter.apply {
                        this?.filter = phone
                    }
                }
                Log.e("filterAddTAG",
                    "BaseAddFragment ContactFilterAdapter contactClick contact $phone")
            }
            binding?.filterAddContactList?.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                this.adapter = contactAdapter
            }
        }
        viewModel.getCountryCodeMap()
        args.filter?.let { viewModel.checkFilterExist(it) }
        setFragmentResultListener(Constants.CHANGE_FILTER) { _, _ ->
            Log.e("filterAddTAG",
                "BaseAddFragment setFragmentResultListener getFilterObject() ${getFilterObject()}")
            isBlackFilter = isBlackFilter.not()
            viewModel.checkFilterExist(getFilterObject())
            setToolbar()
        }
        setFragmentResultListener(Constants.DELETE_FILTER) { _, _ ->
            binding?.filter?.let {
                viewModel.deleteFilter(it)
            }
        }
    }

    private fun setToolbar() {
        (activity as MainActivity).apply {
            toolbar?.apply {
                title = getString(Condition.getTitleByIndex(args.filter?.conditionType.orZero()))
                menu?.clear()
                inflateMenu(R.menu.toolbar_filter)
                menu?.findItem(R.id.filter_menu_item)?.apply {
                    icon = ContextCompat.getDrawable(context,
                        if (isBlackFilter) R.drawable.ic_white_filter else R.drawable.ic_block)
                    setOnMenuItemClickListener {
                        findNavController().navigate(FilterAddFragmentDirections.startChangeFilterDialog(
                            args.filter))
                        return@setOnMenuItemClickListener true
                    }
                }
            }
        }
    }

    private fun initViewsWithData(filter: Filter) {
        Log.e("filterAddTAG",
            "BaseAddFragment initViewsWithData this $this filter $filter filterToInput ${
                context?.let {
                    filter.filterToInput(it)
                }
            }")
        binding?.apply {
            this.filter = filter
            filterAddConditionsInfo.text = if (filterAddInput.inputText()
                    .isEmpty()
            ) getString(R.string.add_filter_message) else String.format(if (filter.isNotNull()) getString(
                R.string.edit_filter_with_filter_message) else getString(R.string.add_filter_with_filter_message),
                filterAddInput.text)
            viewModel.checkContactListByFilter(filter)
        }
        existNumberChecking()
    }

    private fun existNumberChecking() {
        binding?.apply {
            root.post {
                filterAddInput.addTextChangedListener(DebouncingTextChangeListener(lifecycle) { input ->
                    Log.e("filterAddTAG",
                        "BaseAddFragment addTextChangedListener it $input filter ${getFilterObject().filter} type ${getFilterObject().conditionType} isFromDb ${getFilterObject().isFromDb}")
                    //viewModel.checkFilterExist(getFilterObject())
                    itemFilter.filter = getFilterObject()
                    checkSubmitEnable(itemFilter.filter?.name == getString(R.string.invalid_phone_number))
                    filterContactList(input.toString())
                })
            }
        }
    }

    private fun getFilterObject(): Filter {
        return Filter(filter = String.format("+%s%s",
            binding?.filterAddCountryCodeValue?.text,
            binding?.filterAddInput.inputText()),
            filterType = if (isBlackFilter) BLACK_FILTER else WHITE_FILTER,
            name = if (binding?.filterAddInput.inputText().isValidPhoneNumber(context)) binding?.filterAddInput.inputText() else getString(R.string.invalid_phone_number)).apply {
            contactList?.find { contact -> contact.trimmedPhone.getPhoneNumber(countryCodeList?.find { it.countryCode == binding?.filterAddCountryCodeValue?.text.toString().toInt() }?.country.orEmpty())?.nationalNumber.toString() == binding?.filterAddInput.inputText() }
                ?.let {
                    name = it.name
                    photoUrl = it.photoUrl
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
                filter?.apply {
                    if (isFromDb.isTrue()) {
                        findNavController().navigate(FilterAddFragmentDirections.startDeleteFilterDialog(
                            filter = this))
                    } else {
                        viewModel.insertFilter(this)
                    }
                }
            }
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            existFilterLiveData.safeSingleObserve(viewLifecycleOwner) { filter ->
                Log.e("filterAddTAG",
                    "BaseAddFragment observeLiveData existFilterLiveData filter $filter filter isFromDb ${filter.isFromDb}")
                initViewsWithData(filter)
            }
            countryCodeLiveData.safeSingleObserve(viewLifecycleOwner) { countryCodeList ->
                setCountrySpinner(ArrayList(countryCodeList))
            }
            queryContactListLiveData.safeSingleObserve(viewLifecycleOwner) { contactList ->
                //setContactByFilterList(contactList)
            }
            contactLiveData.safeSingleObserve(viewLifecycleOwner) { contactList ->
                this@FilterAddFragment.contactList = contactList
                contactAdapter?.setHeaderAndData(contactList, HeaderDataItem())
            }
            insertFilterLiveData.safeSingleObserve(viewLifecycleOwner) { number ->
                handleSuccessFilterAction(String.format(getString(R.string.filter_added), number))
            }
            deleteFilterLiveData.safeSingleObserve(viewLifecycleOwner) {
                handleSuccessFilterAction(String.format(getString(R.string.delete_filter_from_list),
                    args.filter?.filter.orEmpty()))
            }
        }
    }

    private fun setContactByFilterList(contactList: List<Contact>) {
        binding?.filterAddContactByFilterList?.apply {
            isEnabled = contactList.isNotEmpty()
            Log.e("filterAddTAG",
                "BaseAddFragment setContactByFilterList contactList $contactList")
            val contactListMap =
                context?.contactListMap(contactList, isBlackFilter) ?: linkedMapOf()
            val contactByFilterAdapter =
                ContactByFilterAdapter(arrayListOf(contactListMap.keys.toTypedArray()[0]),
                    contactListMap)
            setAdapter(contactByFilterAdapter)
            setOnGroupClickListener { expandableListView, _, groupPosition, _ ->
                if (groupPosition == 0 && contactList.isNotEmpty()) {
                    binding?.isPreviewMode = expandableListView.isGroupExpanded(0).not()
                    contactByFilterAdapter.titleList =
                        if (expandableListView.isGroupExpanded(0)) arrayListOf(contactListMap.keys.toTypedArray()[0]) else ArrayList(
                            contactListMap.keys)
                    contactByFilterAdapter.contactListMap =
                        if (expandableListView.isGroupExpanded(0)) linkedMapOf() else contactListMap
                    contactByFilterAdapter.notifyDataSetInvalidated()
                }
                return@setOnGroupClickListener false
            }
            setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
                findNavController().navigate(FilterAddFragmentDirections.startContactDetailFragment(
                    number = contactListMap[ArrayList(contactListMap.keys)[groupPosition]]?.get(
                        childPosition)?.phone))
                return@setOnChildClickListener contactList.isEmpty()
            }
        }
    }

    private fun filterContactList(searchQuery: String) {
        val filteredContactList = contactList?.filter { contact ->
            (contact.phone).contains(searchQuery).isTrue()
        }
        filteredContactList?.let { contactList ->
            contactAdapter?.clearData()
            contactAdapter?.setHeaderAndData(contactList, HeaderDataItem())
            contactAdapter?.notifyDataSetChanged()
        }
        binding?.filterAddContactList?.isVisible = filteredContactList.isNullOrEmpty().not()
        binding?.filterAddEmptyList?.emptyStateContainer?.isVisible =
            filteredContactList.isNullOrEmpty()
        binding?.filterAddEmptyList?.emptyStateTitle?.text =
            getString(R.string.no_ruslt_with_list_query)
        Log.e("filterAddTAG",
            "BaseAddFragment filterContactList filteredContactList?.size ${filteredContactList?.size}")
    }

    private fun setCountrySpinner(countryCodeList: ArrayList<CountryCode>) {
        this.countryCodeList = countryCodeList
        countryCodeList.add(CountryCode(flagEmoji = null))
        val countryAdapter = context?.let {
            ArrayAdapter(it,
                android.R.layout.simple_spinner_item,
                countryCodeList.map { countryCode -> countryCode.countryEmoji() })
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
                    binding?.filterAddCountryCodeValue?.text =
                        countryCodeList[position].countryCode.toString()
                    binding?.itemFilter?.filter = getFilterObject()
                    checkSubmitEnable(binding?.itemFilter?.filter?.name == getString(R.string.invalid_phone_number))
                }

                override fun onNothingSelected(p0: AdapterView<*>?) = Unit
            }
            setSelection(countryCodeList.indexOfFirst {
                it.country == context.getUserCountry()?.uppercase()
            })
        }
    }

    private fun checkSubmitEnable(isValid: Boolean) {
        binding?.itemFilter?.itemFilterTypeTitle?.isEnabled = isValid.not()
        binding?.filterAddSubmit?.isEnabled = isValid.not()
    }

    private fun handleSuccessFilterAction(message: String) {
        (activity as MainActivity).apply {
            showMessage(message, false)
            getAllData()
        }
        findNavController().popBackStack()
    }
}
