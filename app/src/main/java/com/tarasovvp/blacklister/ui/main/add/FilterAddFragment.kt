package com.tarasovvp.blacklister.ui.main.add

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.DELETE_NUMBER
import com.tarasovvp.blacklister.databinding.FragmentFilterAddBinding
import com.tarasovvp.blacklister.extensions.*
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.model.*
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.utils.DebouncingTextChangeListener
import com.tarasovvp.blacklister.utils.setSafeOnClickListener


class FilterAddFragment(private var filter: Filter?) :
    BaseFragment<FragmentFilterAddBinding, AddViewModel>() {

    override var layoutId = R.layout.fragment_filter_add

    override val viewModelClass = AddViewModel::class.java

    private var contactByFilterAdapter: ContactByFilterAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.filter = filter
        initViewsWithData(filter)
        setCheckChangeListeners(filter)
        setExistNumberChecking()
        setClickListeners()
        setFragmentResultListener(DELETE_NUMBER) { _, _ ->
            filter?.let {
                viewModel.deleteFilter(it)
            }
        }
    }

    private fun setExistNumberChecking() {
        viewModel.checkFilterExist(filter?.filter.orEmpty(),
            filter?.isBlackFilter.isTrue())
        binding?.filterAddInput?.addTextChangedListener(DebouncingTextChangeListener(lifecycle) {
            viewModel.checkFilterExist(it.toString(), filter?.isBlackFilter.isTrue())
        })
    }

    private fun initViewsWithData(filter: Filter?) {
        binding?.apply {
            filterAddTitle.text =
                if (filterAddInput.text.isEmpty()) getString(R.string.add_filter_message) else String.format(
                    if (filter?.isFromDb.isTrue() && filter.isNotNull()) getString(R.string.edit_filter_with_filter_message) else getString(
                        R.string.add_filter_with_filter_message),
                    filterAddInput.text)
            if (filterAddInput.text.isNotEmpty() && filter?.isFilterIdentical(binding?.filterAddStart?.isChecked.isTrue(), binding?.filterAddContain?.isChecked.isTrue(), binding?.filterAddEnd?.isChecked.isTrue()).isTrue().not()) {
                viewModel.checkContactListByFilter(getFilterObject())
            }

        }
    }

    private fun setCheckChangeListeners(filter: Filter?) {
        binding?.apply {
            val checkChangeListener = CompoundButton.OnCheckedChangeListener { _, _ ->
                filterAddSubmit.isVisible =
                    filterAddInput.text.isNotEmpty() && filter?.isFilterIdentical(binding?.filterAddStart?.isChecked.isTrue(), binding?.filterAddContain?.isChecked.isTrue(), binding?.filterAddEnd?.isChecked.isTrue()).isTrue().not()
                if (filterAddInput.text.isNotEmpty() && filter?.isFilterIdentical(binding?.filterAddStart?.isChecked.isTrue(), binding?.filterAddContain?.isChecked.isTrue(), binding?.filterAddEnd?.isChecked.isTrue()).isTrue().not()) {
                    viewModel.checkContactListByFilter(getFilterObject())
                }
            }
            container.getViewsFromLayout(CheckBox::class.java).forEach { checkBox ->
                checkBox.setOnCheckedChangeListener(checkChangeListener)
            }
        }
    }

    private fun setClickListeners() {
        binding?.apply {
            filterAddConditionsInfo.setSafeOnClickListener {
                filterAddConditionsInfo.showPopUpWindow(Info(title = getString(R.string.add_conditions_title),
                    description = getString(R.string.add_conditions_info),
                    icon = R.drawable.ic_test))
            }
            filterDeleteSubmit.setSafeOnClickListener {
                filter?.let {
                    findNavController().navigate(AddFragmentDirections.startDeleteFilterDialog(
                        filter = it))
                }
            }
            filterAddSubmit.setSafeOnClickListener {
                viewModel.insertFilter(getFilterObject())
            }
        }
    }

    private fun getFilterObject(): Filter {
        val filter = if (filter?.isBlackFilter.isTrue()) {
            BlackFilter(filter = binding?.filterAddInput?.text.toString())
        } else {
            WhiteFilter(filter = binding?.filterAddInput?.text.toString())
        }
        filter.apply {
            start = binding?.filterAddStart?.isChecked.isTrue()
            contain = binding?.filterAddContain?.isChecked.isTrue()
            end = binding?.filterAddEnd?.isChecked.isTrue()
            isBlackFilter = filter.isBlackFilter.isTrue()
        }
        return filter
    }

    private fun setContactByFilterList(contactList: List<Contact>) {
        val title =
            String.format(getString(R.string.contact_by_filter_list_title), contactList.size)
        binding?.filterAddContactByFilterList?.isEnabled = contactList.isNotEmpty()

        val titleList = arrayListOf(title)
        val contactListMap = hashMapOf(title to listOf<Contact>())

        val affectedContactList = contactList.filterNot {
            if (filter?.isBlackFilter.isTrue()) it.isWhiteFilter && SharedPreferencesUtil.isWhiteListPriority else it.isBlackFilter && SharedPreferencesUtil.isWhiteListPriority.not()
        }
        if (affectedContactList.isNotEmpty()) {
            val affectedContacts =
                "Найдено ${affectedContactList.size} контактов, которые будут ${if (filter?.isBlackFilter.isTrue()) "заблокированы" else "разблокированы"} этим фильтром"
            titleList.add(affectedContacts)
            contactListMap[affectedContacts] = affectedContactList
        }

        val nonAffectedContactList = contactList.filter {
            if (filter?.isBlackFilter.isTrue()) it.isWhiteFilter && SharedPreferencesUtil.isWhiteListPriority else it.isBlackFilter && SharedPreferencesUtil.isWhiteListPriority.not()
        }
        if (nonAffectedContactList.isNotEmpty()) {
            val nonAffectedContacts = String.format(getString(R.string.not_block_add_info),
                if (filter?.isBlackFilter.isTrue()) getString(R.string.can_block) else getString(
                    R.string.can_unblock),
                nonAffectedContactList.size)
            titleList.add(nonAffectedContacts)
            contactListMap[nonAffectedContacts] = nonAffectedContactList
        }
        contactByFilterAdapter = ContactByFilterAdapter(arrayListOf(title), contactListMap)
        binding?.filterAddContactByFilterList?.setAdapter(contactByFilterAdapter)
        binding?.filterAddContactByFilterList?.setOnGroupClickListener { expandableListView, _, groupPosition, _ ->
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
        binding?.filterAddContactByFilterList?.setOnChildClickListener { _, _, _, childPosition, _ ->
            findNavController().navigate(AddFragmentDirections.startContactDetailFragment(
                phone = contactList[childPosition].phone))
            return@setOnChildClickListener contactList.isEmpty()
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            existFilterLiveData.observe(viewLifecycleOwner) { filter ->
                filter?.isFromDb = true
                filter?.isFilterIdentical = filter?.isFilterIdentical(binding?.filterAddStart?.isChecked.isTrue(), binding?.filterAddContain?.isChecked.isTrue(), binding?.filterAddEnd?.isChecked.isTrue()).isTrue()
                initViewsWithData(filter)
                setCheckChangeListeners(filter)
            }
            queryContactListLiveData.safeSingleObserve(viewLifecycleOwner) { contactList ->
                var filterAddInfoText = ""
                contactList.filterNot {
                    if (filter?.isBlackFilter.isTrue()) it.isWhiteFilter && SharedPreferencesUtil.isWhiteListPriority else it.isBlackFilter && SharedPreferencesUtil.isWhiteListPriority.not()
                }.apply {
                    if (this.isNotEmpty()) filterAddInfoText += String.format(getString(R.string.block_add_info),
                        if (filter?.isBlackFilter.isTrue()) getString(R.string.can_block) else getString(
                            R.string.can_unblock),
                        this.size)
                }
                contactList.filter {
                    if (filter?.isBlackFilter.isTrue()) it.isWhiteFilter && SharedPreferencesUtil.isWhiteListPriority else it.isBlackFilter && SharedPreferencesUtil.isWhiteListPriority.not()
                }.apply {
                    if (filterAddInfoText.isNotEmpty()) filterAddInfoText += "\n"
                    if (this.isNotEmpty()) filterAddInfoText += String.format(getString(R.string.not_block_add_info),
                        if (filter?.isBlackFilter.isTrue()) getString(R.string.can_block) else getString(
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
                    filter?.filter.orEmpty()))
            }
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