package com.tarasovvp.smartblocker.ui.main.number.details.details_number_data

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants
import com.tarasovvp.smartblocker.databinding.FragmentDetailsNumberDataBinding
import com.tarasovvp.smartblocker.enums.EmptyState
import com.tarasovvp.smartblocker.enums.FilterCondition
import com.tarasovvp.smartblocker.enums.Info
import com.tarasovvp.smartblocker.extensions.*
import com.tarasovvp.smartblocker.models.*
import com.tarasovvp.smartblocker.ui.base.BaseDetailsFragment
import com.tarasovvp.smartblocker.ui.main.number.details.DetailsPagerAdapter
import com.tarasovvp.smartblocker.ui.main.number.details.SingleDetailsFragment

class DetailsNumberDataFragment :
    BaseDetailsFragment<FragmentDetailsNumberDataBinding, DetailsNumberDataViewModel>() {

    override var layoutId = R.layout.fragment_details_number_data
    override val viewModelClass = DetailsNumberDataViewModel::class.java
    private val args: DetailsNumberDataFragmentArgs by navArgs()
    override fun setFragmentResultListeners() = Unit

    private var filtersScreen: SingleDetailsFragment? = null
    private var filteredCallsScreen: SingleDetailsFragment? = null
    private var filter: Filter? = null

    override fun initViews() {
        binding?.apply {
            contact = Contact()
            call = Call()
            when (args.numberData) {
                is Contact -> contact = args.numberData as Contact
                is LogCall,
                is FilteredCall,
                -> call = (args.numberData as Call).apply { isExtract = true }
            }
            if (call?.callId.orZero() > 0 && call?.number?.isEmpty().isTrue()) setHiddenCallScreen()
            executePendingBindings()
        }
    }

    private fun setHiddenCallScreen() {
        binding?.apply {
            detailsNumberDataCreatePermission.isVisible = false
            detailsNumberDataEmpty.emptyState = EmptyState.EMPTY_STATE_HIDDEN
            detailsNumberDataEmpty.root.isVisible = true
            detailsNumberDataTabs.isVisible = false
            detailsNumberDataViewPager.isVisible = false
            with(detailsNumberDataCreateBlocker) {
                setText(R.string.settings)
                context?.let {
                    backgroundTintList = ContextCompat.getColorStateList(it, R.color.transparent)
                    setTextColor(ContextCompat.getColorStateList(it, R.color.text_color_grey))
                    strokeColor = ContextCompat.getColorStateList(it, R.color.text_color_grey)
                    icon = ContextCompat.getDrawable(it, R.drawable.ic_settings)
                    iconTint = ContextCompat.getColorStateList(it, R.color.text_color_grey)
                }
            }
        }
    }

    override fun createAdapter() {
        filtersScreen = SingleDetailsFragment(NumberData::class.simpleName.orEmpty()) {
            findNavController().navigate(DetailsNumberDataFragmentDirections.startDetailsFilterFragment(
                filterDetails = it as Filter))
        }
        filteredCallsScreen = SingleDetailsFragment(FilteredCall::class.simpleName.orEmpty()) {
            findNavController().navigate(DetailsNumberDataFragmentDirections.startDetailsFilterFragment(
                filterDetails = it as Filter))
        }
        val fragmentList = arrayListOf(
            filtersScreen,
            filteredCallsScreen
        )

        binding?.detailsNumberDataViewPager?.adapter =
            activity?.supportFragmentManager?.let { fragmentManager ->
                DetailsPagerAdapter(
                    fragmentList,
                    fragmentManager,
                    lifecycle
                )
            }
        binding?.detailsNumberDataViewPager?.offscreenPageLimit = 2
        binding?.detailsNumberDataViewPager?.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding?.detailsNumberDataTabs?.setImageResource(if (position == 0) R.drawable.ic_filter_details_tab_1 else R.drawable.ic_filter_details_tab_2)
            }
        })
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun setClickListeners() {
        binding?.apply {
            detailsNumberDataCreateBlocker.setSafeOnClickListener {
                if (call?.callId.orZero() > 0 && call?.number?.isEmpty().isTrue()) {
                    findNavController().navigate(DetailsNumberDataFragmentDirections.startSettingsBlockerFragment())
                } else {
                    setAddFilterConditions(true, numberDataDetailAddFilterFull.isShown.isTrue())
                }
            }
            detailsNumberDataCreatePermission.setSafeOnClickListener {
                setAddFilterConditions(false, numberDataDetailAddFilterFull.isShown.isTrue())
            }
            numberDataDetailAddFilterFull.setSafeOnClickListener {
                createFilter(FilterCondition.FILTER_CONDITION_FULL.index)
            }
            numberDataDetailAddFilterStart.setSafeOnClickListener {
                createFilter(FilterCondition.FILTER_CONDITION_START.index)
            }
            numberDataDetailAddFilterContain.setSafeOnClickListener {
                createFilter(FilterCondition.FILTER_CONDITION_CONTAIN.index)
            }
        }
    }

    override fun getData() {
        val number = when (args.numberData) {
            is Contact -> binding?.contact?.trimmedPhone.orEmpty()
            is LogCall,
            is FilteredCall,
            -> binding?.call?.number.orEmpty()
            else -> String.EMPTY
        }
        viewModel.filterListWithNumber(number)
        viewModel.filteredCallsByNumber(number)
    }

    private fun getNumber(): String {
        return if (binding?.contact?.numberData.isNullOrEmpty()) binding?.call?.number.orEmpty() else binding?.contact?.trimmedPhone.orEmpty()
    }

    private fun createFilter(conditionIndex: Int) {
        val number = getNumber()
        filter = Filter(filter = number,
            conditionType = conditionIndex,
            filterType = Constants.BLOCKER)
        val phoneNumber = if (number.getPhoneNumber(String.EMPTY)
                .isNull()
        ) number.getPhoneNumber(context?.getUserCountry().orEmpty()
            .uppercase()) else number.getPhoneNumber(String.EMPTY)
        if (phoneNumber.isNull() || conditionIndex == FilterCondition.FILTER_CONDITION_CONTAIN.index) {
            startAddFilterScreen()
        } else {
            viewModel.getCountryCode(phoneNumber?.countryCode)
        }
    }

    private fun startAddFilterScreen() {
        findNavController().navigate(DetailsNumberDataFragmentDirections.startCreateFilterFragment(
            filterCreate = filter))
    }

    private fun setAddFilterConditions(isBlocker: Boolean, isShown: Boolean) {
        binding?.apply {
            detailsNumberDataCreateBlocker.changeFilterTypeButtonState((isBlocker.not() && isShown.not()).not(),
                isShown.not() && isBlocker)
            detailsNumberDataCreatePermission.changeFilterTypeButtonState((isBlocker && isShown.not()).not(),
                isShown.not() && isBlocker.not())
            numberDataDetailAddFilterFull.changeFilterConditionButtonState(if (isBlocker) FilterCondition.FILTER_CONDITION_FULL.smallBlockerIcon
            else FilterCondition.FILTER_CONDITION_FULL.smallPermissionIcon, isShown)
            numberDataDetailAddFilterStart.changeFilterConditionButtonState(if (isBlocker) FilterCondition.FILTER_CONDITION_START.smallBlockerIcon
            else FilterCondition.FILTER_CONDITION_START.smallPermissionIcon, isShown)
            numberDataDetailAddFilterContain.changeFilterConditionButtonState(if (isBlocker) FilterCondition.FILTER_CONDITION_CONTAIN.smallBlockerIcon
            else FilterCondition.FILTER_CONDITION_CONTAIN.smallPermissionIcon, isShown)
        }
    }

    private fun MaterialButton.changeFilterTypeButtonState(
        isButtonEnabled: Boolean,
        isClose: Boolean
    ) {
        backgroundTintList = ContextCompat.getColorStateList(context,
            if (isButtonEnabled) R.color.button_bg else R.color.transparent)
        strokeColor = ContextCompat.getColorStateList(context,
            if (isButtonEnabled) R.color.button_bg else R.color.comet)
        compoundDrawables.onEach { iconTint = ContextCompat.getColorStateList(context,
            if (isButtonEnabled) R.color.white else R.color.comet) }
        setTextColor(ContextCompat.getColorStateList(context,
            if (isButtonEnabled) R.color.white else R.color.comet))
        isEnabled = isButtonEnabled
        alpha = if (isButtonEnabled) 1f else 0.5f
        setText(if (isClose) R.string.number_details_close else R.string.filter_action_create)
    }

    private fun ExtendedFloatingActionButton.changeFilterConditionButtonState(
        iconRes: Int,
        isShown: Boolean,
    ) {
        setIconResource(iconRes)
        if (isShown) hide() else show()
    }

    override fun observeLiveData() {
        with(viewModel) {
            filterListLiveData.safeSingleObserve(viewLifecycleOwner) { filterList ->
                filtersScreen?.updateNumberDataList(filterList)
            }
            filteredCallListLiveData.safeSingleObserve(viewLifecycleOwner) { filteredCallList ->
                filteredCallsScreen?.updateNumberDataList(filteredCallList)
            }
            countryCodeLiveData.safeSingleObserve(viewLifecycleOwner) { countryCode ->
                filter?.countryCode = countryCode
                filter?.filter = filter?.filterToInput().orEmpty()
                startAddFilterScreen()
            }
        }
    }

    override fun showInfoScreen() {
        findNavController().navigate(DetailsNumberDataFragmentDirections.startInfoFragment(info = InfoData(
            title = getString(Info.INFO_DETAILS_NUMBER_DATA.title),
            description = getString(Info.INFO_DETAILS_NUMBER_DATA.description))))
    }
}