package com.tarasovvp.smartblocker.ui.number_data.details.filter_add

import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants
import com.tarasovvp.smartblocker.constants.Constants.COUNTRY_CODE
import com.tarasovvp.smartblocker.constants.Constants.COUNTRY_CODE_START
import com.tarasovvp.smartblocker.constants.Constants.DEFAULT_FILTER
import com.tarasovvp.smartblocker.constants.Constants.FILTER_ACTION
import com.tarasovvp.smartblocker.constants.Constants.MASK_CHAR
import com.tarasovvp.smartblocker.constants.Constants.PLUS_CHAR
import com.tarasovvp.smartblocker.constants.Constants.SPACE
import com.tarasovvp.smartblocker.databinding.FragmentFilterAddBinding
import com.tarasovvp.smartblocker.enums.EmptyState
import com.tarasovvp.smartblocker.enums.FilterAction
import com.tarasovvp.smartblocker.enums.Info
import com.tarasovvp.smartblocker.extensions.*
import com.tarasovvp.smartblocker.local.SharedPreferencesUtil
import com.tarasovvp.smartblocker.models.CountryCode
import com.tarasovvp.smartblocker.models.Filter
import com.tarasovvp.smartblocker.models.InfoData
import com.tarasovvp.smartblocker.models.NumberData
import com.tarasovvp.smartblocker.ui.MainActivity
import com.tarasovvp.smartblocker.ui.base.BaseDetailsFragment
import com.tarasovvp.smartblocker.ui.number_data.details.NumberDataAdapter
import com.tarasovvp.smartblocker.ui.number_data.details.number_data_detail.NumberDataDetailsFragmentDirections
import com.tarasovvp.smartblocker.utils.setSafeOnClickListener
import java.util.*
import kotlin.collections.ArrayList

