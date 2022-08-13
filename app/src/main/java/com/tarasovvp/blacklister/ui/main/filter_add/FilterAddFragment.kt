package com.tarasovvp.blacklister.ui.main.filter_add

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.BLACK_LIST_PREVIEW
import com.tarasovvp.blacklister.constants.Constants.DELETE_NUMBER
import com.tarasovvp.blacklister.databinding.FragmentFilterAddBinding
import com.tarasovvp.blacklister.extensions.*
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.model.*
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.utils.DebouncingTextChangeListener
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class FilterAddFragment : BaseFragment<FragmentFilterAddBinding, FilterAddViewModel>() {

    override fun getViewBinding() = FragmentFilterAddBinding.inflate(layoutInflater)

    override val viewModelClass = FilterAddViewModel::class.java

    private val args: FilterAddFragmentArgs by navArgs()

    private var contactByFilterAdapter: ContactByFilterAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).toolbar?.title =
            if (args.filter?.isBlackFilter.isTrue()) getString(R.string.black_list) else getString(R.string.white_list)
        binding?.filterAddInput?.setText(args.filter?.filter.orEmpty())
        binding?.filterAddIcon?.setImageResource(if (args.filter?.isBlackFilter.isTrue()) R.drawable.ic_black_filter else R.drawable.ic_white_filter)
        setPriority()
        initViewsWithData(args.filter, false)
        setExistNumberChecking()
        setClickListeners()
        setFragmentResultListener(DELETE_NUMBER) { _, _ ->
            args.filter?.let {
                viewModel.deleteFilter(it)
            }
        }
        setFragmentResultListener(BLACK_LIST_PREVIEW) { _, _ ->
            viewModel.insertFilter(getFilter())
        }
    }

    private fun setPriority() {
        binding?.filterAddPriority?.setCompoundDrawablesWithIntrinsicBounds(0,
            0,
            if (SharedPreferencesUtil.isWhiteListPriority) R.drawable.ic_white_filter else R.drawable.ic_black_filter,
            0)
        binding?.filterAddPriority?.setSafeOnClickListener {
            findNavController().navigate(FilterAddFragmentDirections.startBlockSettingsFragment())
        }
    }

    private fun setExistNumberChecking() {
        viewModel.checkFilterExist(args.filter?.filter.orEmpty(),
            args.filter?.isBlackFilter.isTrue())
        binding?.filterAddInput?.addTextChangedListener(DebouncingTextChangeListener(lifecycle) {
            viewModel.checkFilterExist(it.toString(), args.filter?.isBlackFilter.isTrue())
        })
    }

    private fun initViewsWithData(filter: Filter?, fromDb: Boolean) {
        binding?.apply {
            filterAddStart.isChecked = filter?.start.isTrue()
            filterAddContain.isChecked = filter?.contain.isTrue()
            filterAddEnd.isChecked = filter?.end.isTrue()
            filterAddTitle.text =
                if (filterAddInput.text.isEmpty()) getString(R.string.add_filter_message) else String.format(
                    if (fromDb && filter.isNotNull()) getString(R.string.edit_filter_with_filter_message) else getString(
                        R.string.add_filter_with_filter_message),
                    filterAddInput.text)
            filterAddSubmit.isVisible =
                filterAddInput.text.isNotEmpty() && isFilterIdentical(fromDb, filter).not()
            filterAddSubmit.text =
                if (fromDb && filter.isNotNull()) getString(R.string.edit) else getString(R.string.add)
            filterAddSubmit.isVisible = filterAddInput.text.isNotEmpty() && filter.isNotNull()
            if (filterAddInput.text.isNotEmpty() && isFilterIdentical(fromDb, filter).not()) {
                viewModel.checkContactListByFilter(getFilter())
            }
            setCheckChangeListeners(fromDb, filter)
        }
    }

    private fun setCheckChangeListeners(fromDb: Boolean, filter: Filter?) {
        binding?.apply {
            val checkChangeListener = CompoundButton.OnCheckedChangeListener { _, _ ->
                filterAddSubmit.isVisible =
                    filterAddInput.text.isNotEmpty() && isFilterIdentical(fromDb, filter).not()
                if (filterAddInput.text.isNotEmpty() && isFilterIdentical(fromDb, filter).not()) {
                    viewModel.checkContactListByFilter(getFilter())
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
                args.filter?.let {
                    findNavController().navigate(FilterAddFragmentDirections.startDeleteFilterDialog(
                        filter = it))
                }
            }
            filterAddSubmit.setSafeOnClickListener {
                viewModel.insertFilter(getFilter())
            }
        }
    }

    private fun isFilterIdentical(fromDb: Boolean, filter: Filter?): Boolean {
        return fromDb && filter.isNotNull() && binding?.filterAddStart?.isChecked == filter?.start && binding?.filterAddContain?.isChecked == filter?.contain && binding?.filterAddEnd?.isChecked == filter?.end
    }

    private fun getFilter(): Filter {
        val filter = if (args.filter?.isBlackFilter.isTrue()) {
            BlackFilter(filter = binding?.filterAddInput?.text.toString())
        } else {
            WhiteFilter(filter = binding?.filterAddInput?.text.toString())
        }
        filter.apply {
            start = binding?.filterAddStart?.isChecked.isTrue()
            contain = binding?.filterAddContain?.isChecked.isTrue()
            end = binding?.filterAddEnd?.isChecked.isTrue()
            isBlackFilter = args.filter?.isBlackFilter.isTrue()
        }
        return filter
    }

    private fun setContactByFilterList(contactList: List<Contact>) {
        val title = String.format(getString(R.string.contact_by_filter_list_title), contactList.size)
        val affectedContacts = "Найдено ${contactList.size} контактов, которые будут ${if (args.filter?.isBlackFilter.isTrue()) "заблокированы" else "разблокированы"} этим фильтром"
        val nonAffectedList = contactList.filter {
            if (args.filter?.isBlackFilter.isTrue()) it.isWhiteFilter && SharedPreferencesUtil.isWhiteListPriority else it.isBlackFilter && SharedPreferencesUtil.isWhiteListPriority.not()
        }
        val nonAffectedContacts = String.format(getString(R.string.not_block_add_info), if (args.filter?.isBlackFilter.isTrue()) getString(R.string.can_block) else getString(R.string.can_unblock), nonAffectedList.size)
        contactByFilterAdapter =
            ContactByFilterAdapter(arrayListOf(title), hashMapOf())
        binding?.filterAddContactByFilterList?.setAdapter(contactByFilterAdapter)
        binding?.filterAddContactByFilterList?.isEnabled = contactList.isNotEmpty()
        binding?.filterAddContactByFilterList?.setOnGroupClickListener { expandableListView, _, groupPosition, _ ->
            Log.e("expandTAG", "FilterAddFragment setOnGroupClickListener contactList.isNotEmpty() ${contactList.isNotEmpty()} expandableListView.isGroupExpanded(0) ${expandableListView.isGroupExpanded(0)}")
            Log.e("expandTAG", "FilterAddFragment setOnGroupClickListener expandableListView.isGroupExpanded(0).not() ${expandableListView.isGroupExpanded(0).not()}")
            if (groupPosition == 0 && contactList.isNotEmpty()) {
                binding?.scrollContainer?.isVisible = expandableListView.isGroupExpanded(0)
                contactByFilterAdapter =
                    if (contactList.isNotEmpty() && expandableListView.isGroupExpanded(0).not()) {
                        ContactByFilterAdapter(arrayListOf(title, affectedContacts), hashMapOf(title to arrayListOf(), affectedContacts to contactList))
                    } else {
                        ContactByFilterAdapter(arrayListOf(title), hashMapOf())
                    }
                binding?.filterAddContactByFilterList?.setAdapter(contactByFilterAdapter)
            }
            return@setOnGroupClickListener false
        }
        binding?.filterAddContactByFilterList?.setOnChildClickListener { _, _, _, childPosition, _ ->
            findNavController().navigate(FilterAddFragmentDirections.startContactDetailFragment(
                phone = contactList[childPosition].phone))
            return@setOnChildClickListener contactList.isEmpty()
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            existFilterLiveData.observe(viewLifecycleOwner) { filter ->
                initViewsWithData(filter, true)
            }
            queryContactListLiveData.safeSingleObserve(viewLifecycleOwner) { contactList ->
                var filterAddInfoText = ""
                contactList.filterNot {
                    if (args.filter?.isBlackFilter.isTrue()) it.isWhiteFilter && SharedPreferencesUtil.isWhiteListPriority else it.isBlackFilter && SharedPreferencesUtil.isWhiteListPriority.not()
                }.apply {
                    if (this.isNotEmpty()) filterAddInfoText += String.format(getString(R.string.block_add_info),
                        if (args.filter?.isBlackFilter.isTrue()) getString(R.string.can_block) else getString(
                            R.string.can_unblock),
                        this.size)
                }
                contactList.filter {
                    if (args.filter?.isBlackFilter.isTrue()) it.isWhiteFilter && SharedPreferencesUtil.isWhiteListPriority else it.isBlackFilter && SharedPreferencesUtil.isWhiteListPriority.not()
                }.apply {
                    if (filterAddInfoText.isNotEmpty()) filterAddInfoText += "\n"
                    if (this.isNotEmpty()) filterAddInfoText += String.format(getString(R.string.not_block_add_info),
                        if (args.filter?.isBlackFilter.isTrue()) getString(R.string.can_block) else getString(
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

    private fun handleSuccessFilterAction(message: String) {
        (activity as MainActivity).apply {
            showMessage(message, false)
            getAllData()
        }
        findNavController().popBackStack()
    }

}