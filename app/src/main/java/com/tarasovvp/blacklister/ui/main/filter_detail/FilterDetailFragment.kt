package com.tarasovvp.blacklister.ui.main.filter_detail

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.FragmentFilterDetailBinding
import com.tarasovvp.blacklister.extensions.isNull
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.ui.main.filter_add.ContactFilterAdapter

class FilterDetailFragment : BaseFragment<FragmentFilterDetailBinding, FilterDetailViewModel>() {

    override var layoutId = R.layout.fragment_filter_detail
    override val viewModelClass = FilterDetailViewModel::class.java
    private val args: FilterDetailFragmentArgs by navArgs()

    private var contactFilterAdapter: ContactFilterAdapter? = null
    private var contactFilterList: ArrayList<BaseAdapter.NumberData>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args.filter?.apply {
            binding?.filter = this
            (activity as MainActivity).toolbar?.title = getString(filterTypeTitle())
            setContactAdapter()
            if (contactFilterAdapter?.contactFilterList.isNull()) {
                viewModel.getQueryContactList(this)
            }
        }
        binding?.filterDetailContactListDescription?.text =
            if (binding?.filter?.isBlackFilter().isTrue()) getString(
                R.string.contact_list_with_blocker) else getString(R.string.contact_list_with_allow)
    }

    private fun setContactAdapter() {
        contactFilterAdapter =
            contactFilterAdapter ?: ContactFilterAdapter(contactFilterList) { contact ->
                findNavController().navigate(FilterDetailFragmentDirections.startContactDetailFragment(
                    contact as Contact))
            }
        binding?.filterDetailContactList?.adapter = contactFilterAdapter
    }

    override fun observeLiveData() {
        viewModel.contactListLiveData.safeSingleObserve(viewLifecycleOwner) { filterList ->
            binding?.filterDetailContactListEmpty?.emptyStateTitle?.text =
                if (binding?.filter?.isBlackFilter().isTrue()) getString(
                    R.string.contact_by_blocker_empty_state) else getString(R.string.contact_by_allowing_empty_state)
            binding?.filterDetailContactListDescription?.isVisible = filterList.isNotEmpty()
            binding?.filterDetailContactListEmpty?.emptyStateContainer?.isVisible =
                filterList.isEmpty()
            contactFilterAdapter?.contactFilterList = filterList
            contactFilterAdapter?.notifyDataSetChanged()
        }
    }
}