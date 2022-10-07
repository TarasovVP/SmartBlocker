package com.tarasovvp.blacklister.ui.main.filter_add

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.collection.ArrayMap
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.constants.Constants.BLACK_FILTER
import com.tarasovvp.blacklister.constants.Constants.COUNTRY_CODE_START
import com.tarasovvp.blacklister.constants.Constants.WHITE_FILTER
import com.tarasovvp.blacklister.databinding.FragmentFilterAddBinding
import com.tarasovvp.blacklister.enums.Condition
import com.tarasovvp.blacklister.extensions.*
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.model.HeaderDataItem
import com.tarasovvp.blacklister.model.Info
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.utils.DebouncingTextChangeListener
import com.tarasovvp.blacklister.utils.PhoneNumberUtil
import com.tarasovvp.blacklister.utils.setSafeOnClickListener
import java.util.*

open class FilterAddFragment :
    BaseFragment<FragmentFilterAddBinding, FilterAddViewModel>() {

    override var layoutId = R.layout.fragment_filter_add
    override val viewModelClass = FilterAddViewModel::class.java
    private val args: FilterAddFragmentArgs by navArgs()
    private var contactAdapter: ContactFilterAdapter? = null
    private var contactList: List<Contact>? = null

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
            contactAdapter = ContactFilterAdapter { contact ->
                binding?.apply {
                    filter = filter.apply {
                        this?.filter = contact.phone
                    }
                    this.itemContact.contact = contact
                }
                Log.e("filterAddTAG",
                    "BaseAddFragment ContactFilterAdapter contactClick contact $contact")
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
            } countryCodeKey ${PhoneNumberUtil.countryCodeKey(filter.filter)}")
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
                filterAddInput.addTextChangedListener(DebouncingTextChangeListener(lifecycle) {
                    Log.e("filterAddTAG",
                        "BaseAddFragment addTextChangedListener it $it filter ${getFilterObject().filter} type ${getFilterObject().conditionType} isFromDb ${getFilterObject().isFromDb}")
                    //viewModel.checkFilterExist(getFilterObject())
                    filterContactList(it.toString())
                })
            }
        }
    }

    private fun getFilterObject(): Filter {
        return Filter(filter = String.format("%s%s",
            binding?.filterAddCountryCodeValue?.text,
            binding?.filterAddInput.inputText()),
            filterType = if (isBlackFilter) BLACK_FILTER else WHITE_FILTER)
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
            countryCodeLiveData.safeSingleObserve(viewLifecycleOwner) { countryCodeMap ->
                PhoneNumberUtil.initCountryCodeMap(countryCodeMap)
                setCountrySpinner(countryCodeMap)
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

    fun filterContactList(searchQuery: String) {
         val filteredContactList = contactList?.filter { contact ->
            (contact.phone).contains(searchQuery).isTrue() }
        filteredContactList?.let {
            contactAdapter?.clearData()
            contactAdapter?.setHeaderAndData(it, HeaderDataItem())
        contactAdapter?.notifyDataSetChanged()}
    }

    private fun setCountrySpinner(countryCodeMap: ArrayMap<String, Int?>) {
        countryCodeMap[getString(R.string.no_country_code)] = null
        val countryAdapter = context?.let {
            ArrayAdapter(it,
                android.R.layout.simple_spinner_item,
                countryCodeMap.keys.toTypedArray())
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
                    binding?.filterAddCountryCodeValue?.text = if (countryCodeMap.valueAt(position)
                            .isNotNull()
                    ) String.format(COUNTRY_CODE_START,
                        countryCodeMap.valueAt(position)) else String.EMPTY
                }

                override fun onNothingSelected(p0: AdapterView<*>?) = Unit
            }
            setSelection(countryCodeMap.indexOfKey(if (PhoneNumberUtil.countryCodeKey(args.filter?.filter)
                    .isNotNull()
            ) PhoneNumberUtil.countryCodeKey(args.filter?.filter) else Locale(Locale.getDefault().language,
                context?.getUserCountry().orEmpty()).flagEmoji() + context?.getUserCountry()
                ?.uppercase()).orZero())
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
