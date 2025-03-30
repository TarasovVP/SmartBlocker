package com.tarasovvp.smartblocker.presentation.main.number.details.detailsnumberdata

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentDetailsNumberDataBinding
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.domain.enums.Info
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PERMISSION
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PLUS_CHAR
import com.tarasovvp.smartblocker.presentation.base.BaseDetailsFragment
import com.tarasovvp.smartblocker.presentation.main.number.details.DetailsPagerAdapter
import com.tarasovvp.smartblocker.presentation.main.number.details.NumberDataClickListener
import com.tarasovvp.smartblocker.presentation.main.number.details.SingleDetailsFragment
import com.tarasovvp.smartblocker.presentation.uimodels.CallWithFilterUIModel
import com.tarasovvp.smartblocker.presentation.uimodels.ContactWithFilterUIModel
import com.tarasovvp.smartblocker.presentation.uimodels.CountryCodeUIModel
import com.tarasovvp.smartblocker.presentation.uimodels.FilterWithCountryCodeUIModel
import com.tarasovvp.smartblocker.presentation.uimodels.FilterWithFilteredNumberUIModel
import com.tarasovvp.smartblocker.presentation.uimodels.NumberDataUIModel
import com.tarasovvp.smartblocker.utils.AppPhoneNumberUtil
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.changeFilterConditionButtonState
import com.tarasovvp.smartblocker.utils.extensions.changeFilterTypeButtonState
import com.tarasovvp.smartblocker.utils.extensions.digitsTrimmed
import com.tarasovvp.smartblocker.utils.extensions.highlightedSpanned
import com.tarasovvp.smartblocker.utils.extensions.isNull
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import com.tarasovvp.smartblocker.utils.extensions.orZero
import com.tarasovvp.smartblocker.utils.extensions.safeSingleObserve
import com.tarasovvp.smartblocker.utils.extensions.setSafeOnClickListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DetailsNumberDataFragment :
    BaseDetailsFragment<FragmentDetailsNumberDataBinding, DetailsNumberDataViewModel>() {
    @Inject
    lateinit var appPhoneNumberUtil: AppPhoneNumberUtil

    override var layoutId = R.layout.fragment_details_number_data
    override val viewModelClass = DetailsNumberDataViewModel::class.java
    private val args: DetailsNumberDataFragmentArgs by navArgs()

    override fun setFragmentResultListeners() = Unit

    private var filtersScreen: SingleDetailsFragment? = null
    private var filteredCallsScreen: SingleDetailsFragment? = null
    private var isHiddenCall = false
    private val filterWithCountryCodeUIModel by lazy { FilterWithCountryCodeUIModel() }

    override fun initViews() {
        binding?.apply {
            contactWithFilter =
                if (args.numberData is CallWithFilterUIModel) {
                    val callWithFilter = args.numberData as? CallWithFilterUIModel
                    isHiddenCall = callWithFilter?.callId.orZero() > 0 &&
                        callWithFilter?.number?.isEmpty().isTrue()
                    callWithFilter?.filterWithFilteredNumberUIModel?.let { filterWithFilteredNumberUIModel ->
                        ContactWithFilterUIModel(
                            filterWithFilteredNumberUIModel = filterWithFilteredNumberUIModel,
                            contactName =
                                callWithFilter.callName.takeIf { it.isNotEmpty() }
                                    ?: getString(R.string.details_number_from_call_log),
                            photoUrl = callWithFilter.photoUrl,
                            number = callWithFilter.number,
                            phoneNumberValue = callWithFilter.phoneNumberValue,
                            isPhoneNumberValid = callWithFilter.isPhoneNumberValid,
                        )
                    }
                } else {
                    args.numberData as ContactWithFilterUIModel
                }
            detailsNumberDataItemContact.root.isEnabled = false
            context?.let {
                contactWithFilter?.highlightedSpanned =
                    contactWithFilter?.highlightedSpanned(
                        contactWithFilter?.filterWithFilteredNumberUIModel,
                        ContextCompat.getColor(it, R.color.text_color_black),
                    )
            }
            executePendingBindings()
            if (isHiddenCall) {
                setHiddenCallScreen()
            } else {
                getData()
            }
        }
    }

    private fun setHiddenCallScreen() {
        binding?.apply {
            viewModel.getBlockHidden()
            detailsNumberDataCreatePermission.isVisible = false
            detailsNumberDataHidden.isVisible = true
            detailsNumberDataTabs.isVisible = false
            detailsNumberDataViewPager.isVisible = false
            detailsNumberDataItemContact.itemContactNumber.setText(R.string.details_number_hidden)
            with(detailsNumberDataCreateBlocker) {
                setText(R.string.settings)
                context?.let {
                    backgroundTintList = ContextCompat.getColorStateList(it, R.color.transparent)
                    setTextColor(ContextCompat.getColorStateList(it, R.color.text_color_grey))
                    strokeColor = ContextCompat.getColorStateList(it, R.color.text_color_grey)
                    icon = ContextCompat.getDrawable(it, R.drawable.ic_settings)
                }
            }
        }
    }

    override fun createAdapter() {
        filtersScreen =
            SingleDetailsFragment.newInstance(NumberDataUIModel::class.simpleName.orEmpty())
        filtersScreen?.setNumberDataClick(
            object : NumberDataClickListener {
                override fun onNumberDataClick(numberDataUIModel: NumberDataUIModel) {
                    findNavController().navigate(
                        DetailsNumberDataFragmentDirections.startDetailsFilterFragment(
                            filterWithFilteredNumberUIModel = numberDataUIModel as FilterWithFilteredNumberUIModel,
                        ),
                    )
                }
            },
        )
        filteredCallsScreen =
            SingleDetailsFragment.newInstance(CallWithFilterUIModel::class.simpleName.orEmpty())
        val fragmentList = arrayListOf(filtersScreen, filteredCallsScreen)
        binding?.detailsNumberDataViewPager?.apply {
            adapter =
                activity?.supportFragmentManager?.let { fragmentManager ->
                    DetailsPagerAdapter(fragmentList, fragmentManager, lifecycle)
                }
            offscreenPageLimit = 2
            registerOnPageChangeCallback(
                object :
                    ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        binding?.detailsNumberDataTabs?.setImageResource(
                            if (position == 0) R.drawable.ic_filter_details_tab_1 else R.drawable.ic_filter_details_tab_2,
                        )
                    }
                },
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setClickListeners() {
        binding?.apply {
            detailsNumberDataCreateBlocker.setSafeOnClickListener {
                if (isHiddenCall) {
                    findNavController().navigate(DetailsNumberDataFragmentDirections.startSettingsBlockerFragment())
                } else {
                    setAddFilterConditions(true, numberDataDetailAddFilterFull.isShown.isTrue())
                }
            }
            detailsNumberDataCreatePermission.setSafeOnClickListener {
                setAddFilterConditions(false, numberDataDetailAddFilterFull.isShown.isTrue())
            }
            numberDataDetailAddFilterFull.setSafeOnClickListener {
                createFilter(FilterCondition.FILTER_CONDITION_FULL.ordinal)
            }
            numberDataDetailAddFilterStart.setSafeOnClickListener {
                createFilter(FilterCondition.FILTER_CONDITION_START.ordinal)
            }
            numberDataDetailAddFilterContain.setSafeOnClickListener {
                createFilter(FilterCondition.FILTER_CONDITION_CONTAIN.ordinal)
            }
        }
    }

    override fun getData(allDataChange: Boolean) {
        binding?.contactWithFilter?.apply {
            viewModel.filterListWithNumber(phoneNumberValue)
            viewModel.filteredCallsByNumber(phoneNumberValue, contactName)
        }
    }

    private fun createFilter(conditionIndex: Int) {
        filterWithCountryCodeUIModel.filterWithFilteredNumberUIModel.conditionType = conditionIndex
        filterWithCountryCodeUIModel.filterWithFilteredNumberUIModel.filterType =
            if (binding?.detailsNumberDataCreateBlocker?.isEnabled.isTrue()) BLOCKER else PERMISSION
        filterWithCountryCodeUIModel.countryCodeUIModel = CountryCodeUIModel()
        filterWithCountryCodeUIModel.filterWithFilteredNumberUIModel.filter =
            binding?.contactWithFilter?.number.digitsTrimmed()
                .replace(PLUS_CHAR.toString(), String.EMPTY)
        if (conditionIndex == FilterCondition.FILTER_CONDITION_CONTAIN.ordinal) {
            startCreateFilterScreen()
        } else {
            viewModel.getCurrentCountryCode()
        }
    }

    private fun startCreateFilterScreen() {
        findNavController().navigate(
            DetailsNumberDataFragmentDirections.startCreateFilterFragment(
                filterWithCountryCodeUIModel,
            ),
        )
    }

    private fun setAddFilterConditions(
        isBlocker: Boolean,
        isShown: Boolean,
    ) {
        binding?.apply {
            detailsNumberDataCreateBlocker.changeFilterTypeButtonState(
                (isBlocker.not() && isShown.not()).not(),
                isShown.not() && isBlocker,
            )
            detailsNumberDataCreatePermission.changeFilterTypeButtonState(
                (isBlocker && isShown.not()).not(),
                isShown.not() && isBlocker.not(),
            )
            numberDataDetailAddFilterFull.changeFilterConditionButtonState(
                FilterCondition.FILTER_CONDITION_FULL.mainIcon(),
                isShown,
            )
            numberDataDetailAddFilterStart.changeFilterConditionButtonState(
                FilterCondition.FILTER_CONDITION_START.mainIcon(),
                isShown,
            )
            numberDataDetailAddFilterContain.changeFilterConditionButtonState(
                FilterCondition.FILTER_CONDITION_CONTAIN.mainIcon(),
                isShown,
            )
            changeBackgroundWithFABShow(isShown)
        }
    }

    private fun changeBackgroundWithFABShow(isShown: Boolean) {
        binding?.apply {
            detailsNumberDataItemContact.root.alpha = if (isShown) 1.0f else 0.5f
            detailsNumberDataHidden.alpha = if (isShown) 1.0f else 0.5f
            detailsNumberDataTabs.alpha = if (isShown) 1.0f else 0.5f
            detailsNumberDataViewPager.alpha = if (isShown) 1.0f else 0.5f
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            filterListLiveData.safeSingleObserve(viewLifecycleOwner) { filterList ->
                filtersScreen?.updateNumberDataList(ArrayList(filterList))
            }
            filteredCallListLiveData.safeSingleObserve(viewLifecycleOwner) { filteredCallList ->
                filteredCallsScreen?.updateNumberDataList(ArrayList(filteredCallList), true)
            }
            currentCountryCodeLiveData.safeSingleObserve(viewLifecycleOwner) { countryCode ->
                val number = binding?.contactWithFilter?.number.orEmpty()
                val phoneNumber =
                    when {
                        appPhoneNumberUtil.getPhoneNumber(number, String.EMPTY)
                            .isNull() -> appPhoneNumberUtil.getPhoneNumber(number, countryCode.country)

                        else -> appPhoneNumberUtil.getPhoneNumber(number, String.EMPTY)
                    }
                if (phoneNumber.isNull()) {
                    filterWithCountryCodeUIModel.countryCodeUIModel = countryCode
                    startCreateFilterScreen()
                } else {
                    filterWithCountryCodeUIModel.filterWithFilteredNumberUIModel.filter =
                        phoneNumber?.nationalNumber.toString()
                    viewModel.getCountryCode(phoneNumber?.countryCode)
                }
            }
            countryCodeLiveData.safeSingleObserve(viewLifecycleOwner) { countryCodeUIModel ->
                filterWithCountryCodeUIModel.countryCodeUIModel = countryCodeUIModel
                startCreateFilterScreen()
            }
            blockHiddenLiveData.safeSingleObserve(viewLifecycleOwner) { blockHidden ->
                binding?.detailsNumberDataItemContact?.itemContactFilterTitle?.setText(
                    if (blockHidden) R.string.details_number_hidden_on else R.string.details_number_hidden_off,
                )
            }
        }
    }

    override fun showInfoScreen() {
        findNavController().navigate(
            DetailsNumberDataFragmentDirections.startInfoFragment(info = Info.INFO_DETAILS_NUMBER_DATA),
        )
    }
}
