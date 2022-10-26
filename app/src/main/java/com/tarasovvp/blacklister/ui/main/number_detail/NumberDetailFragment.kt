package com.tarasovvp.blacklister.ui.main.number_detail

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.databinding.FragmentNumberDetailBinding
import com.tarasovvp.blacklister.enums.Condition
import com.tarasovvp.blacklister.extensions.isNull
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.ui.main.filter_add.ContactFilterAdapter
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class NumberDetailFragment : BaseFragment<FragmentNumberDetailBinding, NumberDetailViewModel>() {

    override var layoutId = R.layout.fragment_number_detail
    override val viewModelClass = NumberDetailViewModel::class.java
    private val args: NumberDetailFragmentArgs by navArgs()

    private var contactFilterAdapter: ContactFilterAdapter? = null
    private var contactFilterList: ArrayList<BaseAdapter.NumberData> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args.contact?.apply {
            binding?.contact = this
            if (contactFilterAdapter?.contactFilterList.isNull()) {
                viewModel.filterListWithNumber(this.trimmedPhone)
            }
            binding?.contactDetailAddFilter?.setSafeOnClickListener {
                findNavController().navigate(NumberDetailFragmentDirections.startFilterAddFragment(
                    filter = Filter(filter = trimmedPhone,
                        conditionType = Condition.CONDITION_TYPE_FULL.index,
                        filterType = Constants.BLACK_FILTER)))
            }
        }
        setContactAdapter()
    }

    private fun setContactAdapter() {
        contactFilterAdapter =
            contactFilterAdapter ?: ContactFilterAdapter(contactFilterList) { filter ->
                findNavController().navigate(NumberDetailFragmentDirections.startFilterDetailFragment(
                    filter as Filter))
            }
        binding?.contactDetailFilterList?.adapter = contactFilterAdapter
    }

    private fun checkFilterListEmptiness() {
        binding?.contactDetailFilterListEmpty?.emptyStateTitle?.text = getString(R.string.filter_by_contact_empty_state)
        binding?.contactDetailFilterListDescription?.isVisible = contactFilterList.isNotEmpty()
        binding?.contactDetailFilterListEmpty?.emptyStateContainer?.isVisible = contactFilterList.isEmpty()
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