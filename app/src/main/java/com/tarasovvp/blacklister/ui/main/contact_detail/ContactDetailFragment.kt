package com.tarasovvp.blacklister.ui.main.contact_detail

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.FragmentContactDetailBinding
import com.tarasovvp.blacklister.extensions.isNull
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.ui.main.filter_add.ContactFilterAdapter

class ContactDetailFragment : BaseFragment<FragmentContactDetailBinding, ContactDetailViewModel>() {

    override var layoutId = R.layout.fragment_contact_detail
    override val viewModelClass = ContactDetailViewModel::class.java
    private val args: ContactDetailFragmentArgs by navArgs()

    private var contactFilterAdapter: ContactFilterAdapter? = null
    private var contactFilterList: ArrayList<BaseAdapter.MainData> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args.contact?.apply {
            binding?.contact = this
            if (contactFilterAdapter?.contactFilterList.isNull()) {
                viewModel.filterListWithContact(this.phone)
            }
        }
        setContactAdapter()
    }

    private fun setContactAdapter() {
        contactFilterAdapter =
            contactFilterAdapter ?: ContactFilterAdapter(contactFilterList) { filter ->
                findNavController().navigate(ContactDetailFragmentDirections.startFilterDetailFragment(
                    filter as Filter))
            }
        binding?.contactDetailFilterList?.adapter = contactFilterAdapter
    }

    override fun observeLiveData() {
        with(viewModel) {
            filterListLiveData.safeSingleObserve(viewLifecycleOwner) { contactList ->
                binding?.contactDetailFilterListEmpty?.emptyStateTitle?.text =
                    getString(R.string.filter_by_contact_empty_state)
                binding?.contactDetailFilterListDescription?.isVisible = contactList.isNotEmpty()
                binding?.contactDetailFilterListEmpty?.emptyStateContainer?.isVisible =
                    contactList.isEmpty()
                contactFilterAdapter?.contactFilterList = contactList
                contactFilterAdapter?.notifyDataSetChanged()
            }
        }
    }

}