open class FilterAddFragment :
    BaseDetailsFragment<FragmentFilterAddBinding, FilterAddViewModel>() {

    override var layoutId = R.layout.fragment_filter_add
    override val viewModelClass = FilterAddViewModel::class.java
    private val args: FilterAddFragmentArgs by navArgs()

    private var numberDataAdapter: NumberDataAdapter? = null
    private var numberDataList: ArrayList<NumberData> = ArrayList()

    override fun initViews() {
        setFilterTextChangeListener()
        binding?.filter = args.filterAdd?.apply {
            filterAction = filterAction ?: FilterAction.FILTER_ACTION_INVALID
        }
        binding?.filterAddEmptyList?.emptyState = EmptyState.EMPTY_STATE_ADD_FILTER
        binding?.executePendingBindings()
    }

    override fun setClickListeners() {
        binding?.apply {
            filterAddSubmit.setSafeOnClickListener {
                if (SmartBlockerApp.instance?.isLoggedInUser()
                        .isTrue() && SmartBlockerApp.instance?.isNetworkAvailable.isNotTrue()
                ) {
                    showMessage(getString(R.string.app_network_unavailable_repeat), true)
                } else {
                    findNavController().navigate(FilterAddFragmentDirections.startFilterActionDialog(
                        filterNumber = filter?.addFilter(),
                        filterAction = filter?.filterAction ?: if (filter?.isBlocker()
                                .isTrue()
                        ) FilterAction.FILTER_ACTION_BLOCKER_CREATE else FilterAction.FILTER_ACTION_PERMISSION_CREATE))
                }
            }
            filterAddCountryCodeSpinner.setSafeOnClickListener {
                if (findNavController().currentDestination?.navigatorName != Constants.DIALOG) {
                    findNavController().navigate(FilterAddFragmentDirections.startCountryCodeSearchDialog())
                }
            }
        }
    }

    override fun getData() {
        viewModel.getNumberDataList()
        if (binding?.filter?.isTypeContain().isNotTrue()) {
            if (binding?.filter?.countryCode?.countryCode.isNullOrEmpty()) {
                viewModel.getCountryCodeWithCountry(SharedPreferencesUtil.countryCode)
            } else {
                binding?.filter?.countryCode?.let { setCountryCode(it) }
            }
        } else {
            binding?.filterAddInput?.hint = getString(R.string.creating_filter_enter_hint)
        }
    }

    override fun setFragmentResultListeners() {
        binding?.filter?.let { filter ->
            setFragmentResultListener(COUNTRY_CODE) { _, bundle ->
                bundle.parcelable<CountryCode>(COUNTRY_CODE)?.let { setCountryCode(it) }
            }
            setFragmentResultListener(FILTER_ACTION) { _, bundle ->
                when (val filterAction =
                    bundle.serializable<FilterAction>(FILTER_ACTION)) {
                    FilterAction.FILTER_ACTION_BLOCKER_TRANSFER,
                    FilterAction.FILTER_ACTION_PERMISSION_TRANSFER,
                    -> viewModel.updateFilter(filter.apply {
                        this.filterAction = filterAction
                    })
                    FilterAction.FILTER_ACTION_BLOCKER_DELETE,
                    FilterAction.FILTER_ACTION_PERMISSION_DELETE,
                    -> viewModel.deleteFilter(filter.apply {
                        this.filter = addFilter()
                        this.filterAction = filterAction
                    })
                    FilterAction.FILTER_ACTION_BLOCKER_CREATE,
                    FilterAction.FILTER_ACTION_PERMISSION_CREATE,
                    -> viewModel.insertFilter(filter.apply {
                        numberData = filter.addFilter()
                        this.filter = addFilter()
                        filterWithoutCountryCode = extractFilterWithoutCountryCode()
                        this.filterAction = filterAction
                        created = Date().time
                    })
                    else -> Unit
                }
            }
        }
    }

    private fun setFilterTextChangeListener() {
        binding?.apply {
            container.hideKeyboardWithLayoutTouch()
            filterAddContactList.hideKeyboardWithLayoutTouch()
            filterAddInput.setupClearButtonWithAction()
            filterAddInput.doAfterTextChanged {
                if ((filter?.conditionTypeFullHint() == it.toString() && filter?.isTypeFull()
                        .isTrue())
                    || (filter?.conditionTypeStartHint() == it.toString() && filter?.isTypeStart()
                        .isTrue())
                ) return@doAfterTextChanged
                filterToInput = false
                filter = filter?.apply {
                    filter = filterAddInput.inputText().replace(MASK_CHAR.toString(), String.EMPTY)
                        .replace(SPACE, String.EMPTY)
                    viewModel.checkFilterExist(this)
                }
                viewModel.filterNumberDataList(filter, numberDataList)
            }
        }
    }

    override fun createAdapter() {
        numberDataAdapter = numberDataAdapter ?: NumberDataAdapter(numberDataList) { numberData ->
            binding?.apply {
                binding?.filterToInput = true
                if (filter?.isTypeContain().isTrue()) {
                    filter = filter?.apply {
                        this.filter =
                            numberData.numberData.digitsTrimmed()
                                .replace(PLUS_CHAR.toString(), String.EMPTY)
                    }
                } else {
                    val phoneNumber =
                        numberData.numberData.getPhoneNumber(filter?.countryCode?.country.orEmpty())
                    if ((phoneNumber?.nationalNumber.toString() == filterAddInput.getRawText() &&
                                String.format(COUNTRY_CODE_START,
                                    phoneNumber?.countryCode.toString()) == filterAddCountryCodeValue.text.toString()).not()
                    ) {
                        filter = filter?.apply {
                            this.filter =
                                phoneNumber?.nationalNumber?.toString()
                                    ?: numberData.numberData.digitsTrimmed()
                        }
                        filterAddCountryCodeSpinner.text =
                            if (phoneNumber?.countryCode.isNull()) binding?.filter?.countryCode?.countryCode else String.format(
                                COUNTRY_CODE_START,
                                phoneNumber?.countryCode.toString())
                    }
                }
            }
        }
        binding?.filterAddContactList?.adapter = numberDataAdapter
    }

    private fun setCountryCode(countryCode: CountryCode) {
        binding?.apply {
            filter = filter?.apply {
                this.countryCode = countryCode
                if (isTypeFull().isTrue()) {
                    filterAddInput.setNumberMask(conditionTypeFullHint())
                } else if (isTypeStart().isTrue()) {
                    filterAddInput.setNumberMask(conditionTypeStartHint())
                }
            }
            filterAddCountryCodeSpinner.text = countryCode.countryEmoji()
            viewModel.filterNumberDataList(filter, numberDataList)
        }
    }

    private fun filterNumberDataList(filteredList: ArrayList<NumberData>) {
        binding?.apply {
            numberDataAdapter?.numberDataList = filteredList
            numberDataAdapter?.notifyDataSetChanged()
            filterAddContactList.isVisible = filteredList.isEmpty().not()
            filterAddEmptyList.root.isVisible = filteredList.isEmpty()
            viewModel.hideProgress()
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            countryCodeLiveData.safeSingleObserve(viewLifecycleOwner) { countryCode ->
                setCountryCode(countryCode)
            }
            numberDataListLiveData.safeSingleObserve(viewLifecycleOwner) { numberDataList ->
                this@FilterAddFragment.numberDataList = ArrayList(numberDataList)
                if (binding?.filter?.isTypeContain().isTrue().not()) {
                    filterNumberDataList(binding?.filter, this@FilterAddFragment.numberDataList)
                } else {
                    numberDataAdapter?.numberDataList = this@FilterAddFragment.numberDataList
                    numberDataAdapter?.notifyDataSetChanged()
                }
                binding?.filterToInput = true
                binding?.filter = binding?.filter
            }
            existingFilterLiveData.safeSingleObserve(viewLifecycleOwner) { existingFilter ->
                binding?.filter = binding?.filter?.apply {
                    filterAction = when (existingFilter.filterType) {
                        DEFAULT_FILTER -> if (isInValidPhoneNumber().isTrue()) FilterAction.FILTER_ACTION_INVALID else if (isBlocker()) FilterAction.FILTER_ACTION_BLOCKER_CREATE else FilterAction.FILTER_ACTION_PERMISSION_CREATE
                        filterType -> if (isBlocker()) FilterAction.FILTER_ACTION_BLOCKER_DELETE else FilterAction.FILTER_ACTION_PERMISSION_DELETE
                        else -> if (isBlocker()) FilterAction.FILTER_ACTION_BLOCKER_TRANSFER else FilterAction.FILTER_ACTION_PERMISSION_TRANSFER
                    }
                }
            }
            filteredNumberDataListLiveData.safeSingleObserve(viewLifecycleOwner) { filteredNumberDataList ->
                filterNumberDataList(filteredNumberDataList)
            }
            filterActionLiveData.safeSingleObserve(viewLifecycleOwner) { filter ->
                handleSuccessFilterAction(filter)
            }
        }
    }

    private fun handleSuccessFilterAction(filter: Filter) {
        (activity as MainActivity).apply {
            showMessage(String.format(filter.filterAction?.successText?.let { getString(it) }
                .orEmpty(),
                filter.filter), false)
            showInterstitial()
            getAllData()
            findNavController().navigate(if (binding?.filter?.isBlocker().isTrue())
                FilterAddFragmentDirections.startListBlockerFragment()
            else FilterAddFragmentDirections.startListPermissionFragment())
        }
    }

    override fun showInfoScreen() {
        val info = when {
            binding?.filter?.isTypeStart().isTrue() -> Info.INFO_FILTER_ADD_START
            binding?.filter?.isTypeContain().isTrue() -> Info.INFO_FILTER_ADD_CONTAIN
            else -> Info.INFO_FILTER_ADD_FULL
        }
        findNavController().navigate(NumberDataDetailsFragmentDirections.startInfoFragment(info = InfoData(
            title = getString(info.title),
            description = getString(info.description))))
    }
}
