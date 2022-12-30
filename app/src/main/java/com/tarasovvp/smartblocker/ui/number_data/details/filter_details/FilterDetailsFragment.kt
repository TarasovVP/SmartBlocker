package com.tarasovvp.smartblocker.ui.number_data.details.filter_details

import android.util.Log
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.constants.Constants.FILTER_ACTION
import com.tarasovvp.smartblocker.constants.Constants.PERMISSION
import com.tarasovvp.smartblocker.databinding.FragmentFilterDetailBinding
import com.tarasovvp.smartblocker.enums.FilterAction
import com.tarasovvp.smartblocker.enums.Info
import com.tarasovvp.smartblocker.extensions.isTrue
import com.tarasovvp.smartblocker.extensions.safeSingleObserve
import com.tarasovvp.smartblocker.models.Filter
import com.tarasovvp.smartblocker.models.InfoData
import com.tarasovvp.smartblocker.ui.MainActivity
import com.tarasovvp.smartblocker.ui.base.BaseDetailsFragment
import com.tarasovvp.smartblocker.ui.base.BaseNumberDataFragment
import com.tarasovvp.smartblocker.ui.number_data.details.DetailsPagerAdapter
import com.tarasovvp.smartblocker.ui.number_data.details.SingleDetailsFragment
import com.tarasovvp.smartblocker.ui.number_data.details.number_data_detail.NumberDataDetailsFragmentDirections
import com.tarasovvp.smartblocker.utils.setSafeOnClickListener

class FilterDetailsFragment :
    BaseDetailsFragment<FragmentFilterDetailBinding, FilterDetailsViewModel>() {

    override var layoutId = R.layout.fragment_filter_detail
    override val viewModelClass = FilterDetailsViewModel::class.java
    private val args: FilterDetailsFragmentArgs by navArgs()

    private var numberDataScreen: SingleDetailsFragment? = null
    private var filteredCallsScreen: SingleDetailsFragment? = null

    override fun initViews() {
        binding?.apply {
            args.filterDetail?.let { filter ->
                (activity as MainActivity).toolbar?.title = getString(filter.filterTypeTitle())
                this.filter = filter
                Log.e("filterDetailTAG",
                    "FilterDetailFragment initViews after args.filter $filter binding?.filter ${this.filter}")
            }
            executePendingBindings()
        }
    }

    override fun setFragmentResultListeners() {
        binding?.filter?.let { filter ->
            setFragmentResultListener(FILTER_ACTION) { _, bundle ->
                when (val filterAction = bundle.getSerializable(FILTER_ACTION) as FilterAction) {
                    FilterAction.FILTER_ACTION_BLOCKER_TRANSFER,
                    FilterAction.FILTER_ACTION_PERMISSION_TRANSFER,
                    -> viewModel.updateFilter(filter.apply {
                        filterType = if (this.isBlocker()) PERMISSION else BLOCKER
                        this.filterAction = filterAction
                    })
                    FilterAction.FILTER_ACTION_BLOCKER_DELETE,
                    FilterAction.FILTER_ACTION_PERMISSION_DELETE,
                    -> viewModel.deleteFilter(filter.apply {
                        this.filterAction = filterAction
                    })
                    else -> Unit
                }
            }
        }
    }

    override fun setClickListeners() {
        binding?.apply {
            filterDetailChangeFilter.setSafeOnClickListener {
                startFilterActionDialog(if (filter?.isBlocker()
                        .isTrue()
                ) FilterAction.FILTER_ACTION_BLOCKER_TRANSFER else FilterAction.FILTER_ACTION_PERMISSION_TRANSFER)
            }
            filterDetailDeleteFilter.setSafeOnClickListener {
                startFilterActionDialog(if (filter?.isBlocker()
                        .isTrue()
                ) FilterAction.FILTER_ACTION_BLOCKER_DELETE else FilterAction.FILTER_ACTION_PERMISSION_DELETE)
            }
        }
    }

    private fun startFilterActionDialog(filterAction: FilterAction) {
        findNavController().navigate(FilterDetailsFragmentDirections.startFilterActionDialog(
            filterNumber = String.format(getString(R.string.number_value), binding?.filter?.filter),
            filterAction = filterAction))
    }

    override fun createAdapter() {
        numberDataScreen = SingleDetailsFragment {
            findNavController().navigate(FilterDetailsFragmentDirections.startNumberDataDetailFragment(numberData = it))
        }
        filteredCallsScreen = SingleDetailsFragment(true) {
            findNavController().navigate(FilterDetailsFragmentDirections.startNumberDataDetailFragment(numberData = it))
        }
        val fragmentList = arrayListOf(
            numberDataScreen,
            filteredCallsScreen
        )

        binding?.filterDetailViewPager?.adapter =
            activity?.supportFragmentManager?.let { fragmentManager ->
                DetailsPagerAdapter(
                    fragmentList,
                    fragmentManager,
                    lifecycle
                )
            }
        binding?.filterDetailViewPager?.offscreenPageLimit = 2
        binding?.filterDetailViewPager?.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding?.filterDetailItemFilter?.isFilteredCallDetails =
                    binding?.filterDetailItemFilter?.isFilteredCallDetails.isTrue().not()
                binding?.filterDetailTabs?.setImageResource(if (position == 0) R.drawable.ic_filter_details_tab_1 else R.drawable.ic_filter_details_tab_2)
            }
        })
    }

    override fun getData() {
        binding?.filter?.let {
            viewModel.getQueryContactCallList(it)
            viewModel.filteredCallsByFilter(it.filter)
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            numberDataListLiveData.safeSingleObserve(viewLifecycleOwner) { numberDataList ->
                numberDataScreen?.updateNumberDataList(numberDataList)
            }
            filteredCallListLiveData.safeSingleObserve(viewLifecycleOwner) { filteredCallList ->
                binding?.filter?.filteredCalls = filteredCallList.size.toString()
                filteredCallsScreen?.updateNumberDataList(filteredCallList)
            }
            filterActionLiveData.safeSingleObserve(viewLifecycleOwner) { filter ->
                handleSuccessFilterAction(filter)
            }
        }
    }

    private fun handleSuccessFilterAction(filter: Filter) {
        (activity as MainActivity).apply {
            showMessage(String.format(filter.filterAction?.successText?.let { getString(it) }
                .orEmpty(), binding?.filter?.filter.orEmpty()), false)
            getAllData()
            if (filter.isChangeFilterAction()) {
                mainViewModel.successAllDataLiveData.safeSingleObserve(viewLifecycleOwner) {
                    initViews()
                    viewModel.getQueryContactCallList(filter)
                }
            } else {
                findNavController().navigate(if (binding?.filter?.isBlocker()
                        .isTrue()
                ) FilterDetailsFragmentDirections.startBlockerListFragment()
                else FilterDetailsFragmentDirections.startPermissionListFragment())
            }
        }
    }

    override fun showInfoScreen() {
        findNavController().navigate(NumberDataDetailsFragmentDirections.startInfoFragment(info = InfoData(
            title = getString(Info.INFO_FILTER_DETAIL.title),
            description = getString(Info.INFO_FILTER_DETAIL.description))))
    }
}