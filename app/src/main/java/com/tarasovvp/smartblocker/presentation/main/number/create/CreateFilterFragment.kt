package com.tarasovvp.smartblocker.presentation.main.number.create

import android.content.Context
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.COUNTRY_CODE
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.COUNTRY_CODE_START
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.DEFAULT_FILTER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTER_ACTION
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PLUS_CHAR
import com.tarasovvp.smartblocker.databinding.FragmentCreateFilterBinding
import com.tarasovvp.smartblocker.domain.enums.FilterAction
import com.tarasovvp.smartblocker.domain.enums.Info
import com.tarasovvp.smartblocker.presentation.main.MainActivity
import com.tarasovvp.smartblocker.presentation.base.BaseDetailsFragment
import com.tarasovvp.smartblocker.presentation.main.number.details.NumberDataAdapter
import com.tarasovvp.smartblocker.presentation.main.number.details.details_number_data.DetailsNumberDataFragmentDirections
import com.tarasovvp.smartblocker.presentation.ui_models.*
import com.tarasovvp.smartblocker.utils.PhoneNumberUtil
import com.tarasovvp.smartblocker.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
open class CreateFilterFragment :
    BaseDetailsFragment<FragmentCreateFilterBinding, CreateFilterViewModel>() {

    @Inject
    lateinit var phoneNumberUtil: PhoneNumberUtil

    override var layoutId = R.layout.fragment_create_filter
    override val viewModelClass = CreateFilterViewModel::class.java

    private val args: CreateFilterFragmentArgs by navArgs()

    private var numberDataAdapter: NumberDataAdapter? = null
    private var numberDataUIModelList: ArrayList<NumberDataUIModel> = ArrayList()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (activity as MainActivity).setMainProgressVisibility(true)
    }

    override fun createAdapter() {
        numberDataAdapter = numberDataAdapter ?: NumberDataAdapter(numberDataUIModelList) { numberData ->
            val number = when (numberData) {
                is ContactWithFilterUIModel -> numberData.number
                is CallWithFilterUIModel -> numberData.number
                else -> String.EMPTY
            }
            binding?.apply {
                binding?.filterToInput = true
                if (filterWithCountryCode?.isTypeContain().isTrue()) {
                    filterWithCountryCode?.filterUIModel = filterWithCountryCode?.filterUIModel?.apply {
                        this.filter = number.digitsTrimmed().replace(PLUS_CHAR.toString(), String.EMPTY)
                    }
                } else {
                    val phoneNumber = phoneNumberUtil.getPhoneNumber(number, filterWithCountryCode?.countryCodeUIModel?.country.orEmpty())
                    if ((phoneNumber?.nationalNumber.toString() == createFilterInput.getRawText() && String.format(COUNTRY_CODE_START, phoneNumber?.countryCode.toString()) == createFilterCountryCodeValue.text.toString()).not()) {
                        filterWithCountryCode?.filterUIModel = filterWithCountryCode?.filterUIModel?.apply {
                            this.filter = phoneNumber?.nationalNumber?.toString() ?: number.digitsTrimmed()
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
        Timber.e("CreateFilterFragment initViews")
        binding?.apply {
            filterWithCountryCode = args.filterWithCountryCode?.apply {
                filterAction = filterAction ?: FilterAction.FILTER_ACTION_INVALID
            }
            Timber.e( "CreateFilterFragment initViews filter?.filterAction ${filterWithCountryCode?.filterAction}")
            setCountryCode(filterWithCountryCode?.countryCodeUIModel)
            (activity as MainActivity).toolbar?.title = getString(if (filterWithCountryCode?.isBlocker().isTrue()) R.string.creating_blocker else R.string.creating_permission)
            container.hideKeyboardWithLayoutTouch()
            createFilterNumberList.hideKeyboardWithLayoutTouch()
            createFilterInput.setupClearButtonWithAction()
            setFilterTextChangeListener()
            executePendingBindings()
        }
    }

    override fun setClickListeners() {
        binding?.apply {
            createFilterSubmit.setSafeOnClickListener {
                findNavController().navigate(CreateFilterFragmentDirections.startFilterActionDialog(filterWithCountryCode = filterWithCountryCode?.apply {
                    filterUIModel?.filter = filterWithCountryCode?.createFilter().orEmpty()
                    filterAction = filterAction ?: if (isBlocker().isTrue()) FilterAction.FILTER_ACTION_BLOCKER_CREATE else FilterAction.FILTER_ACTION_PERMISSION_CREATE }))
            }
            createFilterCountryCodeValue.setSafeOnClickListener {
                findNavController().navigate(CreateFilterFragmentDirections.startCountryCodeSearchDialog())
            }
            createFilterCountryCodeSpinner.setSafeOnClickListener {
                findNavController().navigate(CreateFilterFragmentDirections.startCountryCodeSearchDialog())
            }
        }
    }

    override fun getData() {
        Timber.e( "CreateFilterFragment getData")
        viewModel.getNumberDataList(binding?.filterWithCountryCode?.createFilter().orEmpty())
    }

    override fun setFragmentResultListeners() {
        binding?.filterWithCountryCode?.let { filter ->
            setFragmentResultListener(COUNTRY_CODE) { _, bundle ->
                bundle.parcelable<CountryCodeUIModel>(COUNTRY_CODE)?.let { setCountryCode(it) }
                filterNumberDataList()
            }
            setFragmentResultListener(FILTER_ACTION) { _, bundle ->
                when (val filterAction = bundle.serializable<FilterAction>(FILTER_ACTION)) {
                    FilterAction.FILTER_ACTION_BLOCKER_TRANSFER,
                    FilterAction.FILTER_ACTION_PERMISSION_TRANSFER,
                    -> viewModel.updateFilter(filter.apply {
                        this.filterAction = filterAction
                    })
                    FilterAction.FILTER_ACTION_BLOCKER_DELETE,
                    FilterAction.FILTER_ACTION_PERMISSION_DELETE,
                    -> viewModel.deleteFilter(filter.apply {
                        this.filterUIModel?.filter = filter.createFilter()
                        this.filterAction = filterAction
                    })
                    FilterAction.FILTER_ACTION_BLOCKER_CREATE,
                    FilterAction.FILTER_ACTION_PERMISSION_CREATE,
                    -> viewModel.createFilter(filter.apply {
                        this.filterUIModel?.filter = filter.createFilter()
                        this.filterAction = filterAction
                        this.filterUIModel?.created = Date().time
                        this.filterUIModel?.country = filter.countryCodeUIModel?.country.orEmpty()
                        this.filterUIModel?.countryCode = filter.countryCodeUIModel?.countryCode.orEmpty()
                    })
                    else -> Unit
                }
            }
        }
    }

    private fun setFilterTextChangeListener() {
        binding?.apply {
            createFilterInput.doAfterTextChanged {
                Timber.e( "CreateFilterFragment setFilterTextChangeListener doAfterTextChanged it $it")
                if ((filterWithCountryCode?.conditionTypeFullHint() == it.toString() && filterWithCountryCode?.isTypeFull().isTrue())
                    || (filterWithCountryCode?.conditionTypeStartHint() == it.toString() && filterWithCountryCode?.isTypeStart().isTrue())) return@doAfterTextChanged
                Timber.e("CreateFilterFragment setFilterTextChangeListener true condition")
                filterToInput = false
                filterWithCountryCode = filterWithCountryCode?.apply {
                    filterUIModel?.filter = createFilterInput.inputText().replace(Constants.MASK_CHAR.toString(), String.EMPTY).replace(Constants.SPACE, String.EMPTY)
                    viewModel.checkFilterExist(this.createFilter())
                }
                Timber.e( "CreateFilterFragment setFilterTextChangeListener filter?.filterAction ${filterWithCountryCode?.filterAction}")
                filterNumberDataList()
            }
        }
    }

    private fun filterNumberDataList() {
       viewModel.getNumberDataList(binding?.filterWithCountryCode?.createFilter().orEmpty())
    }

    private fun setCountryCode(countryCode: CountryCodeUIModel?) {
        Timber.e("CreateFilterFragment setCountryCode countryCode.country ${countryCode?.country} filter?.filterAction ${binding?.filterWithCountryCode?.filterAction}")
        binding?.apply {
            filterWithCountryCode = filterWithCountryCode?.apply {
                if (isTypeContain()) {
                    createFilterInput.setHint(R.string.creating_filter_enter_hint)
                } else {
                    filterWithCountryCode?.countryCodeUIModel = countryCode
                    //TODO
                    //createFilterCountryCodeSpinner.text = countryCode?.countryEmoji()
                    when {
                        isTypeFull().isTrue() -> createFilterInput.setNumberMask(filterWithCountryCode?.conditionTypeFullHint().orEmpty())
                        isTypeStart().isTrue() -> createFilterInput.setNumberMask(filterWithCountryCode?.conditionTypeStartHint().orEmpty())
                    }
                }
            }
            Timber.e( "CreateFilterFragment setCountryCode filter?.filterAction ${filterWithCountryCode?.filterAction}")
        }
    }

    private fun filterNumberDataList(filteredList: ArrayList<NumberDataUIModel>) {
        Timber.e("CreateFilterFragment filterNumberDataList filteredList ${filteredList.size}")
        binding?.apply {
            numberDataAdapter?.numberDataUIModelList = filteredList
            numberDataAdapter?.notifyDataSetChanged()
            createFilterNumberList.isVisible = filteredList.isEmpty().not()
            createFilterEmptyList.isVisible = filteredList.isEmpty()
            viewModel.hideProgress()
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            countryCodeLiveData.safeSingleObserve(viewLifecycleOwner) { countryCode ->
                Timber.e("CreateFilterFragment observeLiveData countryCodeLiveData")
                setCountryCode(countryCode)
                filterNumberDataList()
            }
            numberDataListLiveDataUIModel.safeSingleObserve(viewLifecycleOwner) { numberDataList ->
                Timber.e("CreateFilterFragment observeLiveData numberDataListLiveData")
                this@CreateFilterFragment.numberDataUIModelList = ArrayList(numberDataList)
                if (binding?.filterWithCountryCode?.isTypeContain().isTrue().not()) {
                    viewModel.getNumberDataList(binding?.filterWithCountryCode?.createFilter().orEmpty())
                } else {
                    numberDataAdapter?.numberDataUIModelList = this@CreateFilterFragment.numberDataUIModelList
                    numberDataAdapter?.notifyDataSetChanged()
                }
                binding?.filterToInput = true
                binding?.filterWithCountryCode?.filterUIModel = binding?.filterWithCountryCode?.filterUIModel
                (activity as MainActivity).setMainProgressVisibility(false)
            }
            existingFilterLiveData.safeSingleObserve(viewLifecycleOwner) { existingFilter ->
                Timber.e("CreateFilterFragment observeLiveData existingFilterLiveData existingFilter $existingFilter filterAction ${ binding?.filterWithCountryCode?.filterAction}")
                binding?.filterWithCountryCode = binding?.filterWithCountryCode?.apply {
                    filterAction = when (existingFilter.filterUIModel?.filterType) {
                        DEFAULT_FILTER -> if (binding?.filterWithCountryCode?.isInValidPhoneNumber(phoneNumberUtil).isTrue()) FilterAction.FILTER_ACTION_INVALID else if (isBlocker()) FilterAction.FILTER_ACTION_BLOCKER_CREATE else FilterAction.FILTER_ACTION_PERMISSION_CREATE
                        filterUIModel?.filterType -> if (isBlocker()) FilterAction.FILTER_ACTION_BLOCKER_DELETE else FilterAction.FILTER_ACTION_PERMISSION_DELETE
                        else -> if (isBlocker()) FilterAction.FILTER_ACTION_PERMISSION_TRANSFER else FilterAction.FILTER_ACTION_BLOCKER_TRANSFER
                    }
                }
                Timber.e("CreateFilterFragment observeLiveData existingFilterLiveData filterAction ${ binding?.filterWithCountryCode?.filterAction}")
            }
            filteredNumberDataListLiveDataUIModel.safeSingleObserve(viewLifecycleOwner) { filteredNumberDataList ->
                Timber.e( "CreateFilterFragment observeLiveData filteredNumberDataListLiveData")
                filterNumberDataList(filteredNumberDataList)
            }
            filterActionLiveData.safeSingleObserve(viewLifecycleOwner) { filter ->
                Timber.e("CreateFilterFragment observeLiveData filterActionLiveData")
                handleSuccessFilterAction(filter)
            }
        }
    }

    private fun handleSuccessFilterAction(filter: FilterWithCountryCodeUIModel) {
        Timber.e("CreateFilterFragment handleSuccessFilterAction")
        (activity as MainActivity).apply {
            showInfoMessage(String.format(filter.filterAction?.successText()?.let { getString(it) }
                .orEmpty(),
                filter.filterUIModel, getString(filter.conditionTypeName())), false)
            showInterstitial()
            getAllData()
            findNavController().navigate(if (binding?.filterWithCountryCode?.isBlocker().isTrue())
                CreateFilterFragmentDirections.startListBlockerFragment()
            else CreateFilterFragmentDirections.startListPermissionFragment()
            )
        }
    }

    override fun showInfoScreen() {
        Timber.e("CreateFilterFragment showInfoScreen")
        val info = when {
            binding?.filterWithCountryCode?.isTypeStart().isTrue() -> Info.INFO_CREATE_FILTER_START
            binding?.filterWithCountryCode?.isTypeContain().isTrue() -> Info.INFO_CREATE_FILTER_CONTAIN
            else -> Info.INFO_CREATE_FILTER_FULL
        }
        findNavController().navigate(
            DetailsNumberDataFragmentDirections.startInfoFragment(info = InfoData(
            title = getString(info.title()),
            description = getString(info.description()))
        ))
    }
}
