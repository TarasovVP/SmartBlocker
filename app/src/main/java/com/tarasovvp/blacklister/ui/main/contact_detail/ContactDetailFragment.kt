package com.tarasovvp.blacklister.ui.main.contact_detail

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.PLUS_CHAR
import com.tarasovvp.blacklister.databinding.FragmentContactDetailBinding
import com.tarasovvp.blacklister.extensions.isNotNull
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.model.BlockedCall
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class ContactDetailFragment : BaseFragment<FragmentContactDetailBinding, ContactDetailViewModel>() {

    override var layoutId = R.layout.fragment_contact_detail
    override val viewModelClass = ContactDetailViewModel::class.java

    private val args: ContactDetailFragmentArgs by navArgs()

    private var expandableFilterAdapter: ContactDetailAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setPriority()
        args.phone?.filter { it.isDigit() || it == PLUS_CHAR }?.let { phone ->
            if (phone.isEmpty()) {
                viewModel.contactDetailLiveData.postValue(Contact(name = getString(R.string.hidden)))
            } else {
                viewModel.getContact(phone)
            }
            viewModel.getBlockedCallList(phone)
            setFilterListView(phone)
        }
    }

    private fun setFilterListView(phone: String) {
        if (expandableFilterAdapter.isNotNull()) {
            binding?.contactDetailFilterList?.setAdapter(expandableFilterAdapter)
        } else {
            viewModel.getBlackFilterList(phone)
            viewModel.getWhiteFilterList(phone)
        }
        binding?.contactDetailFilterList?.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
            Log.e("contactTAG",
                "ContactDetailFragment setNumberList setOnChildClickListener groupPosition $groupPosition childPosition $childPosition")
            findNavController().navigate(ContactDetailFragmentDirections.startAddFragment(
                filter = expandableFilterAdapter?.getChild(groupPosition, childPosition)))
            return@setOnChildClickListener true
        }
        binding?.contactDetailFilterList?.setOnGroupClickListener { _, _, groupPosition, _ ->
            return@setOnGroupClickListener expandableFilterAdapter?.getChildrenCount(groupPosition) == 0
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            contactDetailLiveData.safeSingleObserve(viewLifecycleOwner) { contact ->
                setContactInfo(contact)
            }
            blackFilterLiveData.safeSingleObserve(viewLifecycleOwner) { blackNumberList ->
                setFilterList(blackNumberList, true)
            }
            whiteFilterLiveData.safeSingleObserve(viewLifecycleOwner) { whiteNumberList ->
                setFilterList(whiteNumberList, false)
            }
            blockedCallLiveData.safeSingleObserve(viewLifecycleOwner) { blockedCallList ->
                setBlockedCallList(blockedCallList)
            }
        }
    }

    private fun setPriority() {
        binding?.contactDetailPriority?.setCompoundDrawablesWithIntrinsicBounds(0,
            0,
            if (SharedPreferencesUtil.isWhiteListPriority) R.drawable.ic_white_filter else R.drawable.ic_black_filter,
            0)
        binding?.contactDetailPriority?.setSafeOnClickListener {
            findNavController().navigate(ContactDetailFragmentDirections.startBlockSettingsFragment())
        }
    }

    private fun setContactInfo(contact: Contact) {
        binding?.apply {
            this.contact = contact
            contactDetailPriority.text = String.format(getString(R.string.prioritness),
                if (SharedPreferencesUtil.isWhiteListPriority) getString(R.string.white_list) else getString(
                    R.string.black_list))
            contactDetailAddFilter.setSafeOnClickListener {
                findNavController().navigate(ContactDetailFragmentDirections.startAddFragment(
                    filter = Filter(filter = contact.trimmedPhone).apply {
                        isBlackFilter = true
                    }))
            }
        }
    }

    private fun setFilterList(filterList: List<Filter>, isBlackList: Boolean) {
        val title = String.format(getString(R.string.contact_filter_match), filterList.size, getString(if (isBlackList) R.string.black_list else R.string.white_list))
        Log.e("contactTAG",
            "ContactDetailFragment setNumberList filterList $filterList isBlackList $isBlackList title $title")
        if (expandableFilterAdapter.isNotNull()) {
            expandableFilterAdapter?.titleList?.add(title)
            expandableFilterAdapter?.filterListMap?.put(title, filterList)
        } else {
            expandableFilterAdapter =
                ContactDetailAdapter(arrayListOf(title), hashMapOf(title to filterList))
            binding?.contactDetailFilterList?.setAdapter(expandableFilterAdapter)
        }
        Log.e("contactTAG",
            "ContactDetailFragment setNumberList expandableNumberAdapter titleList ${expandableFilterAdapter?.titleList} filterListMap ${expandableFilterAdapter?.filterListMap}")
    }

    private fun setBlockedCallList(blockedCallList: List<BlockedCall>) {
        Log.e("contactTAG",
            "ContactDetailFragment setBlockedCallList blockedCallList $blockedCallList")
        binding?.contactDetailBlockedList?.removeAllViews()
        binding?.contactDetailBlockedTitle?.isVisible = blockedCallList.isNotEmpty()
        blockedCallList.sortedByDescending { it.time }.forEach {
            val textView = TextView(context)
            textView.text = String.format("%s %s", it.dateFromTime(), it.dateTimeFromTime())
            binding?.contactDetailBlockedList?.addView(textView)
        }
    }
}