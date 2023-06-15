package com.tarasovvp.smartblocker.presentation.main.number.create

import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentCreateFilterBinding
import com.tarasovvp.smartblocker.domain.enums.FilterAction
import com.tarasovvp.smartblocker.domain.enums.Info
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.COUNTRY_CODE
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.COUNTRY_CODE_START
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.DEFAULT_FILTER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTER_ACTION
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PLUS_CHAR
import com.tarasovvp.smartblocker.presentation.base.BaseDetailsFragment
import com.tarasovvp.smartblocker.presentation.main.MainActivity
import com.tarasovvp.smartblocker.presentation.main.number.details.details_number_data.DetailsNumberDataFragmentDirections
import com.tarasovvp.smartblocker.presentation.ui_models.*
import com.tarasovvp.smartblocker.utils.AppPhoneNumberUtil
import com.tarasovvp.smartblocker.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
open class CreateFilterFragment :
    BaseDetailsFragment<FragmentCreateFilterBinding, CreateFilterViewModel>() {

    @Inject
    lateinit var appPhoneNumberUtil: AppPhoneNumberUtil

    override var layoutId = R.layout.fragment_create_filter
    override val viewModelClass = CreateFilterViewModel::class.java

    private val args: CreateFilterFragmentArgs by navArgs()

    private var createFilterAdapter: CreateFilterAdapter? = null
    private var numberDataUIModelList: ArrayList<ContactWithFilterUIModel> = ArrayList()

    override fun createAdapter() {
        createFilterAdapter = createFilterAdapter ?: CreateFilterAdapter(numberDataUIModelList) { contactWithFilter ->
            binding?.apply {
                binding?.filterToInput = true
                filterWithCountryCode?.filterWithFilteredNumberUIModel?.apply {
                    if (filterWithCountryCode?.filterWithFilteredNumberUIModel?.isTypeContain().isTrue()) {
                        filterWithCountryCode?.filterWithFilteredNumberUIModel = this.apply {
                            this.filter = contactWithFilter.number.digitsTrimmed().replace(PLUS_CHAR.toString(), String.EMPTY)
                        }
                    } else {
                        val phoneNumber = appPhoneNumberUtil.getPhoneNumber(contactWithFilter.number, filterWithCountryCode?.countryCodeUIModel?.country.orEmpty())
                        if ((phoneNumber?.nationalNumber.toString() == createFilterInput.getRawText() && String.format(COUNTRY_CODE_START, phoneNumber?.countryCode.toString()) == createFilterCountryCodeValue.text.toString()).not()) {
                            filterWithCountryCode?.filterWithFilteredNumberUIModel = this.apply {
                                this.filter = phoneNumber?.nationalNumber?.toString() ?: contactWithFilter.number.digitsTrimmed()
                            }
                            if (phoneNumber?.countryCode.orZero() > 0) {
                                viewModel.getCountryCodeWithCode(phoneNumber?.countryCode)
                            }
                        }
                        binding?.executePendingBindings()
                    }
                }
            }
        }
        binding?.createFilterNumberList?.adapter = createFilterAdapter
    }

    override fun initViews() {
        Timber.e("CreateFilterFragment initViews args.filterWithCountryCodeUIModel ${args.filterWithCountryCodeUIModel}")
        binding?.apply {
            filterToInput = true
            filterWithCountryCode = args.filterWithCountryCodeUIModel?.apply {
                filterWithFilteredNumberUIModel.filterAction = this.filterWithFilteredNumberUIModel.filterAction ?: FilterAction.FILTER_ACTION_INVALID
            }
            Timber.e( "CreateFilterFragment initViews filter?.filterAction ${filterWithCountryCode?.filterWithFilteredNumberUIModel?.filterAction}")
            if (filterWithCountryCode?.filterWithFilteredNumberUIModel?.isTypeContain().isTrue()) {
                createFilterInput.setHint(R.string.creating_filter_enter_hint)
            } else {
                setCountryCode(filterWithCountryCode?.countryCodeUIModel)
            }
            (activity as? MainActivity)?.toolbar?.title = getString(if (filterWithCountryCode?.filterWithFilteredNumberUIModel?.isBlocker().isTrue()) R.string.creating_blocker else R.string.creating_permission)
            setKeyboardHidden()
            setFilterTextChangeListener()
            executePendingBindings()
            Timber.e("CreateFilterFragment initViews filterWithFilteredNumberUIModel.filter ${filterWithCountryCode?.filterWithFilteredNumberUIModel?.filter}")
        }
    }

    private fun setKeyboardHidden() {
        binding?.apply {
            container.hideKeyboardWithLayoutTouch()
            createFilterNumberList.hideKeyboardWithLayoutTouch()
            createFilterInput.setupClearButtonWithAction()
        }
    }
    override fun setClickListeners() {
        binding?.apply {
            createFilterSubmit.setSafeOnClickListener {
                findNavController().navigate(CreateFilterFragmentDirections.startFilterActionDialog(filterWithFilteredNumberUIModel = filterWithCountryCode?.filterWithFilteredNumberUIModel?.apply {
                    filter = filterWithCountryCode?.createFilter().orEmpty()
                    filterAction = filterAction ?: if (isBlocker()) FilterAction.FILTER_ACTION_BLOCKER_CREATE else FilterAction.FILTER_ACTION_PERMISSION_CREATE }))
            }
            createFilterCountryCodeValue.setSafeOnClickListener {
                findNavController().navigate(CreateFilterFragmentDirections.startCountryCodeSearchDialog())
            }
            createFilterCountryCodeSpinner.setSafeOnClickListener {
                findNavController().navigate(CreateFilterFragmentDirections.startCountryCodeSearchDialog())
            }
        }
    }

    override fun setFragmentResultListeners() {
        binding?.filterWithCountryCode?.let { filterWithCountryCode ->
            setFragmentResultListener(COUNTRY_CODE) { _, bundle ->
                bundle.parcelable<CountryCodeUIModel>(COUNTRY_CODE)?.let { setCountryCode(it) }
            }
            setFragmentResultListener(FILTER_ACTION) { _, bundle ->
                when (val filterAction = bundle.serializable<FilterAction>(FILTER_ACTION)) {
                    FilterAction.FILTER_ACTION_BLOCKER_TRANSFER,
                    FilterAction.FILTER_ACTION_PERMISSION_TRANSFER,
                    -> viewModel.updateFilter(filterWithCountryCode.filterWithFilteredNumberUIModel.apply {
                        this.filterAction = filterAction
                    })
                    FilterAction.FILTER_ACTION_BLOCKER_DELETE,
                    FilterAction.FILTER_ACTION_PERMISSION_DELETE,
                    -> viewModel.deleteFilter(filterWithCountryCode.filterWithFilteredNumberUIModel.apply {
                        this.filterAction = filterAction
                    })
                    FilterAction.FILTER_ACTION_BLOCKER_CREATE,
                    FilterAction.FILTER_ACTION_PERMISSION_CREATE,
                    -> viewModel.createFilter(filterWithCountryCode.filterWithFilteredNumberUIModel.apply {
                        this.filterAction = filterAction
                        this.created = Date().time
                        this.country = filterWithCountryCode.countryCodeUIModel.country
                        this.countryCode = filterWithCountryCode.countryCodeUIModel.countryCode
                    })
                    else -> Unit
                }
            }
        }
    }

    override fun getData() {
        Timber.e( "CreateFilterFragment getData")
        viewModel.getMatchedContactWithFilterList(binding?.filterWithCountryCode)
    }

    private fun setFilterTextChangeListener() {
        binding?.apply {
            createFilterInput.doAfterTextChanged {
                Timber.e( "CreateFilterFragment setFilterTextChangeListener doAfterTextChanged it $it")
                if (isHintInput(filterWithCountryCode, it.toString())) return@doAfterTextChanged
                Timber.e("CreateFilterFragment setFilterTextChangeListener true condition")
                filterToInput = false
                filterWithCountryCode = filterWithCountryCode?.apply {
                    filterWithFilteredNumberUIModel.filter = createFilterInput.inputText().replace(Constants.MASK_CHAR.toString(), String.EMPTY).replace(Constants.SPACE, String.EMPTY)
                    viewModel.checkFilterExist(this.createFilter())
                }
                Timber.e( "CreateFilterFragment setFilterTextChangeListener filter?.filterAction ${filterWithCountryCode?.filterWithFilteredNumberUIModel?.filterAction}")
                getData()
            }
        }
    }

    private fun isHintInput(filterWithCountryCode: FilterWithCountryCodeUIModel?, inputText: String): Boolean {
        return (filterWithCountryCode?.conditionTypeFullHint() == inputText && filterWithCountryCode.filterWithFilteredNumberUIModel.isTypeFull())
                || (filterWithCountryCode?.conditionTypeStartHint() == inputText && filterWithCountryCode.filterWithFilteredNumberUIModel.isTypeStart())
    }

    private fun setCountryCode(countryCode: CountryCodeUIModel?) {
        Timber.e("CreateFilterFragment setCountryCode countryCode.country ${countryCode?.country} filter?.filterAction ${binding?.filterWithCountryCode?.filterWithFilteredNumberUIModel?.filterAction}")
        binding?.apply {
            filterWithCountryCode = filterWithCountryCode?.apply {
                countryCode?.let { filterWithCountryCode?.countryCodeUIModel = countryCode }
                createFilterCountryCodeSpinner.text = countryCode?.countryEmoji()
                when {
                    filterWithFilteredNumberUIModel.isTypeFull() -> createFilterInput.setNumberMask(filterWithCountryCode?.conditionTypeFullHint().orEmpty())
                    filterWithFilteredNumberUIModel.isTypeStart() -> createFilterInput.setNumberMask(filterWithCountryCode?.conditionTypeStartHint().orEmpty())
                }
            }
            Timber.e( "CreateFilterFragment setCountryCode filter?.filterAction ${filterWithCountryCode?.filterWithFilteredNumberUIModel?.filterAction}")
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            countryCodeLiveData.safeSingleObserve(viewLifecycleOwner) { countryCode ->
                Timber.e("CreateFilterFragment observeLiveData countryCodeLiveData")
                setCountryCode(countryCode)
            }
            contactWithFilterLiveData.safeSingleObserve(viewLifecycleOwner) { numberDataList ->
                Timber.e("CreateFilterFragment observeLiveData numberDataList.size ${numberDataList.size}")
                this@CreateFilterFragment.numberDataUIModelList = ArrayList(numberDataList)
                setNumberDataUIModelList()
            }
            existingFilterLiveData.safeSingleObserve(viewLifecycleOwner) { existingFilter ->
                Timber.e("CreateFilterFragment observeLiveData existingFilterLiveData existingFilter $existingFilter filterAction ${ binding?.filterWithCountryCode?.filterWithFilteredNumberUIModel?.filterAction}")
                binding?.filterWithCountryCode = binding?.filterWithCountryCode?.apply {
                    filterWithFilteredNumberUIModel.filterAction = when (existingFilter.filterType) {
                        DEFAULT_FILTER -> if (binding?.filterWithCountryCode?.isInValidPhoneNumber(appPhoneNumberUtil).isTrue()) FilterAction.FILTER_ACTION_INVALID else if (filterWithFilteredNumberUIModel.isBlocker()) FilterAction.FILTER_ACTION_BLOCKER_CREATE else FilterAction.FILTER_ACTION_PERMISSION_CREATE
                        filterWithFilteredNumberUIModel.filterType -> if (filterWithFilteredNumberUIModel.isBlocker()) FilterAction.FILTER_ACTION_BLOCKER_DELETE else FilterAction.FILTER_ACTION_PERMISSION_DELETE
                        else -> if (filterWithFilteredNumberUIModel.isBlocker()) FilterAction.FILTER_ACTION_PERMISSION_TRANSFER else FilterAction.FILTER_ACTION_BLOCKER_TRANSFER
                    }
                }
                Timber.e("CreateFilterFragment observeLiveData existingFilterLiveData filterAction ${ binding?.filterWithCountryCode?.filterWithFilteredNumberUIModel?.filterAction}")
            }
            filterActionLiveData.safeSingleObserve(viewLifecycleOwner) { filter ->
                Timber.e("CreateFilterFragment observeLiveData filterActionLiveData")
                handleSuccessFilterAction(filter)
            }
        }
    }

    private fun setNumberDataUIModelList() {
        Timber.e("CreateFilterFragment setNumberDataUIModelList numberDataUIModelList ${numberDataUIModelList.size}")
        binding?.apply {
            createFilterAdapter?.filterWithFilteredNumberUIModel = FilterWithFilteredNumberUIModel(
                filter = binding?.filterWithCountryCode?.createFilter().toString(),
                countryCode = binding?.createFilterCountryCodeValue?.text.toString())
            createFilterAdapter?.contactWithFilterUIModels = numberDataUIModelList
            createFilterAdapter?.notifyDataSetChanged()
            createFilterNumberList.isVisible = numberDataUIModelList.isEmpty().not()
            createFilterEmptyList.isVisible = numberDataUIModelList.isEmpty()
        }
    }

    private fun handleSuccessFilterAction(filterWithFilteredNumberUIModel: FilterWithFilteredNumberUIModel) {
        Timber.e("CreateFilterFragment handleSuccessFilterAction")
        (activity as? MainActivity)?.apply {
            showInfoMessage(String.format(filterWithFilteredNumberUIModel.filterAction?.successText()?.let { getString(it) }
                .orEmpty(),
                filterWithFilteredNumberUIModel.filter, filterWithFilteredNumberUIModel.conditionTypeName()?.let { getString(it) }), false)
            showInterstitial()
            getAllData()
            findNavController().navigate(if (binding?.filterWithCountryCode?.filterWithFilteredNumberUIModel?.isBlocker().isTrue())
                CreateFilterFragmentDirections.startListBlockerFragment()
            else CreateFilterFragmentDirections.startListPermissionFragment()
            )
        }
    }

    override fun showInfoScreen() {
        Timber.e("CreateFilterFragment showInfoScreen")
        val info = when {
            binding?.filterWithCountryCode?.filterWithFilteredNumberUIModel?.isTypeStart().isTrue() -> Info.INFO_CREATE_FILTER_START
            binding?.filterWithCountryCode?.filterWithFilteredNumberUIModel?.isTypeContain().isTrue() -> Info.INFO_CREATE_FILTER_CONTAIN
            else -> Info.INFO_CREATE_FILTER_FULL
        }
        findNavController().navigate(
            DetailsNumberDataFragmentDirections.startInfoFragment(info = InfoData(
            title = getString(info.title()),
            description = getString(info.description()))
        ))
    }
}
