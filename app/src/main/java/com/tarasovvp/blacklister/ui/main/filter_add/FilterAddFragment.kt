package com.tarasovvp.blacklister.ui.main.filter_add

import android.content.Context
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
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.constants.Constants.BLACK_FILTER
import com.tarasovvp.blacklister.constants.Constants.COUNTRY_CODE_START
import com.tarasovvp.blacklister.constants.Constants.DEFAULT_FILTER
import com.tarasovvp.blacklister.constants.Constants.WHITE_FILTER
import com.tarasovvp.blacklister.databinding.FragmentFilterAddBinding
import com.tarasovvp.blacklister.enums.Condition
import com.tarasovvp.blacklister.extensions.*
import com.tarasovvp.blacklister.model.*
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

open class FilterAddFragment :
    BaseFragment<FragmentFilterAddBinding, FilterAddViewModel>() {

    override var layoutId = R.layout.fragment_filter_add
    override val viewModelClass = FilterAddViewModel::class.java
    private val args: FilterAddFragmentArgs by navArgs()

    private var contactAdapter: ContactFilterAdapter? = null
    private var contactList: List<Contact>? = null
    private var countryCodeList: ArrayList<CountryCode>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivity).setMainProgressVisibility(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.filter = args.filter
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
        if (contactAdapter.isNull()) {
            contactAdapter = ContactFilterAdapter { phone ->
                binding?.apply {
                    val phoneNumber = phone.getPhoneNumber(binding?.filter?.countryCode?.country.orEmpty())
                    binding?.filterToInput = true
                    Log.e("filterAddTAG",
                        "BaseAddFragment ContactFilterAdapter contactClick phoneNumber $phoneNumber filterToInput ${binding?.filterToInput}")
                    if ((phoneNumber?.nationalNumber.toString() == filterAddInput.getRawText() && String.format(
                            COUNTRY_CODE_START, phoneNumber?.countryCode.toString()) == filterAddCountryCodeValue.text.toString()).not()) {
                        filter = filter?.apply {
                            this.filter = phoneNumber?.nationalNumber?.toString() ?: phone.digitsTrimmed()
                        }
                        filterAddCountryCodeSpinner.setSelection(countryCodeList?.indexOfFirst {
                            it.countryCode == if(phoneNumber?.countryCode.isNull()) binding?.filter?.countryCode?.countryCode else String.format(COUNTRY_CODE_START, phoneNumber?.countryCode.toString())
                        }.orZero())
                    }
                }
                Log.e("filterAddTAG",
                    "BaseAddFragment ContactFilterAdapter contactClick contact $phone filterToInput ${binding?.filterToInput}")
            }
            binding?.filterAddContactList?.adapter = contactAdapter
        }
    }

    private fun setFragmentResultListeners() {
        setFragmentResultListener(Constants.CHANGE_FILTER) { _, _ ->
            binding?.filter?.filterType =
                if (binding?.filter?.isBlackFilter().isTrue()) WHITE_FILTER else BLACK_FILTER
            binding?.filter?.let { viewModel.checkFilterExist(it) }
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
                title =
                    getString(Condition.getTitleByIndex(binding?.filter?.conditionType.orZero()))
                menu?.clear()
                inflateMenu(R.menu.toolbar_filter)
                menu?.findItem(R.id.filter_menu_item)?.apply {
                    icon = ContextCompat.getDrawable(context,
                        if (binding?.filter?.isBlackFilter()
                                .isTrue()
                        ) R.drawable.ic_white_filter else R.drawable.ic_block)
                    setOnMenuItemClickListener {
                        findNavController().navigate(FilterAddFragmentDirections.startChangeFilterDialog(
                            binding?.filter))
                        return@setOnMenuItemClickListener true
                    }
                }
            }
        }
    }

    private fun setFilterTextChangeListener() {
        binding?.filterAddInput?.doAfterTextChanged {
            binding?.filterToInput = false
            binding?.filter = binding?.filter?.apply {
                filter = if (this.isTypeFull()) binding?.filterAddInput?.getRawText().orEmpty() else binding?.filterAddInput.inputText()
                viewModel.checkFilterExist(this)
                filterContactList(this.filter)
            }
            Log.e("filterAddTAG",
                "BaseAddFragment MaskedEditText addTextChangedListener it $it getRawText ${binding?.filterAddInput?.getRawText().orEmpty()} filter ${binding?.filter?.filter} type ${binding?.filter?.conditionType} isFromDb ${binding?.filter?.isFromDb}")
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
                if (BlackListerApp.instance?.isLoggedInUser().isTrue() && BlackListerApp.instance?.isNetworkAvailable.isTrue().not()
                ) {
                    showMessage(getString(R.string.unavailable_network_repeat), true)
                } else {
                    if (filterAddSubmit.text == getString(R.string.delete_menu)) {
                        findNavController().navigate(FilterAddFragmentDirections.startDeleteFilterDialog(filter))
                    } else {
                        filter?.filter = filter?.addFilter().orEmpty()
                        filter?.let {  viewModel.insertFilter(it) }
                    }
                }
            }
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            existFilterLiveData.safeSingleObserve(viewLifecycleOwner) { filterType ->
                Log.e("filterAddTAG",
                    "BaseAddFragment observeLiveData existFilterLiveData filterType $filterType")
                val buttonText = when (filterType) {
                    DEFAULT_FILTER -> R.string.add
                    binding?.filter?.filterType -> R.string.delete_menu
                    else -> R.string.change
                }

                binding?.filterAddSubmit?.text = getString(buttonText)
            }
            countryCodeListLiveData.safeSingleObserve(viewLifecycleOwner) { countryCodeList ->
                Log.e("filterAddTAG",
                    "BaseAddFragment observeLiveData countryCodeLiveData countryCodeList.size ${countryCodeList.size}")
                this@FilterAddFragment.countryCodeList = ArrayList(countryCodeList)
                setCountrySpinner()
            }
            contactLiveData.safeSingleObserve(viewLifecycleOwner) { contactList ->
                Log.e("filterAddTAG",
                    "BaseAddFragment observeLiveData contactLiveData contactList.size ${contactList.size}")
                this@FilterAddFragment.contactList = contactList
                contactAdapter?.setHeaderAndData(contactList, HeaderDataItem())
                viewModel.hideMainProgress()
            }
            insertFilterLiveData.safeSingleObserve(viewLifecycleOwner) { number ->
                handleSuccessFilterAction(String.format(getString(R.string.filter_added), number))
            }
            deleteFilterLiveData.safeSingleObserve(viewLifecycleOwner) {
                handleSuccessFilterAction(String.format(getString(R.string.delete_filter_from_list),
                    binding?.filter?.filter.orEmpty()))
            }
        }
    }

    private fun filterContactList(searchQuery: String) {
        val filteredContactList = contactList?.filter { contact ->
            (contact.trimmedPhone).contains(searchQuery).isTrue()
        }
        contactAdapter?.searchQuery = searchQuery
        filteredContactList?.let { contactList ->
            contactAdapter?.clearData()
            contactAdapter?.setHeaderAndData(contactList, HeaderDataItem())
            contactAdapter?.notifyDataSetChanged()
        }
        binding?.filterAddContactList?.isVisible = filteredContactList.isNullOrEmpty().not()
        binding?.filterAddEmptyList?.emptyStateContainer?.isVisible =
            filteredContactList.isNullOrEmpty()
        binding?.filterAddEmptyList?.emptyStateTitle?.text =
            getString(R.string.no_result_with_list_query)
        Log.e("filterAddTAG",
            "BaseAddFragment filterContactList filteredContactList?.size ${filteredContactList?.size}")
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
                        binding?.filterAddInput?.setNumberMask(binding?.filter?.conditionTypeFullHint().orEmpty())
                    }
                    Log.e("filterAddTAG",
                        "BaseAddFragment OnItemSelectedListener countryCode ${binding?.filter?.countryCode} itemFilter?.filter ${binding?.itemFilter?.filter}")
                }

                override fun onNothingSelected(p0: AdapterView<*>?) = Unit
            }
            val countryCodeIndex = countryCodeList?.indexOfFirst {
                it.country == if (binding?.filter?.isTypeContain().isTrue()) String.EMPTY else context.getUserCountry()?.uppercase()
            }.orZero()
            setSelection(countryCodeIndex)
        }
    }

    private fun handleSuccessFilterAction(message: String) {
        (activity as MainActivity).apply {
            showMessage(message, false)
            getAllData()
        }
        findNavController().popBackStack()
    }
}
