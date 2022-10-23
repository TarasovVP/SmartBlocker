package com.tarasovvp.blacklister.ui.main.call_detail

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.BLACK_FILTER
import com.tarasovvp.blacklister.constants.Constants.PLUS_CHAR
import com.tarasovvp.blacklister.databinding.FragmentCallDetailBinding
import com.tarasovvp.blacklister.extensions.isNotNull
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.model.BlockedCall
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.ui.main.contact_detail.ContactDetailAdapter
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class CallDetailFragment : BaseFragment<FragmentCallDetailBinding, CallDetailViewModel>() {

    override var layoutId = R.layout.fragment_call_detail
    override val viewModelClass = CallDetailViewModel::class.java

    private val args: CallDetailFragmentArgs by navArgs()

    private var expandableFilterAdapter: ContactDetailAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.call = args.call
    }

    private fun setFilterListView(phone: String) {
        if (expandableFilterAdapter.isNotNull()) {
            binding?.callDetailFilterList?.setAdapter(expandableFilterAdapter)
        } else {
            viewModel.filterList(phone)
        }
        binding?.callDetailFilterList?.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
            Log.e("contactTAG",
                "ContactDetailFragment setNumberList setOnChildClickListener groupPosition $groupPosition childPosition $childPosition")
            findNavController().navigate(CallDetailFragmentDirections.startFilterAddFragment(
                filter = expandableFilterAdapter?.getChild(groupPosition, childPosition)))
            return@setOnChildClickListener true
        }
        binding?.callDetailFilterList?.setOnGroupClickListener { _, _, groupPosition, _ ->
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
            callDetailAddFilter.setSafeOnClickListener {
                findNavController().navigate(CallDetailFragmentDirections.startFilterAddFragment(
                    filter = Filter(filter = contact.trimmedPhone).apply {
                        filterType = BLACK_FILTER
                    }))
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
            binding?.callDetailFilterList?.setAdapter(expandableFilterAdapter)
        }
        Log.e("contactTAG",
            "ContactDetailFragment setNumberList expandableNumberAdapter titleList ${expandableFilterAdapter?.titleList} filterListMap ${expandableFilterAdapter?.filterListMap}")
    }

    private fun setBlockedCallList(blockedCallList: List<BlockedCall>) {
        Log.e("contactTAG",
            "ContactDetailFragment setBlockedCallList blockedCallList $blockedCallList")
        binding?.callDetailBlockedList?.removeAllViews()
        binding?.callDetailBlockedTitle?.isVisible = blockedCallList.isNotEmpty()
        blockedCallList.sortedByDescending { it.time }.forEach {
            val textView = TextView(context)
            textView.text = String.format("%s %s", it.dateFromTime(), it.dateTimeFromTime())
            binding?.callDetailBlockedList?.addView(textView)
        }
    }
}