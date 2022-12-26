package com.tarasovvp.smartblocker.ui.number_data.filter_details

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.constants.Constants.FILTER_ACTION
import com.tarasovvp.smartblocker.constants.Constants.PERMISSION
import com.tarasovvp.smartblocker.databinding.FragmentFilterDetailBinding
import com.tarasovvp.smartblocker.databinding.FragmentSingleFilterDetailsBinding
import com.tarasovvp.smartblocker.enums.EmptyState
import com.tarasovvp.smartblocker.enums.FilterAction
import com.tarasovvp.smartblocker.extensions.isTrue
import com.tarasovvp.smartblocker.extensions.safeSingleObserve
import com.tarasovvp.smartblocker.models.Call
import com.tarasovvp.smartblocker.models.Contact
import com.tarasovvp.smartblocker.models.Filter
import com.tarasovvp.smartblocker.models.NumberData
import com.tarasovvp.smartblocker.ui.MainActivity
import com.tarasovvp.smartblocker.ui.base.BaseDetailFragment
import com.tarasovvp.smartblocker.ui.base.BaseFragment
import com.tarasovvp.smartblocker.ui.number_data.NumberDataAdapter
import com.tarasovvp.smartblocker.utils.setSafeOnClickListener

class SingleFilterDetailsFragment :
    BaseFragment<FragmentSingleFilterDetailsBinding, FilterDetailsViewModel>() {

    override var layoutId = R.layout.fragment_single_filter_details
    override val viewModelClass = FilterDetailsViewModel::class.java

    private var numberDataAdapter: NumberDataAdapter? = null
    private var numberDataList: ArrayList<NumberData>? = null
    private var filteredCallList: ArrayList<NumberData>? = null

    private var isFilteredCallDetails: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createAdapter()
    }

    private fun createAdapter() {
        numberDataAdapter =
            numberDataAdapter ?: NumberDataAdapter(arrayListOf()) { numberData ->

            }
        binding?.singleFilterDetailsList?.adapter = numberDataAdapter
    }

    override fun observeLiveData() {
        with(viewModel) {
            numberDataListLiveData.safeSingleObserve(viewLifecycleOwner) { numberDataList ->
                binding?.filterDetailsContactListEmpty?.root?.isVisible =
                    numberDataList.isEmpty().isTrue()
                numberDataAdapter?.numberDataList = numberDataList
                numberDataAdapter?.notifyDataSetChanged()
            }
            filteredCallListLiveData.safeSingleObserve(viewLifecycleOwner) { filteredCallList ->

                this@SingleFilterDetailsFragment.filteredCallList = filteredCallList
            }
        }
    }
}