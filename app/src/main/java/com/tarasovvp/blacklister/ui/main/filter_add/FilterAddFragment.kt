package com.tarasovvp.blacklister.ui.main.filter_add

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tarasovvp.blacklister.BlackListerApp
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
    private var countryCode: CountryCode? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivity).setMainProgressVisibility(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("filterLifeCycleTAG", "FilterAddFragment onViewCreated")
        Log.e("filterAddTAG",
            "BaseAddFragment onViewCreated filter ${args.filter?.filter} type ${args.filter?.conditionType} isFromDb ${args.filter?.isFromDb} ")
        binding?.filter = args.filter
        setToolbar()
        setClickListeners()
        existNumberChecking()
        if (args.filter?.isTypeFull().isTrue() || args.filter?.isTypeStart().isTrue()) {
            viewModel.getCountryCodeAndContactsData()
        }
        if (contactAdapter.isNull()) {
            contactAdapter = ContactFilterAdapter { phone ->
                binding?.apply {
                    val phoneNumber = phone.getPhoneNumber(countryCode?.country.orEmpty())
                    filter = filter?.apply {
                        this.filter = phoneNumber?.nationalNumber?.toString() ?: phone
                    }
                    val codeCountry = phoneNumber?.countryCode ?: countryCode?.countryCode
                    binding?.filterAddCountryCodeSpinner?.setSelection(countryCodeList?.indexOfFirst { it.countryCode == codeCountry }.orZero())
                }
                Log.e("filterAddTAG",
                    "BaseAddFragment ContactFilterAdapter contactClick contact $phone")
            }
            binding?.filterAddContactList?.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                this.adapter = contactAdapter
            }
        }
        setFragmentResultListener(Constants.CHANGE_FILTER) { _, _ ->
            binding?.filter?.filterType = if (binding?.filter?.isBlackFilter().isTrue()) WHITE_FILTER else BLACK_FILTER
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
                        if (binding?.filter?.isBlackFilter().isTrue()) R.drawable.ic_white_filter else R.drawable.ic_block)
                    setOnMenuItemClickListener {
                        findNavController().navigate(FilterAddFragmentDirections.startChangeFilterDialog(
                            args.filter))
                        return@setOnMenuItemClickListener true
                    }
                }
            }
        }
    }

    private fun existNumberChecking() {
        binding?.filterAddInput?.addTextChangedListener(DebouncingTextChangeListener(lifecycle) { input ->
            val filter = getFilterObject()
            Log.e("filterAddTAG",
                "BaseAddFragment addTextChangedListener it $input filter ${filter.filter} type ${filter.conditionType} isFromDb ${filter.isFromDb}")
            viewModel.checkFilterExist(filter)
            binding?.itemFilter?.filter = filter
            checkSubmitEnable(binding?.itemFilter?.filter?.name == getString(R.string.invalid_phone_number))
            filterContactList(input.toString())
        })
    }

    private fun getFilterObject(): Filter {
        Log.e("filterAddTAG",
            "BaseAddFragment getFilterObject before")
        val filter = Filter(filter = String.format("+%s%s",
            binding?.filterAddCountryCodeValue?.text,
            binding?.filterAddInput.inputText()),
            filterType = if (binding?.filter?.isBlackFilter().isTrue()) BLACK_FILTER else WHITE_FILTER,
            country = countryCode?.country.orEmpty(),
            conditionType = args.filter?.conditionType.orZero(),
            name = if(binding?.filterAddInput.inputText().isEmpty()) getString(R.string.invalid_phone_number) else if (binding?.filterAddInput.inputText().isValidPhoneNumber(countryCode?.country.orEmpty())) getString(R.string.condition_full) else getString(R.string.invalid_phone_number))
        Log.e("filterAddTAG",
            "BaseAddFragment getFilterObject after filter $filter")
        return filter
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
                    viewModel.insertFilter(getFilterObject())
                }
            }
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            existFilterLiveData.safeSingleObserve(viewLifecycleOwner) { isExist ->
                Log.e("filterAddTAG",
                    "BaseAddFragment observeLiveData existFilterLiveData isExist $isExist")
                binding?.filterAddSubmit?.text =
                    getString(if (isExist) R.string.delete_menu else R.string.add)
            }
            countryCodeListLiveData.safeSingleObserve(viewLifecycleOwner) { countryCodeList ->
                Log.e("filterAddTAG",
                    "BaseAddFragment observeLiveData countryCodeLiveData countryCodeList.size ${countryCodeList.size}")
                this@FilterAddFragment.countryCodeList = ArrayList(countryCodeList)
                setCountrySpinner()
            }
            queryContactListLiveData.safeSingleObserve(viewLifecycleOwner) { contactList ->
                //setContactByFilterList(contactList)
            }
            contactLiveData.safeSingleObserve(viewLifecycleOwner) { contactList ->
                Log.e("filterAddTAG",
                    "BaseAddFragment observeLiveData contactLiveData contactList.size ${contactList.size}")
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
                context?.contactListMap(contactList, binding?.filter?.isBlackFilter().isTrue()) ?: linkedMapOf()
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
            (contact.trimmedPhone).contains(searchQuery).isTrue()
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

    private fun setCountrySpinner() {
        countryCodeList?.add(CountryCode(flagEmoji = null))
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
                    countryCode = countryCodeList?.get(position)
                    binding?.filterAddCountryCodeValue?.text =
                        countryCode?.countryCode.toString()
                    binding?.itemFilter?.filter = getFilterObject()
                    checkSubmitEnable(binding?.itemFilter?.filter?.name == getString(R.string.invalid_phone_number))
                    Log.e("filterAddTAG",
                        "BaseAddFragment OnItemSelectedListener countryCode $countryCode itemFilter?.filter ${binding?.itemFilter?.filter}")
                }

                override fun onNothingSelected(p0: AdapterView<*>?) = Unit
            }
            val countryCodeIndex = countryCodeList?.indexOfFirst {
                it.country == context.getUserCountry()?.uppercase()
            }.orZero()
            countryCode = countryCodeList?.get(countryCodeIndex)
            setSelection(countryCodeIndex)
            viewModel.hideMainProgress()
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
