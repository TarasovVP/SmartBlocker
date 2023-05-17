package com.tarasovvp.smartblocker.presentation.main.number.details.details_filter

import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTER_ACTION
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PERMISSION
import com.tarasovvp.smartblocker.databinding.FragmentDetailsFilterBinding
import com.tarasovvp.smartblocker.domain.entities.db_views.CallWithFilter
import com.tarasovvp.smartblocker.domain.enums.FilterAction
import com.tarasovvp.smartblocker.domain.enums.Info
import com.tarasovvp.smartblocker.presentation.ui_models.InfoData
import com.tarasovvp.smartblocker.presentation.ui_models.NumberDataUIModel
import com.tarasovvp.smartblocker.presentation.main.MainActivity
import com.tarasovvp.smartblocker.presentation.base.BaseDetailsFragment
import com.tarasovvp.smartblocker.presentation.main.number.details.DetailsPagerAdapter
import com.tarasovvp.smartblocker.presentation.main.number.details.NumberDataClickListener
import com.tarasovvp.smartblocker.presentation.main.number.details.SingleDetailsFragment
import com.tarasovvp.smartblocker.presentation.ui_models.FilterWithFilteredNumberUIModel
import com.tarasovvp.smartblocker.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsFilterFragment :
    BaseDetailsFragment<FragmentDetailsFilterBinding, DetailsFilterViewModel>() {

    override var layoutId = R.layout.fragment_details_filter
    override val viewModelClass = DetailsFilterViewModel::class.java
    private val args: DetailsFilterFragmentArgs by navArgs()

    private var numberDataScreen: SingleDetailsFragment? = null
    private var filteredCallsScreen: SingleDetailsFragment? = null

    override fun initViews() {
        binding?.apply {
            args.filterWithFilteredNumberUIModel?.let { filterWithFilteredNumberUIModel ->
                (activity as MainActivity).toolbar?.title = getString(filterWithFilteredNumberUIModel.filterTypeTitle())
                this.filterWithFilteredNumberUIModel = filterWithFilteredNumberUIModel
            }
            executePendingBindings()
        }
    }

    override fun setFragmentResultListeners() {
        binding?.filterWithFilteredNumberUIModel?.let { filterWithFilteredNumberUIModel ->
            setFragmentResultListener(FILTER_ACTION) { _, bundle ->
                when (val filterAction = bundle.serializable(FILTER_ACTION) as? FilterAction) {
                    FilterAction.FILTER_ACTION_BLOCKER_TRANSFER,
                    FilterAction.FILTER_ACTION_PERMISSION_TRANSFER,
                    -> { viewModel.updateFilter(filterWithFilteredNumberUIModel.apply {
                        this.filterType = if (filterWithFilteredNumberUIModel.isBlocker()) PERMISSION else BLOCKER
                        filterWithFilteredNumberUIModel.filterAction = filterAction
                    })
                    }
                    FilterAction.FILTER_ACTION_BLOCKER_DELETE,
                    FilterAction.FILTER_ACTION_PERMISSION_DELETE,
                    -> viewModel.deleteFilter(filterWithFilteredNumberUIModel.apply {
                        filterWithFilteredNumberUIModel.filterAction = filterAction
                    })
                    else -> Unit
                }
            }
        }
    }

    override fun setClickListeners() {
        binding?.apply {
            detailsFilterChangeFilter.setSafeOnClickListener {
                startFilterActionDialog(if (filterWithFilteredNumberUIModel?.isBlocker()
                        .isTrue()
                ) FilterAction.FILTER_ACTION_BLOCKER_TRANSFER else FilterAction.FILTER_ACTION_PERMISSION_TRANSFER)
            }
            detailsFilterDeleteFilter.setSafeOnClickListener {
                startFilterActionDialog(if (filterWithFilteredNumberUIModel?.isBlocker()
                        .isTrue()
                ) FilterAction.FILTER_ACTION_BLOCKER_DELETE else FilterAction.FILTER_ACTION_PERMISSION_DELETE)
            }
        }
    }

    private fun startFilterActionDialog(filterAction: FilterAction) {
        binding?.filterWithFilteredNumberUIModel?.let { filterWithFilteredNumberUIModel ->
            findNavController().navigate(DetailsFilterFragmentDirections.startFilterActionDialog(filterWithFilteredNumberUIModel = filterWithFilteredNumberUIModel.apply {
                this.filterAction = filterAction
            }))
        }
    }

    override fun createAdapter() {
        numberDataScreen = SingleDetailsFragment.newInstance(Filter::class.simpleName.orEmpty())
        numberDataScreen?.setNumberDataClick(object : NumberDataClickListener {
            override fun onNumberDataClick(numberDataUIModel: NumberDataUIModel) {
                findNavController().navigate(
                    DetailsFilterFragmentDirections.startDetailsNumberDataFragment(
                        numberData = numberDataUIModel
                    )
                )
            }
        })
        filteredCallsScreen = SingleDetailsFragment.newInstance(CallWithFilter::class.simpleName.orEmpty())
        filteredCallsScreen?.setNumberDataClick(object : NumberDataClickListener {
            override fun onNumberDataClick(numberDataUIModel: NumberDataUIModel) {
                findNavController().navigate(
                    DetailsFilterFragmentDirections.startDetailsNumberDataFragment(
                        numberData = numberDataUIModel
                    )
                )
            }
        })
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
        binding?.filterWithFilteredNumberUIModel?.let {
            viewModel.getQueryContactCallList(it.filter)
            viewModel.filteredCallsByFilter(it.filter)
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            numberDataListLiveDataUIModel.safeSingleObserve(viewLifecycleOwner) { numberDataList ->
                numberDataScreen?.updateNumberDataList(numberDataList)
            }
            filteredCallListLiveData.safeSingleObserve(viewLifecycleOwner) { filteredCallList ->
                binding?.filterWithFilteredNumberUIModel?.filteredCalls = filteredCallList.size
                filteredCallsScreen?.updateNumberDataList(filteredCallList)
            }
            filterActionLiveData.safeSingleObserve(viewLifecycleOwner) { filter ->
                handleSuccessFilterAction(filter)
            }
        }
    }

    private fun handleSuccessFilterAction(filterWithFilteredNumberUIModel: FilterWithFilteredNumberUIModel) {
        (activity as MainActivity).apply {
            showInfoMessage(String.format(filterWithFilteredNumberUIModel.filterAction?.successText()?.let { getString(it) }
                .orEmpty(), binding?.filterWithFilteredNumberUIModel?.filter.orEmpty()), false)
            //TODO interstitial
            //showInterstitial()
            getAllData()
            if (filterWithFilteredNumberUIModel.isChangeFilterAction()) {
                mainViewModel.successAllDataLiveData.safeSingleObserve(viewLifecycleOwner) {
                    initViews()
                    viewModel.getQueryContactCallList(filterWithFilteredNumberUIModel.filter)
                }
            } else {
                findNavController().navigate(if (binding?.filterWithFilteredNumberUIModel?.isBlocker().isTrue()) DetailsFilterFragmentDirections.startListBlockerFragment()
                else DetailsFilterFragmentDirections.startListPermissionFragment()
                )
            }
        }
    }

    override fun showInfoScreen() {
        findNavController().navigate(
            DetailsFilterFragmentDirections.startInfoFragment(
                info = InfoData(
                    title = getString(Info.INFO_DETAILS_FILTER.title()),
                    description = getString(Info.INFO_DETAILS_FILTER.description())
                )
            )
        )
    }
}