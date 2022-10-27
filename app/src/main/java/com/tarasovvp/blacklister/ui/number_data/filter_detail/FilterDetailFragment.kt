package com.tarasovvp.blacklister.ui.number_data.filter_detail

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.FragmentFilterDetailBinding
import com.tarasovvp.blacklister.enums.AddFilterState
import com.tarasovvp.blacklister.extensions.isNull
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.model.Contact
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.ui.number_data.NumberData
import com.tarasovvp.blacklister.ui.number_data.NumberDataAdapter
import com.tarasovvp.blacklister.ui.number_data.filter_add.FilterAddFragmentDirections
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class FilterDetailFragment : BaseFragment<FragmentFilterDetailBinding, FilterDetailViewModel>() {

    override var layoutId = R.layout.fragment_filter_detail
    override val viewModelClass = FilterDetailViewModel::class.java
    private val args: FilterDetailFragmentArgs by navArgs()

    private var numberDataAdapter: NumberDataAdapter? = null
    private var contactFilterList: ArrayList<NumberData>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args.filter?.apply {
            binding?.filter = this
            (activity as MainActivity).toolbar?.title = getString(filterTypeTitle())
            binding?.filterDetailContactListDescription?.text =
                if (isBlackFilter().isTrue()) getString(
                    R.string.contact_list_with_blocker) else getString(R.string.contact_list_with_allow)
            if (numberDataAdapter?.contactFilterList.isNull()) {
                viewModel.getQueryContactList(this)
            }
        }
        setContactAdapter()
        setClickListeners()
    }

    private fun setClickListeners() {
        binding?.filterDetailDeleteFilter?.setSafeOnClickListener {
            findNavController().navigate(FilterDetailFragmentDirections.startFilterActionDialog(
                filter = binding?.filter,
                filterAction = AddFilterState.ADD_FILTER_DELETE.name))
        }
        setFragmentResultListener(AddFilterState.ADD_FILTER_DELETE.name) { _, _ ->
            binding?.filter?.let {
                viewModel.deleteFilter(it)
            }
        }
    }

    private fun setContactAdapter() {
        numberDataAdapter =
            numberDataAdapter ?: NumberDataAdapter(contactFilterList) { contact ->
                findNavController().navigate(FilterDetailFragmentDirections.startNumberDataDetailFragment(
                    contact as Contact))
            }
        binding?.filterDetailContactList?.adapter = numberDataAdapter
    }

    override fun observeLiveData() {
        with(viewModel) {
            contactListLiveData.safeSingleObserve(viewLifecycleOwner) { filterList ->
                binding?.filterDetailContactListEmpty?.emptyStateTitle?.text =
                    if (binding?.filter?.isBlackFilter().isTrue()) getString(
                        R.string.contact_by_blocker_empty_state) else getString(R.string.contact_by_allowing_empty_state)
                binding?.filterDetailContactListDescription?.isVisible = filterList.isNotEmpty()
                binding?.filterDetailContactListEmpty?.emptyStateContainer?.isVisible =
                    filterList.isEmpty()
                numberDataAdapter?.contactFilterList = filterList
                numberDataAdapter?.notifyDataSetChanged()
            }
            deleteFilterLiveData.safeSingleObserve(viewLifecycleOwner) {
                (activity as MainActivity).apply {
                    showMessage(String.format(getString(R.string.delete_filter_from_list),
                        binding?.filter?.filter.orEmpty()), false)
                    getAllData()
                    findNavController().navigate(if (binding?.filter?.isBlackFilter().isTrue())
                        FilterDetailFragmentDirections.startBlackFilterListFragment()
                    else FilterDetailFragmentDirections.startWhiteFilterListFragment())
                }
            }
        }
    }
}