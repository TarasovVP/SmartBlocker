package com.tarasovvp.smartblocker.presentation.main.number.details.details_filter

import androidx.core.content.ContextCompat
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
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.enums.FilterAction
import com.tarasovvp.smartblocker.domain.enums.Info
import com.tarasovvp.smartblocker.domain.entities.models.InfoData
import com.tarasovvp.smartblocker.domain.entities.models.NumberData
import com.tarasovvp.smartblocker.presentation.main.MainActivity
import com.tarasovvp.smartblocker.presentation.base.BaseDetailsFragment
import com.tarasovvp.smartblocker.presentation.main.number.details.DetailsPagerAdapter
import com.tarasovvp.smartblocker.presentation.main.number.details.NumberDataClickListener
import com.tarasovvp.smartblocker.presentation.main.number.details.SingleDetailsFragment
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
            args.filterWithCountryCode?.let { filterWithCountryCode ->
                (activity as MainActivity).toolbar?.title = getString(filterWithCountryCode.filterTypeTitle().orZero())
                this.filterWithCountryCode = filterWithCountryCode
            }
            executePendingBindings()
        }
    }

    override fun setFragmentResultListeners() {
        binding?.filterWithCountryCode?.let { filter ->
            setFragmentResultListener(FILTER_ACTION) { _, bundle ->
                when (val filterAction = bundle.serializable(FILTER_ACTION) as? FilterAction) {
                    FilterAction.FILTER_ACTION_BLOCKER_TRANSFER,
                    FilterAction.FILTER_ACTION_PERMISSION_TRANSFER,
                    -> { viewModel.updateFilter(filter.apply {
                        this.filter?.filterType = if (filter.isBlocker()) PERMISSION else BLOCKER
                        filter.filterAction = filterAction
                    })
                    }
                    FilterAction.FILTER_ACTION_BLOCKER_DELETE,
                    FilterAction.FILTER_ACTION_PERMISSION_DELETE,
                    -> viewModel.deleteFilter(filter.apply {
                        filter.filterAction = filterAction
                    })
                    else -> Unit
                }
            }
        }
    }

    override fun setClickListeners() {
        binding?.apply {
            detailsFilterChangeFilter.setSafeOnClickListener {
                startFilterActionDialog(if (filterWithCountryCode?.isBlocker()
                        .isTrue()
                ) FilterAction.FILTER_ACTION_BLOCKER_TRANSFER else FilterAction.FILTER_ACTION_PERMISSION_TRANSFER)
            }
            detailsFilterDeleteFilter.setSafeOnClickListener {
                startFilterActionDialog(if (filterWithCountryCode?.isBlocker()
                        .isTrue()
                ) FilterAction.FILTER_ACTION_BLOCKER_DELETE else FilterAction.FILTER_ACTION_PERMISSION_DELETE)
            }
        }
    }

    private fun startFilterActionDialog(filterAction: FilterAction) {
        findNavController().navigate(
            DetailsFilterFragmentDirections.startFilterActionDialog(
                filterWithCountryCode = binding?.filterWithCountryCode?.apply {
                    this@apply.filterAction = filterAction
                })
        )
    }

    override fun createAdapter() {
        numberDataScreen = SingleDetailsFragment.newInstance(Filter::class.simpleName.orEmpty())
        numberDataScreen?.setNumberDataClick(object : NumberDataClickListener {
            override fun onNumberDataClick(numberData: NumberData) {
                findNavController().navigate(
                    DetailsFilterFragmentDirections.startDetailsNumberDataFragment(
                        numberData = numberData
                    )
                )
            }
        })
        filteredCallsScreen = SingleDetailsFragment.newInstance(CallWithFilter::class.simpleName.orEmpty())
        filteredCallsScreen?.setNumberDataClick(object : NumberDataClickListener {
            override fun onNumberDataClick(numberData: NumberData) {
                findNavController().navigate(
                    DetailsFilterFragmentDirections.startDetailsNumberDataFragment(
                        numberData = numberData
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
        binding?.filterWithCountryCode?.filter?.let {
            viewModel.getQueryContactCallList(it)
            viewModel.filteredCallsByFilter(it.filter)
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            numberDataListLiveData.safeSingleObserve(viewLifecycleOwner) { numberDataList ->
                context?.let {
                    filteredNumberDataList(binding?.filterWithCountryCode?.filter, numberDataList, ContextCompat.getColor(it, R.color.text_color_black))
                }
            }
            filteredNumberDataListLiveData.safeSingleObserve(viewLifecycleOwner) { numberDataList ->
                numberDataScreen?.updateNumberDataList(numberDataList)
            }
            filteredCallListLiveData.safeSingleObserve(viewLifecycleOwner) { filteredCallList ->
                binding?.filterWithCountryCode?.filteredCalls = filteredCallList.size
                filteredCallsScreen?.updateNumberDataList(filteredCallList)
            }
            filterActionLiveData.safeSingleObserve(viewLifecycleOwner) { filter ->
                handleSuccessFilterAction(filter)
            }
        }
    }

    private fun handleSuccessFilterAction(filter: FilterWithCountryCode) {
        (activity as MainActivity).apply {
            showInfoMessage(String.format(filter.filterAction?.successText()?.let { getString(it) }
                .orEmpty(), binding?.filterWithCountryCode?.filter?.filter.orEmpty()), false)
            //TODO interstitial
            //showInterstitial()
            getAllData()
            if (filter.isChangeFilterAction()) {
                mainViewModel.successAllDataLiveData.safeSingleObserve(viewLifecycleOwner) {
                    initViews()
                    filter.filter?.let { it1 -> viewModel.getQueryContactCallList(it1) }
                }
            } else {
                findNavController().navigate(if (binding?.filterWithCountryCode?.isBlocker().isTrue()) DetailsFilterFragmentDirections.startListBlockerFragment()
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