package com.tarasovvp.blacklister.ui.main.filter_add

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ExpandableListView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.constants.Constants.BLACK_FILTER
import com.tarasovvp.blacklister.constants.Constants.COUNTRY_CODE_START
import com.tarasovvp.blacklister.constants.Constants.WHITE_FILTER
import com.tarasovvp.blacklister.databinding.FragmentFilterAddBinding
import com.tarasovvp.blacklister.extensions.*
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.model.*
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

    private var isBlackFilter: Boolean = true
    private var contactByFilterAdapter: ContactByFilterAdapter? = null
    private var contactByFilterList: ExpandableListView? = null
    private val countryCodeMap = PhoneNumberUtil.countryCodeMap

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("filterLifeCycleTAG", "FilterAddFragment onViewCreated")
        Log.e("filterAddTAG",
            "BaseAddFragment onViewCreated filter ${args.filter?.filter} type ${args.filter?.conditionType} isFromDb ${args.filter?.isFromDb} ")
        isBlackFilter = args.filter?.isBlackFilter().isTrue()
        setToolbar()
        setClickListeners()
        setCountrySpinner()
        args.filter?.let { viewModel.checkFilterExist(it) }
        setFragmentResultListener(Constants.CHANGE_FILTER) { _, _ ->
            Log.e("filterAddTAG",
                "BaseAddFragment setFragmentResultListener getFilterObject() ${getFilterObject()}")
            isBlackFilter = isBlackFilter.not()
            initViewsWithData(getFilterObject())
            setToolbar()
        }
        setFragmentResultListener(Constants.DELETE_FILTER) { _, _ ->
            args.filter?.let {
                viewModel.deleteFilter(it)
            }
        }
    }

    private fun setToolbar() {
        (activity as MainActivity).apply {
            toolbar?.apply {
                title = if (isBlackFilter) getString(R.string.black_list) else getString(
                    R.string.white_list)
                menu?.clear()
                inflateMenu(R.menu.toolbar_filter)
                menu?.findItem(R.id.filter_menu_item)?.apply {
                    icon = ContextCompat.getDrawable(context,
                        if (isBlackFilter) R.drawable.ic_white_filter else R.drawable.ic_black_filter)
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
            filterAddTitle.text = if (filterAddInput.inputText()
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
            binding?.root?.post {
                filterAddInput.addTextChangedListener(DebouncingTextChangeListener(lifecycle) {
                    Log.e("filterAddTAG",
                        "BaseAddFragment addTextChangedListener it $it filter ${getFilterObject().filter} type ${getFilterObject().conditionType} isFromDb ${getFilterObject().isFromDb}")
                    viewModel.checkFilterExist(getFilterObject())
                })
                typeRadioGroup.setOnCheckedChangeListener { _, checkedRadioButtonId ->
                    Log.e("filterAddTAG",
                        "BaseAddFragment setOnCheckedChangeListener filter ${getFilterObject().filter} type ${getFilterObject().conditionType} isFromDb ${getFilterObject().isFromDb}")
                    if (checkedRadioButtonId == R.id.filter_type_contain) {
                        filterAddCountryCodeValue.text = String.EMPTY
                    } else {
                        filterAddCountryCodeSpinner.setSelection(countryCodeMap?.indexOfKey(if (PhoneNumberUtil.countryCodeKey(
                                filter?.filter).isNotNull()
                        ) PhoneNumberUtil.countryCodeKey(args.filter?.filter) else Locale(Locale.getDefault().language,
                            context?.getUserCountry().orEmpty()).flagEmoji() + context?.getUserCountry()
                            ?.uppercase()).orZero())
                    }
                    viewModel.checkFilterExist(getFilterObject())
                }
            }
        }
    }

    private fun getFilterObject(): Filter {
        return Filter(filter = String.format("%s%s",
            binding?.filterAddCountryCodeValue?.text,
            binding?.filterAddInput.inputText()), filterType = if (isBlackFilter) BLACK_FILTER else WHITE_FILTER, conditionType =
        binding?.typeRadioGroup?.indexOfChild(binding?.typeRadioGroup?.findViewById(binding?.typeRadioGroup?.checkedRadioButtonId.orZero()))
            .orZero())
    }

    private fun setClickListeners() {
        binding?.apply {
            filterAddConditionsInfo.setSafeOnClickListener {
                filterAddConditionsInfo.showPopUpWindow(Info(title = getString(R.string.add_conditions_title),
                    description = getString(R.string.add_conditions_info),
                    icon = R.drawable.ic_test))
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
                    "BaseAddFragment observeLiveData existFilterLiveData filter $filter")
                initViewsWithData(filter)
            }
            queryContactListLiveData.safeSingleObserve(viewLifecycleOwner) { contactList ->
                var filterAddInfoText = ""
                contactList.filterNot {
                    if (isBlackFilter) it.isWhiteFilter() && SharedPreferencesUtil.whiteListPriority else it.isBlackFilter() && SharedPreferencesUtil.whiteListPriority.not()
                }.apply {
                    if (this.isNotEmpty()) filterAddInfoText += String.format(getString(R.string.block_add_info),
                        if (isBlackFilter) getString(R.string.can_block) else getString(
                            R.string.can_unblock),
                        this.size)
                }
                contactList.filter {
                    if (isBlackFilter) it.isWhiteFilter() && SharedPreferencesUtil.whiteListPriority else it.isBlackFilter() && SharedPreferencesUtil.whiteListPriority.not()
                }.apply {
                    if (filterAddInfoText.isNotEmpty()) filterAddInfoText += "\n"
                    if (this.isNotEmpty()) filterAddInfoText += String.format(getString(R.string.not_block_add_info),
                        if (isBlackFilter) getString(R.string.can_block) else getString(
                            R.string.can_unblock),
                        this.size)
                }
                setContactByFilterList(contactList)
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
        val title =
            String.format(getString(R.string.contact_by_filter_list_title), contactList.size)
        contactByFilterList?.isEnabled = contactList.isNotEmpty()

        val titleList = arrayListOf(title)
        val contactListMap = hashMapOf(title to listOf<Contact>())

        val affectedContactList = contactList.filterNot {
            if (isBlackFilter) it.isWhiteFilter() && SharedPreferencesUtil.whiteListPriority else it.isBlackFilter() && SharedPreferencesUtil.whiteListPriority.not()
        }
        if (affectedContactList.isNotEmpty()) {
            val affectedContacts =
                "Найдено ${affectedContactList.size} контактов, которые будут ${if (isBlackFilter) "заблокированы" else "разблокированы"} этим фильтром"
            titleList.add(affectedContacts)
            contactListMap[affectedContacts] = affectedContactList
        }

        val nonAffectedContactList = contactList.filter {
            if (isBlackFilter) it.isWhiteFilter() && SharedPreferencesUtil.whiteListPriority else it.isBlackFilter() && SharedPreferencesUtil.whiteListPriority.not()
        }
        if (nonAffectedContactList.isNotEmpty()) {
            val nonAffectedContacts = String.format(getString(R.string.not_block_add_info),
                if (isBlackFilter) getString(R.string.can_block) else getString(
                    R.string.can_unblock),
                nonAffectedContactList.size)
            titleList.add(nonAffectedContacts)
            contactListMap[nonAffectedContacts] = nonAffectedContactList
        }
        contactByFilterAdapter = ContactByFilterAdapter(arrayListOf(title), contactListMap)
        contactByFilterList?.setAdapter(contactByFilterAdapter)
        contactByFilterList?.setOnGroupClickListener { expandableListView, _, groupPosition, _ ->
            if (groupPosition == 0 && contactList.isNotEmpty()) {
                binding?.scrollContainer?.isVisible = expandableListView.isGroupExpanded(0)
                if (expandableListView.isGroupExpanded(0)) {
                    contactByFilterAdapter?.titleList = arrayListOf(title)
                    contactByFilterAdapter?.contactListMap = hashMapOf()
                } else {
                    contactByFilterAdapter?.titleList = titleList
                    contactByFilterAdapter?.contactListMap = contactListMap
                }
            }
            return@setOnGroupClickListener false
        }
        contactByFilterList?.setOnChildClickListener { _, _, _, childPosition, _ ->
            findNavController().navigate(FilterAddFragmentDirections.startContactDetailFragment(
                number = contactList[childPosition].phone))
            return@setOnChildClickListener contactList.isEmpty()
        }
    }

    private fun handleSuccessFilterAction(message: String) {
        (activity as MainActivity).apply {
            showMessage(message, false)
            getAllData()
        }
        findNavController().popBackStack()
    }

    private fun setCountrySpinner() {
        countryCodeMap?.put(getString(R.string.no_country_code), null)
        val countryAdapter = context?.let {
            ArrayAdapter(it,
                android.R.layout.simple_spinner_item,
                countryCodeMap?.keys.orEmpty().toTypedArray())
        }
        binding?.filterAddCountryCodeSpinner?.adapter = countryAdapter
        binding?.filterAddCountryCodeSpinner?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    spinner: AdapterView<*>?,
                    tv: View?,
                    position: Int,
                    id: Long,
                ) {
                    binding?.filterAddCountryCodeValue?.text =
                        if (countryCodeMap?.valueAt(position).isNotNull()) String.format(
                            COUNTRY_CODE_START,
                            countryCodeMap?.valueAt(position)) else String.EMPTY
                }

                override fun onNothingSelected(p0: AdapterView<*>?) = Unit
            }
        binding?.filterAddCountryCodeSpinner?.setSelection(countryCodeMap?.indexOfKey(if (PhoneNumberUtil.countryCodeKey(
                args.filter?.filter).isNotNull()
        ) PhoneNumberUtil.countryCodeKey(args.filter?.filter) else Locale(Locale.getDefault().language,
            context?.getUserCountry().orEmpty()).flagEmoji() + context?.getUserCountry()
            ?.uppercase()).orZero())
    }
}
