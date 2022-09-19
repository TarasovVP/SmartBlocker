package com.tarasovvp.blacklister.ui.main.add

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.DELETE_FILTER
import com.tarasovvp.blacklister.extensions.*
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.model.*
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.utils.DebouncingTextChangeListener
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

abstract class BaseAddFragment<B : ViewDataBinding>(private var filter: Filter?) :
    BaseFragment<B, AddViewModel>() {

    override var layoutId = R.layout.fragment_full_number_add
    override val viewModelClass = AddViewModel::class.java

    abstract fun initViews()

    private var contactByFilterAdapter: ContactByFilterAdapter? = null
    protected var title: TextView? = null
    protected var icon: ImageView? = null
    protected var filterInput: EditText? = null
    protected var submitButton: Button? = null
    protected var contactByFilterList: ExpandableListView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initViewsWithData(filter, false)
        setExistNumberChecking()
        setClickListeners()
        setFragmentResultListener(DELETE_FILTER) { _, _ ->
            filter?.let {
                viewModel.deleteFilter(it)
            }
        }
    }

    private fun setExistNumberChecking() {
        viewModel.checkFilterExist(filter?.filter.orEmpty(), filter?.isBlackFilter.isTrue())
        filterInput?.addTextChangedListener(DebouncingTextChangeListener(lifecycle) {
            viewModel.checkFilterExist(it.toString(), filter?.isBlackFilter.isTrue())
        })
    }

    private fun initViewsWithData(filter: Filter?, fromDb: Boolean) {
        filterInput?.setText(if (filter?.filter.orEmpty()
                .isValidPhoneNumber(context?.getUserCountry().orEmpty())
        ) filter?.nationalNumber(context?.getUserCountry().orEmpty()) else filter?.filter.orEmpty())
        icon?.setImageResource(if (filter?.isBlackFilter.isTrue()) R.drawable.ic_black_filter else R.drawable.ic_white_filter)
        if (this is FilterAddFragment) {
            title?.text = if (filterInput?.text?.toString().orEmpty().isEmpty()) getString(R.string.add_filter_message) else String.format(
                if (fromDb && filter.isNotNull()) getString(R.string.edit_filter_with_filter_message) else getString(
                    R.string.add_filter_with_filter_message), filterInput?.text)
        } else {
            filterInput?.setText(if (filter?.filter.orEmpty().isValidPhoneNumber(context?.getUserCountry().orEmpty())) filter?.nationalNumber(context?.getUserCountry().orEmpty()) else filter?.filter.orEmpty())
            title?.text = if (filterInput?.text?.toString().orEmpty().isEmpty()) getString(R.string.add_filter_message) else String.format(
                if (fromDb && filter.isNotNull()) getString(R.string.edit_filter_with_filter_message) else getString(
                    R.string.add_filter_with_filter_message), filterInput?.text)
        }
        submitButton?.isVisible =
            filterInput?.text?.toString().orEmpty().isNotEmpty()
        submitButton?.text =
            if (fromDb && filter.isNotNull()) getString(R.string.edit) else getString(R.string.add)
        if (submitButton?.text?.toString().orEmpty().isNotEmpty()) {
            viewModel.checkContactListByFilter(getFilter())
        }
    }

    private fun setClickListeners() {
        binding?.apply {
            /*fullNumberAddConditionsInfo.setSafeOnClickListener {
                fullNumberAddConditionsInfo.showPopUpWindow(Info(title = getString(R.string.add_conditions_title),
                    description = getString(R.string.add_conditions_info),
                    icon = R.drawable.ic_test))
            }*/
            //TODO implement button click
            submitButton?.setSafeOnClickListener {
                filter?.let {
                    findNavController().navigate(ContainerAddFragmentDirections.startDeleteFilterDialog(
                        filter = it))
                }
            }
            submitButton?.setSafeOnClickListener {
                viewModel.insertFilter(getFilter())
            }
        }
    }


    private fun getFilter(): Filter {
        val filter = if (filter?.isBlackFilter.isTrue()) {
            BlackFilter(filter = filterInput?.text.toString())
        } else {
            WhiteFilter(filter = filterInput?.text.toString())
        }
        return filter
    }

    override fun observeLiveData() {
        with(viewModel) {
            existFilterLiveData.observe(viewLifecycleOwner) { filter ->
                initViewsWithData(filter, true)
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

    private fun setContactByFilterList(contactList: List<Contact>) {
        val title =
            String.format(getString(R.string.contact_by_filter_list_title), contactList.size)
        contactByFilterList?.isEnabled = contactList.isNotEmpty()

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
        contactByFilterList?.setAdapter(contactByFilterAdapter)
        contactByFilterList?.setOnGroupClickListener { expandableListView, _, groupPosition, _ ->
            if (groupPosition == 0 && contactList.isNotEmpty()) {
                //binding?.scrollContainer?.isVisible = expandableListView.isGroupExpanded(0)
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
            findNavController().navigate(ContainerAddFragmentDirections.startContactDetailFragment(
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

}