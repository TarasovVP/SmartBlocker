package com.tarasovvp.smartblocker.presentation.main.number.details.details_number_data

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.entities.db_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.entities.db_views.FilteredCallWithFilter
import com.tarasovvp.smartblocker.domain.entities.models.CallWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_entities.Contact
import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.databinding.FragmentDetailsNumberDataBinding
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.domain.enums.Info
import com.tarasovvp.smartblocker.domain.entities.models.InfoData
import com.tarasovvp.smartblocker.domain.entities.models.NumberData
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PERMISSION
import com.tarasovvp.smartblocker.infrastructure.prefs.SharedPrefs
import com.tarasovvp.smartblocker.presentation.base.BaseDetailsFragment
import com.tarasovvp.smartblocker.presentation.main.number.details.DetailsPagerAdapter
import com.tarasovvp.smartblocker.presentation.main.number.details.NumberDataClickListener
import com.tarasovvp.smartblocker.presentation.main.number.details.SingleDetailsFragment
import com.tarasovvp.smartblocker.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsNumberDataFragment :
    BaseDetailsFragment<FragmentDetailsNumberDataBinding, DetailsNumberDataViewModel>() {

    override var layoutId = R.layout.fragment_details_number_data
    override val viewModelClass = DetailsNumberDataViewModel::class.java
    private val args: DetailsNumberDataFragmentArgs by navArgs()
    override fun setFragmentResultListeners() = Unit

    private var filtersScreen: SingleDetailsFragment? = null
    private var filteredCallsScreen: SingleDetailsFragment? = null
    private var filterWithCountryCode: FilterWithCountryCode? = null
    private var isHiddenCall = false

    override fun initViews() {
        binding?.apply {
            contactWithFilter = if (args.numberData is CallWithFilter) {
                val callWithFilter = args.numberData as? CallWithFilter
                isHiddenCall = callWithFilter?.call?.callId.orZero() > 0
                        && callWithFilter?.call?.number?.isEmpty().isTrue()
                ContactWithFilter(filterWithCountryCode = callWithFilter?.filterWithCountryCode,
                    contact = Contact(name = getString(R.string.details_number_from_call_log),
                    photoUrl = callWithFilter?.call?.photoUrl,
                    number = callWithFilter?.call?.number.orEmpty(),
                filter = callWithFilter?.filterWithCountryCode?.filter?.filter.orEmpty()))
            } else {
                args.numberData as ContactWithFilter
            }
            detailsNumberDataItemContact.root.isEnabled = false
            context?.let { contactWithFilter?.highlightedSpanned = contactWithFilter?.highlightedSpanned(contactWithFilter?.filterWithCountryCode?.filter, ContextCompat.getColor(it, R.color.sunset)) }
            executePendingBindings()
            if (isHiddenCall) setHiddenCallScreen()
        }
    }

    private fun setHiddenCallScreen() {
        binding?.apply {
            detailsNumberDataCreatePermission.isVisible = false
            detailsNumberDataHidden.isVisible = true
            detailsNumberDataTabs.isVisible = false
            detailsNumberDataViewPager.isVisible = false
            detailsNumberDataItemContact.itemContactNumber.setText(R.string.details_number_hidden)
            detailsNumberDataItemContact.itemContactFilterTitle.setText(if (SharedPrefs.blockHidden.isTrue()) R.string.details_number_hidden_on else R.string.details_number_hidden_off)
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
        filtersScreen = SingleDetailsFragment.newInstance(NumberData::class.simpleName.orEmpty())
        filtersScreen?.setNumberDataClick(object : NumberDataClickListener {
            override fun onNumberDataClick(numberData: NumberData) {
                findNavController().navigate(
                    DetailsNumberDataFragmentDirections.startDetailsFilterFragment(
                        filterWithCountryCode = numberData as FilterWithCountryCode
                    )
                )
            }
        })
        filteredCallsScreen =
            SingleDetailsFragment.newInstance(FilteredCallWithFilter::class.simpleName.orEmpty())
        val fragmentList = arrayListOf(filtersScreen, filteredCallsScreen)

        binding?.detailsNumberDataViewPager?.adapter =
            activity?.supportFragmentManager?.let { fragmentManager ->
                DetailsPagerAdapter(fragmentList, fragmentManager, lifecycle)
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

    override fun getData() {
        viewModel.filterListWithNumber(binding?.contactWithFilter?.contact?.phoneNumberValue().orEmpty())
        viewModel.filteredCallsByNumber(binding?.contactWithFilter?.contact?.phoneNumberValue().orEmpty())
    }

    private fun createFilter(conditionIndex: Int) {
        val number = binding?.contactWithFilter?.contact?.number.orEmpty()
        filterWithCountryCode = FilterWithCountryCode(filter = Filter(
            filter = number,
            conditionType = conditionIndex,
            filterType = if (binding?.detailsNumberDataCreateBlocker?.isEnabled.isTrue()) BLOCKER else PERMISSION))
        val phoneNumber = if (number.getPhoneNumber(String.EMPTY).isNull()) number.getPhoneNumber(context?.getUserCountry().orEmpty().uppercase())
        else number.getPhoneNumber(String.EMPTY)
        if (phoneNumber.isNull() || conditionIndex == FilterCondition.FILTER_CONDITION_CONTAIN.ordinal) {
            startAddFilterScreen()
        } else {
            viewModel.getCountryCode(phoneNumber?.countryCode)
        }
    }

    private fun startAddFilterScreen() {
        findNavController().navigate(
            DetailsNumberDataFragmentDirections.startCreateFilterFragment(
                filterWithCountryCode = filterWithCountryCode
            )
        )
    }

    private fun setAddFilterConditions(isBlocker: Boolean, isShown: Boolean) {
        binding?.apply {
            detailsNumberDataCreateBlocker.changeFilterTypeButtonState(
                (isBlocker.not() && isShown.not()).not(),
                isShown.not() && isBlocker
            )
            detailsNumberDataCreatePermission.changeFilterTypeButtonState(
                (isBlocker && isShown.not()).not(),
                isShown.not() && isBlocker.not()
            )
            numberDataDetailAddFilterFull.changeFilterConditionButtonState(
                FilterCondition.FILTER_CONDITION_FULL.mainIcon(), isShown
            )
            numberDataDetailAddFilterStart.changeFilterConditionButtonState(
                FilterCondition.FILTER_CONDITION_START.mainIcon(), isShown
            )
            numberDataDetailAddFilterContain.changeFilterConditionButtonState(
                FilterCondition.FILTER_CONDITION_CONTAIN.mainIcon(), isShown
            )
        }
    }

    private fun MaterialButton.changeFilterTypeButtonState(
        isButtonEnabled: Boolean,
        isClose: Boolean,
    ) {
        backgroundTintList = ContextCompat.getColorStateList(
            context,
            if (isButtonEnabled) R.color.button_bg else R.color.transparent
        )
        strokeColor = ContextCompat.getColorStateList(
            context,
            if (isButtonEnabled) R.color.button_bg else R.color.comet
        )
        compoundDrawables.onEach {
            iconTint = ContextCompat.getColorStateList(
                context,
                if (isButtonEnabled) R.color.white else R.color.comet
            )
        }
        setTextColor(
            ContextCompat.getColorStateList(
                context,
                if (isButtonEnabled) R.color.white else R.color.comet
            )
        )
        isEnabled = isButtonEnabled
        alpha = if (isButtonEnabled) 1f else 0.5f
        setText(if (isClose) R.string.number_details_close else R.string.filter_action_create)
    }

    private fun ExtendedFloatingActionButton.changeFilterConditionButtonState(
        iconRes: Int?,
        isShown: Boolean,
    ) {
        iconRes?.let { setIconResource(it) }
        if (isShown) hide() else show()
    }

    override fun observeLiveData() {
        with(viewModel) {
            filterListLiveData.safeSingleObserve(viewLifecycleOwner) { filterList ->
                filtersScreen?.updateNumberDataList(filterList)
            }
            filteredCallListLiveData.safeSingleObserve(viewLifecycleOwner) { filteredCallList ->
                filteredCallsScreen?.updateNumberDataList(filteredCallList, true)
            }
            countryCodeLiveData.safeSingleObserve(viewLifecycleOwner) { countryCode ->
                filterWithCountryCode?.filter?.country = countryCode.country
                filterWithCountryCode?.countryCode = countryCode
                filterWithCountryCode?.filter?.filter = filterWithCountryCode?.filterToInput().orEmpty()
                startAddFilterScreen()
            }
        }
    }

    override fun showInfoScreen() {
        findNavController().navigate(
            DetailsNumberDataFragmentDirections.startInfoFragment(
                info = InfoData(
                    title = getString(Info.INFO_DETAILS_NUMBER_DATA.title()),
                    description = getString(Info.INFO_DETAILS_NUMBER_DATA.description())
                )
            )
        )
    }
}