package com.tarasovvp.blacklister.ui.main.filter_detail

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.FragmentFilterDetailBinding
import com.tarasovvp.blacklister.extensions.isNotNull
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.model.BlockedCall
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.ui.main.contact_detail.ContactDetailAdapter
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class FilterDetailFragment : BaseFragment<FragmentFilterDetailBinding, FilterDetailViewModel>() {

    override var layoutId = R.layout.fragment_filter_detail
    override val viewModelClass = FilterDetailViewModel::class.java

    private val args: FilterDetailFragmentArgs by navArgs()

    private var expandableFilterAdapter: ContactDetailAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.filter = args.filter

    }

    private fun setFilterListView(phone: String) {
        if (expandableFilterAdapter.isNotNull()) {
            binding?.filterDetailFilterList?.setAdapter(expandableFilterAdapter)
        } else {
            viewModel.filterList(phone)
        }
        binding?.filterDetailFilterList?.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
            Log.e("contactTAG",
                "ContactDetailFragment setNumberList setOnChildClickListener groupPosition $groupPosition childPosition $childPosition")
            //TODO implement click listeners
            return@setOnChildClickListener true
        }
        binding?.filterDetailFilterList?.setOnGroupClickListener { _, _, groupPosition, _ ->
            return@setOnGroupClickListener expandableFilterAdapter?.getChildrenCount(groupPosition) == 0
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            contactDetailLiveData.safeSingleObserve(viewLifecycleOwner) { contact ->
                setContactInfo(contact)
            }
            filterLiveData.safeSingleObserve(viewLifecycleOwner) { filterList ->
                setFilterList(filterList.filter { it.isBlackFilter() }, true)
                setFilterList(filterList.filter { it.isBlackFilter().not() }, false)
            }
            blockedCallLiveData.safeSingleObserve(viewLifecycleOwner) { blockedCallList ->
                setBlockedCallList(blockedCallList)
            }
        }
    }

    private fun setContactInfo(contact: Contact) {
        binding?.apply {
            contact.name = if (contact.name.orEmpty()
                    .isEmpty()
            ) getString(R.string.no_in_contact_list) else contact.name
            filterDetailAddFilter.setSafeOnClickListener {
                //TODO implement click listener
            }
        }
    }

    private fun setFilterList(filterList: List<Filter>, isBlackList: Boolean) {
        val title = String.format(getString(R.string.contact_filter_match),
            filterList.size,
            getString(if (isBlackList) R.string.black_list else R.string.white_list))
        Log.e("contactTAG",
            "ContactDetailFragment setNumberList filterList $filterList isBlackList $isBlackList title $title")
        if (expandableFilterAdapter.isNotNull()) {
            expandableFilterAdapter?.titleList?.add(title)
            expandableFilterAdapter?.filterListMap?.put(title, filterList)
        } else {
            expandableFilterAdapter =
                ContactDetailAdapter(arrayListOf(title), hashMapOf(title to filterList))
            binding?.filterDetailFilterList?.setAdapter(expandableFilterAdapter)
        }
        Log.e("contactTAG",
            "ContactDetailFragment setNumberList expandableNumberAdapter titleList ${expandableFilterAdapter?.titleList} filterListMap ${expandableFilterAdapter?.filterListMap}")
    }

    private fun setBlockedCallList(blockedCallList: List<BlockedCall>) {
        Log.e("contactTAG",
            "ContactDetailFragment setBlockedCallList blockedCallList $blockedCallList")
        binding?.filterDetailBlockedList?.removeAllViews()
        binding?.filterDetailBlockedTitle?.isVisible = blockedCallList.isNotEmpty()
        blockedCallList.sortedByDescending { it.time }.forEach {
            val textView = TextView(context)
            textView.text = String.format("%s %s", it.dateFromTime(), it.dateTimeFromTime())
            binding?.filterDetailBlockedList?.addView(textView)
        }
    }
}