package com.tarasovvp.smartblocker.ui.main.number.create

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants
import com.tarasovvp.smartblocker.constants.Constants.COUNTRY_CODE
import com.tarasovvp.smartblocker.constants.Constants.COUNTRY_CODE_START
import com.tarasovvp.smartblocker.constants.Constants.DEFAULT_FILTER
import com.tarasovvp.smartblocker.constants.Constants.FILTER_ACTION
import com.tarasovvp.smartblocker.constants.Constants.PLUS_CHAR
import com.tarasovvp.smartblocker.databinding.FragmentCreateFilterBinding
import com.tarasovvp.smartblocker.enums.EmptyState
import com.tarasovvp.smartblocker.enums.FilterAction
import com.tarasovvp.smartblocker.enums.Info
import com.tarasovvp.smartblocker.extensions.*
import com.tarasovvp.smartblocker.local.SharedPrefs
import com.tarasovvp.smartblocker.models.*
import com.tarasovvp.smartblocker.ui.MainActivity
import com.tarasovvp.smartblocker.ui.base.BaseDetailsFragment
import com.tarasovvp.smartblocker.ui.main.number.details.NumberDataAdapter
import com.tarasovvp.smartblocker.ui.main.number.details.details_number_data.DetailsNumberDataFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
open class CreateFilterFragment :
    BaseDetailsFragment<FragmentCreateFilterBinding, CreateFilterViewModel>() {

    override var layoutId = R.layout.fragment_create_filter
    override val viewModelClass = CreateFilterViewModel::class.java

    private val args: CreateFilterFragmentArgs by navArgs()

    private var numberDataAdapter: NumberDataAdapter? = null
    private var numberDataList: ArrayList<NumberData> = ArrayList()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as MainActivity).setMainProgressVisibility(true)
    }

    override fun createAdapter() {
        numberDataAdapter = numberDataAdapter ?: NumberDataAdapter(numberDataList) { numberData ->
            val number = when (numberData) {
                is ContactWithFilter -> numberData.contact?.number
                is LogCallWithFilter -> numberData.call?.number
                else -> String.EMPTY
            }
            binding?.apply {
                binding?.filterToInput = true
                if (filterWithCountryCode?.filter?.isTypeContain().isTrue()) {
                    filterWithCountryCode?.filter = filterWithCountryCode?.filter?.apply {
                        this.filter =
                            number.digitsTrimmed()
                                .replace(PLUS_CHAR.toString(), String.EMPTY)
                    }
                } else {
                    val phoneNumber =
                        number.getPhoneNumber(filterWithCountryCode?.countryCode?.country.orEmpty())
                    if ((phoneNumber?.nationalNumber.toString() == createFilterInput.getRawText() &&
                                String.format(COUNTRY_CODE_START,
                                    phoneNumber?.countryCode.toString()) == createFilterCountryCodeValue.text.toString()).not()
                    ) {
                        filterWithCountryCode?.filter = filterWithCountryCode?.filter?.apply {
                            this.filter =
                                phoneNumber?.nationalNumber?.toString()
                                    ?: number.digitsTrimmed()
                        }
                        if (phoneNumber?.countryCode.orZero() > 0) {
                            viewModel.getCountryCodeWithCode(phoneNumber?.countryCode)
                        }
                    }
                    binding?.executePendingBindings()
                }
            }
        }
        binding?.createFilterNumberList?.adapter = numberDataAdapter
    }

    override fun initViews() {
        Log.e("createFilterTAG", "CreateFilterFragment initViews")
        binding?.filterWithCountryCode = args.filterWithCountryCode?.apply {
            filter?.filterAction = filter?.filterAction ?: FilterAction.FILTER_ACTION_INVALID
        }
        (activity as MainActivity).toolbar?.title = getString(if (binding?.filterWithCountryCode?.filter?.isBlocker()
                .isTrue()
        ) R.string.creating_blocker else R.string.creating_permission)
        setFilterTextChangeListener()
        binding?.createFilterEmptyList?.emptyState = EmptyState.EMPTY_STATE_ADD_FILTER
        binding?.executePendingBindings()
    }

    override fun setClickListeners() {
        binding?.apply {
            createFilterSubmit.setSafeOnClickListener {
                    findNavController().navigate(CreateFilterFragmentDirections.startFilterActionDialog(
                        filterWithCountryCode = filterWithCountryCode?.apply {
                            filter?.filter = filterWithCountryCode?.createFilter().orEmpty()
                            filter?.filterAction = filter?.filterAction
                                ?: if (filter?.isBlocker().isTrue()) FilterAction.FILTER_ACTION_BLOCKER_CREATE else FilterAction.FILTER_ACTION_PERMISSION_CREATE
                        }))
            }
            createFilterCountryCodeSpinner.setSafeOnClickListener {
                if (findNavController().currentDestination?.navigatorName != Constants.DIALOG) {
                    findNavController().navigate(CreateFilterFragmentDirections.startCountryCodeSearchDialog())
                }
            }
        }
    }

    override fun getData() {
        Log.e("createFilterTAG", "CreateFilterFragment getData")
        viewModel.getNumberDataList()
        if (binding?.filterWithCountryCode?.filter?.isTypeContain().isNotTrue()) {
            if (binding?.filterWithCountryCode?.countryCode?.countryCode.isNullOrEmpty()) {
                viewModel.getCountryCodeWithCountry(SharedPrefs.country)
            } else {
                binding?.filterWithCountryCode?.countryCode?.let { setCountryCode(it) }
            }
        } else {
            binding?.createFilterInput?.hint = getString(R.string.creating_filter_enter_hint)
        }
    }

    override fun setFragmentResultListeners() {
        binding?.filterWithCountryCode?.let { filter ->
            setFragmentResultListener(COUNTRY_CODE) { _, bundle ->
                bundle.parcelable<CountryCode>(COUNTRY_CODE)?.let { setCountryCode(it) }
            }
            setFragmentResultListener(FILTER_ACTION) { _, bundle ->
                when (val filterAction =
                    bundle.serializable<FilterAction>(FILTER_ACTION)) {
                    FilterAction.FILTER_ACTION_BLOCKER_TRANSFER,
                    FilterAction.FILTER_ACTION_PERMISSION_TRANSFER,
                    -> viewModel.updateFilter(filter.filter?.apply {
                        this.filterAction = filterAction
                    })
                    FilterAction.FILTER_ACTION_BLOCKER_DELETE,
                    FilterAction.FILTER_ACTION_PERMISSION_DELETE,
                    -> viewModel.deleteFilter(filter.filter?.apply {
                        this.filter = filter.createFilter()
                        this.filterAction = filterAction
                    })
                    FilterAction.FILTER_ACTION_BLOCKER_CREATE,
                    FilterAction.FILTER_ACTION_PERMISSION_CREATE,
                    -> viewModel.createFilter(filter.filter?.apply {
                        this.filter = filter.createFilter()
                        filterWithoutCountryCode = filter.extractFilterWithoutCountryCode()
                        this.filterAction = filterAction
                        created = Date().time
                        country = filter.countryCode?.country.orEmpty()
                        countryCode = filter.countryCode?.countryCode.orEmpty()
                    })
                    else -> Unit
                }
            }
        }
    }

    private fun setFilterTextChangeListener() {
        binding?.apply {
            container.hideKeyboardWithLayoutTouch()
            createFilterNumberList.hideKeyboardWithLayoutTouch()
            createFilterInput.setupClearButtonWithAction()
            createFilterInput.doAfterTextChanged {
                Log.e("createFilterTAG", "CreateFilterFragment setFilterTextChangeListener doAfterTextChanged it $it")
                if ((filterWithCountryCode?.conditionTypeFullHint() == it.toString() && filterWithCountryCode?.filter?.isTypeFull().isTrue())
                    || (filterWithCountryCode?.conditionTypeStartHint() == it.toString() && filterWithCountryCode?.filter?.isTypeStart().isTrue())
                ) return@doAfterTextChanged
                Log.e("createFilterTAG", "CreateFilterFragment setFilterTextChangeListener true condition")
                filterToInput = false
                filterWithCountryCode = filterWithCountryCode?.apply {
                    filter?.filter = createFilterInput.inputText().replace(Constants.MASK_CHAR.toString(), String.EMPTY)
                        .replace(Constants.SPACE, String.EMPTY)
                    viewModel.checkFilterExist(this)
                }
                context?.let { context ->
                    viewModel.filterNumberDataList(filterWithCountryCode, numberDataList,
                    ContextCompat.getColor(context, R.color.text_color_black)) }
            }
        }
    }

    private fun setCountryCode(countryCode: CountryCode) {
        Log.e("createFilterTAG", "CreateFilterFragment setCountryCode countryCode.country ${countryCode.country}")
        binding?.apply {
            filterWithCountryCode?.filter = filterWithCountryCode?.filter?.apply {
                filterWithCountryCode?.countryCode = countryCode
                if (isTypeFull().isTrue()) {
                    createFilterInput.setNumberMask(filterWithCountryCode?.conditionTypeFullHint().orEmpty())
                } else if (isTypeStart().isTrue()) {
                    createFilterInput.setNumberMask(filterWithCountryCode?.conditionTypeStartHint().orEmpty())
                }
            }
            createFilterCountryCodeSpinner.text = countryCode.countryEmoji()
            context?.let {viewModel.filterNumberDataList(filterWithCountryCode, numberDataList,
                ContextCompat.getColor(it, R.color.text_color_black)) }
        }
    }

    private fun filterNumberDataList(filteredList: ArrayList<NumberData>) {
        Log.e("createFilterTAG", "CreateFilterFragment filterNumberDataList filteredList ${filteredList.size}")
        binding?.apply {
            numberDataAdapter?.numberDataList = filteredList
            numberDataAdapter?.notifyDataSetChanged()
            createFilterNumberList.isVisible = filteredList.isEmpty().not()
            createFilterEmptyList.root.isVisible = filteredList.isEmpty()
            viewModel.hideProgress()
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            countryCodeLiveData.safeSingleObserve(viewLifecycleOwner) { countryCode ->
                Log.e("createFilterTAG", "CreateFilterFragment observeLiveData countryCodeLiveData")
                setCountryCode(countryCode)
            }
            numberDataListLiveData.safeSingleObserve(viewLifecycleOwner) { numberDataList ->
                Log.e("createFilterTAG", "CreateFilterFragment observeLiveData numberDataListLiveData")
                this@CreateFilterFragment.numberDataList = ArrayList(numberDataList)
                if (binding?.filterWithCountryCode?.filter?.isTypeContain().isTrue().not()) {
                    context?.let {viewModel.filterNumberDataList(binding?.filterWithCountryCode, this@CreateFilterFragment.numberDataList,
                        ContextCompat.getColor(it, R.color.text_color_black)) }
                } else {
                    numberDataAdapter?.numberDataList = this@CreateFilterFragment.numberDataList
                    numberDataAdapter?.notifyDataSetChanged()
                }
                binding?.filterToInput = true
                binding?.filterWithCountryCode?.filter = binding?.filterWithCountryCode?.filter
                (activity as MainActivity).setMainProgressVisibility(false)
            }
            existingFilterLiveData.safeSingleObserve(viewLifecycleOwner) { existingFilter ->
                Log.e("createFilterTAG", "CreateFilterFragment observeLiveData existingFilterLiveData existingFilter $existingFilter")
                binding?.filterWithCountryCode?.filter = binding?.filterWithCountryCode?.filter?.apply {
                    filterAction = when (existingFilter.filter?.filterType) {
                        DEFAULT_FILTER -> if (binding?.filterWithCountryCode?.isInValidPhoneNumber().isTrue()) FilterAction.FILTER_ACTION_INVALID else if (isBlocker()) FilterAction.FILTER_ACTION_BLOCKER_CREATE else FilterAction.FILTER_ACTION_PERMISSION_CREATE
                        filterType -> if (isBlocker()) FilterAction.FILTER_ACTION_BLOCKER_DELETE else FilterAction.FILTER_ACTION_PERMISSION_DELETE
                        else -> if (isBlocker()) FilterAction.FILTER_ACTION_PERMISSION_TRANSFER else FilterAction.FILTER_ACTION_BLOCKER_TRANSFER
                    }
                }
            }
            filteredNumberDataListLiveData.safeSingleObserve(viewLifecycleOwner) { filteredNumberDataList ->
                Log.e("createFilterTAG", "CreateFilterFragment observeLiveData filteredNumberDataListLiveData")
                filterNumberDataList(filteredNumberDataList)
            }
            filterActionLiveData.safeSingleObserve(viewLifecycleOwner) { filter ->
                Log.e("createFilterTAG", "CreateFilterFragment observeLiveData filterActionLiveData")
                handleSuccessFilterAction(filter)
            }
        }
    }

    private fun handleSuccessFilterAction(filter: Filter) {
        Log.e("createFilterTAG", "CreateFilterFragment handleSuccessFilterAction")
        (activity as MainActivity).apply {
            showInfoMessage(String.format(filter.filterAction?.successText?.let { getString(it) }
                .orEmpty(),
                filter.filter, getString(filter.conditionTypeName())), false)
            showInterstitial()
            getAllData()
            findNavController().navigate(if (binding?.filterWithCountryCode?.filter?.isBlocker().isTrue())
                CreateFilterFragmentDirections.startListBlockerFragment()
            else CreateFilterFragmentDirections.startListPermissionFragment())
        }
    }

    override fun showInfoScreen() {
        Log.e("createFilterTAG", "CreateFilterFragment showInfoScreen")
        val info = when {
            binding?.filterWithCountryCode?.filter?.isTypeStart().isTrue() -> Info.INFO_CREATE_FILTER_START
            binding?.filterWithCountryCode?.filter?.isTypeContain().isTrue() -> Info.INFO_CREATE_FILTER_CONTAIN
            else -> Info.INFO_CREATE_FILTER_FULL
        }
        findNavController().navigate(DetailsNumberDataFragmentDirections.startInfoFragment(info = InfoData(
            title = getString(info.title),
            description = getString(info.description))))
    }
}
