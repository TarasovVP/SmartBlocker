package com.tarasovvp.smartblocker.ui.main.number.details.details_filter

import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.constants.Constants.FILTER_ACTION
import com.tarasovvp.smartblocker.constants.Constants.PERMISSION
import com.tarasovvp.smartblocker.databinding.FragmentDetailsFilterBinding
import com.tarasovvp.smartblocker.enums.FilterAction
import com.tarasovvp.smartblocker.enums.Info
import com.tarasovvp.smartblocker.extensions.isTrue
import com.tarasovvp.smartblocker.extensions.safeSingleObserve
import com.tarasovvp.smartblocker.models.Filter
import com.tarasovvp.smartblocker.models.FilteredCall
import com.tarasovvp.smartblocker.models.InfoData
import com.tarasovvp.smartblocker.ui.MainActivity
import com.tarasovvp.smartblocker.ui.base.BaseDetailsFragment
import com.tarasovvp.smartblocker.ui.main.number.details.DetailsPagerAdapter
import com.tarasovvp.smartblocker.ui.main.number.details.SingleDetailsFragment
import com.tarasovvp.smartblocker.utils.setSafeOnClickListener

class DetailsFilterFragment :
    BaseDetailsFragment<FragmentDetailsFilterBinding, DetailsFilterViewModel>() {

    override var layoutId = R.layout.fragment_details_filter
    override val viewModelClass = DetailsFilterViewModel::class.java
    private val args: DetailsFilterFragmentArgs by navArgs()

    private var numberDataScreen: SingleDetailsFragment? = null
    private var filteredCallsScreen: SingleDetailsFragment? = null

    override fun initViews() {
        binding?.apply {
            args.filterDetails?.let { filter ->
                (activity as MainActivity).toolbar?.title = getString(filter.filterTypeTitle())
                this.filter = filter
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
            detailsFilterChangeFilter.setSafeOnClickListener {
                startFilterActionDialog(if (filter?.isBlocker()
                        .isTrue()
                ) FilterAction.FILTER_ACTION_BLOCKER_TRANSFER else FilterAction.FILTER_ACTION_PERMISSION_TRANSFER)
            }
            detailsFilterDeleteFilter.setSafeOnClickListener {
                startFilterActionDialog(if (filter?.isBlocker()
                        .isTrue()
                ) FilterAction.FILTER_ACTION_BLOCKER_DELETE else FilterAction.FILTER_ACTION_PERMISSION_DELETE)
            }
        }
    }

    private fun startFilterActionDialog(filterAction: FilterAction) {
        findNavController().navigate(DetailsFilterFragmentDirections.startFilterActionDialog(
            filter = binding?.filter?.apply {
                filter = String.format(getString(R.string.filter_action_number_value),
                    binding?.filter?.filter)
                this@apply.filterAction = filterAction
            }))
    }

    override fun createAdapter() {
        numberDataScreen = SingleDetailsFragment(Filter::class.simpleName.orEmpty()) {
            findNavController().navigate(DetailsFilterFragmentDirections.startDetailsNumberDataFragment(
                numberData = it))
        }
        filteredCallsScreen = SingleDetailsFragment(FilteredCall::class.simpleName.orEmpty()) {
            findNavController().navigate(DetailsFilterFragmentDirections.startDetailsNumberDataFragment(
                numberData = it))
        }
        val fragmentList = arrayListOf(
            numberDataScreen,
            filteredCallsScreen
        )

        binding?.detailsFilterViewPager?.adapter =
            activity?.supportFragmentManager?.let { fragmentManager ->
                DetailsPagerAdapter(
                    fragmentList,
                    fragmentManager,
                    lifecycle
                )
            }
        binding?.detailsFilterViewPager?.offscreenPageLimit = 2
        binding?.detailsFilterViewPager?.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding?.detailsFilterItemFilter?.isFilteredCallDetails =
                    binding?.detailsFilterItemFilter?.isFilteredCallDetails.isTrue().not()
                binding?.detailsFilterTabs?.setImageResource(if (position == 0) R.drawable.ic_filter_details_tab_1 else R.drawable.ic_filter_details_tab_2)
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
                binding?.filter?.filteredCalls = filteredCallList.size
                filteredCallsScreen?.updateNumberDataList(filteredCallList)
            }
            filterActionLiveData.safeSingleObserve(viewLifecycleOwner) { filter ->
                handleSuccessFilterAction(filter)
            }
        }
    }

    private fun handleSuccessFilterAction(filter: Filter) {
        (activity as MainActivity).apply {
            showInfoMessage(String.format(filter.filterAction?.successText?.let { getString(it) }
                .orEmpty(), binding?.filter?.filter.orEmpty()), false)
            showInterstitial()
            getAllData()
            if (filter.isChangeFilterAction()) {
                mainViewModel.successAllDataLiveData.safeSingleObserve(viewLifecycleOwner) {
                    initViews()
                    viewModel.getQueryContactCallList(filter)
                }
            } else {
                findNavController().navigate(if (binding?.filter?.isBlocker()
                        .isTrue()
                ) DetailsFilterFragmentDirections.startListBlockerFragment()
                else DetailsFilterFragmentDirections.startListPermissionFragment())
            }
        }
    }

    override fun showInfoScreen() {
        findNavController().navigate(DetailsFilterFragmentDirections.startInfoFragment(info = InfoData(
            title = getString(Info.INFO_FILTER_DETAILS.title),
            description = getString(Info.INFO_FILTER_DETAILS.description))))
    }
}