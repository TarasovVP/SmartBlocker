package com.tarasovvp.blacklister.ui.main.call_detail

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.databinding.FragmentCallDetailBinding
import com.tarasovvp.blacklister.enums.Condition
import com.tarasovvp.blacklister.extensions.isNull
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.ui.main.filter_add.ContactFilterAdapter
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class CallDetailFragment : BaseFragment<FragmentCallDetailBinding, CallDetailViewModel>() {

    override var layoutId = R.layout.fragment_call_detail
    override val viewModelClass = CallDetailViewModel::class.java

    private val args: CallDetailFragmentArgs by navArgs()

    private var contactFilterAdapter: ContactFilterAdapter? = null
    private var contactFilterList: ArrayList<BaseAdapter.NumberData> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args.call?.apply {
            binding?.call = this
            if (contactFilterAdapter?.contactFilterList.isNull()) {
                viewModel.filterListWithCall(this.number)
            }
            binding?.callDetailAddFilter?.setSafeOnClickListener {
                findNavController().navigate(CallDetailFragmentDirections.startFilterAddFragment(
                    filter = Filter(filter = this.number,
                        conditionType = Condition.CONDITION_TYPE_FULL.index,
                        filterType = Constants.BLACK_FILTER)))
            }
        }
        setContactAdapter()
    }

    private fun setContactAdapter() {
        contactFilterAdapter =
            contactFilterAdapter ?: ContactFilterAdapter(contactFilterList) { filter ->
                findNavController().navigate(CallDetailFragmentDirections.startFilterDetailFragment(
                    filter as Filter))
            }
        binding?.callDetailFilterList?.adapter = contactFilterAdapter
    }

    private fun checkFilterListEmptiness() {
        binding?.callDetailFilterListEmpty?.emptyStateTitle?.text =
            getString(R.string.filter_by_call_empty_state)
        binding?.callDetailFilterListDescription?.isVisible = contactFilterList.isNotEmpty()
        binding?.callDetailFilterListEmpty?.emptyStateContainer?.isVisible =
            contactFilterList.isEmpty()
    }

    override fun observeLiveData() {
        viewModel.filterListLiveData.safeSingleObserve(viewLifecycleOwner) { contactList ->
            this.contactFilterList = contactList
            checkFilterListEmptiness()
            contactFilterAdapter?.contactFilterList = contactList
            contactFilterAdapter?.notifyDataSetChanged()
        }
    }
}