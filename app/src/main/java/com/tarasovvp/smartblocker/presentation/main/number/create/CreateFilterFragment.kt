package com.tarasovvp.smartblocker.presentation.main.number.create

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.models.database_views.ContactWithFilter
import com.tarasovvp.smartblocker.domain.models.database_views.LogCallWithFilter
import com.tarasovvp.smartblocker.domain.models.entities.CountryCode
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.COUNTRY_CODE
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.COUNTRY_CODE_START
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.DEFAULT_FILTER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTER_ACTION
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PLUS_CHAR
import com.tarasovvp.smartblocker.databinding.FragmentCreateFilterBinding
import com.tarasovvp.smartblocker.domain.enums.FilterAction
import com.tarasovvp.smartblocker.domain.enums.Info
import com.tarasovvp.smartblocker.domain.models.InfoData
import com.tarasovvp.smartblocker.presentation.ui_models.NumberDataUIModel
import com.tarasovvp.smartblocker.presentation.main.MainActivity
import com.tarasovvp.smartblocker.presentation.base.BaseDetailsFragment
import com.tarasovvp.smartblocker.presentation.main.number.details.NumberDataAdapter
import com.tarasovvp.smartblocker.presentation.main.number.details.details_number_data.DetailsNumberDataFragmentDirections
import com.tarasovvp.smartblocker.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
open class CreateFilterFragment :
    BaseDetailsFragment<FragmentCreateFilterBinding, CreateFilterViewModel>() {

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
                is ContactWithFilter -> numberData.contact?.number
                is LogCallWithFilter -> numberData.callUIModel?.number
                else -> String.EMPTY
            }
            binding?.apply {
                binding?.filterToInput = true
                if (filterWithCountryCode?.filter?.isTypeContain().isTrue()) {
                    filterWithCountryCode?.filter = filterWithCountryCode?.filter?.apply {
                        this.filter = number.digitsTrimmed().replace(PLUS_CHAR.toString(), String.EMPTY)
                    }
                } else {
                    val phoneNumber = number.getPhoneNumber(filterWithCountryCode?.countryCode?.country.orEmpty())
                    if ((phoneNumber?.nationalNumber.toString() == createFilterInput.getRawText() && String.format(COUNTRY_CODE_START, phoneNumber?.countryCode.toString()) == createFilterCountryCodeValue.text.toString()).not()) {
                        filterWithCountryCode?.filter = filterWithCountryCode?.filter?.apply {
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
                filter?.filterAction = filter?.filterAction ?: FilterAction.FILTER_ACTION_INVALID
            }
            Timber.e( "CreateFilterFragment initViews filter?.filterAction ${filterWithCountryCode?.filter?.filterAction}")
            setCountryCode(filterWithCountryCode?.countryCode)
            (activity as MainActivity).toolbar?.title = getString(if (filterWithCountryCode?.filter?.isBlocker().isTrue()) R.string.creating_blocker else R.string.creating_permission)
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
                    filter?.filter = filterWithCountryCode?.createFilter().orEmpty()
                    filter?.filterAction = filter?.filterAction ?: if (filter?.isBlocker().isTrue()) FilterAction.FILTER_ACTION_BLOCKER_CREATE else FilterAction.FILTER_ACTION_PERMISSION_CREATE }))
            }
            createFilterCountryCodeSpinner.setSafeOnClickListener {
                findNavController().navigate(CreateFilterFragmentDirections.startCountryCodeSearchDialog())
            }
        }
    }

    override fun getData() {
        Timber.e( "CreateFilterFragment getData")
        viewModel.getNumberDataList()
    }

    override fun setFragmentResultListeners() {
        binding?.filterWithCountryCode?.let { filter ->
            setFragmentResultListener(COUNTRY_CODE) { _, bundle ->
                bundle.parcelable<CountryCode>(COUNTRY_CODE)?.let { setCountryCode(it) }
                filterNumberDataList()
            }
            setFragmentResultListener(FILTER_ACTION) { _, bundle ->
                when (val filterAction = bundle.serializable<FilterAction>(FILTER_ACTION)) {
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
            createFilterInput.doAfterTextChanged {
                Timber.e( "CreateFilterFragment setFilterTextChangeListener doAfterTextChanged it $it")
                if ((filterWithCountryCode?.conditionTypeFullHint() == it.toString() && filterWithCountryCode?.filter?.isTypeFull().isTrue())
                    || (filterWithCountryCode?.conditionTypeStartHint() == it.toString() && filterWithCountryCode?.filter?.isTypeStart().isTrue())) return@doAfterTextChanged
                Timber.e("CreateFilterFragment setFilterTextChangeListener true condition")
                filterToInput = false
                filterWithCountryCode = filterWithCountryCode?.apply {
                    filter?.filter = createFilterInput.inputText().replace(Constants.MASK_CHAR.toString(), String.EMPTY).replace(Constants.SPACE, String.EMPTY)
                    viewModel.checkFilterExist(when {
                        filter?.isTypeContain().isTrue() -> filter?.filter.orEmpty()
                        else -> String.format("%s%s", countryCode?.countryCode, filterToInput())
                    })
                }
                Timber.e( "CreateFilterFragment setFilterTextChangeListener filter?.filterAction ${filterWithCountryCode?.filter?.filterAction}")
                filterNumberDataList()
            }
        }
    }

    private fun filterNumberDataList() {
        context?.let { context -> viewModel.filterNumberDataList(binding?.filterWithCountryCode, numberDataUIModelList, ContextCompat.getColor(context, R.color.text_color_black)) }
    }

    private fun setCountryCode(countryCode: CountryCode?) {
        Timber.e("CreateFilterFragment setCountryCode countryCode.country ${countryCode?.country} filter?.filterAction ${binding?.filterWithCountryCode?.filter?.filterAction}")
        binding?.apply {
            filterWithCountryCode?.filter = filterWithCountryCode?.filter?.apply {
                if (isTypeContain()) {
                    createFilterInput.setHint(R.string.creating_filter_enter_hint)
                } else {
                    filterWithCountryCode?.countryCode = countryCode
                    createFilterCountryCodeSpinner.text = countryCode?.countryEmoji()
                    when {
                        isTypeFull().isTrue() -> createFilterInput.setNumberMask(filterWithCountryCode?.conditionTypeFullHint().orEmpty())
                        isTypeStart().isTrue() -> createFilterInput.setNumberMask(filterWithCountryCode?.conditionTypeStartHint().orEmpty())
                    }
                }
            }
            Timber.e( "CreateFilterFragment setCountryCode filter?.filterAction ${filterWithCountryCode?.filter?.filterAction}")
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
                if (binding?.filterWithCountryCode?.filter?.isTypeContain().isTrue().not()) {
                    context?.let {viewModel.filterNumberDataList(binding?.filterWithCountryCode, this@CreateFilterFragment.numberDataUIModelList,
                        ContextCompat.getColor(it, R.color.text_color_black)) }
                } else {
                    numberDataAdapter?.numberDataUIModelList = this@CreateFilterFragment.numberDataUIModelList
                    numberDataAdapter?.notifyDataSetChanged()
                }
                binding?.filterToInput = true
                binding?.filterWithCountryCode?.filter = binding?.filterWithCountryCode?.filter
                (activity as MainActivity).setMainProgressVisibility(false)
            }
            existingFilterLiveData.safeSingleObserve(viewLifecycleOwner) { existingFilter ->
                Timber.e("CreateFilterFragment observeLiveData existingFilterLiveData existingFilter $existingFilter filterAction ${ binding?.filterWithCountryCode?.filter?.filterAction}")
                binding?.filterWithCountryCode = binding?.filterWithCountryCode?.apply {
                    filter?.filterAction = when (existingFilter.filter?.filterType) {
                        DEFAULT_FILTER -> if (binding?.filterWithCountryCode?.isInValidPhoneNumber().isTrue()) FilterAction.FILTER_ACTION_INVALID else if (filter?.isBlocker().isTrue()) FilterAction.FILTER_ACTION_BLOCKER_CREATE else FilterAction.FILTER_ACTION_PERMISSION_CREATE
                        filter?.filterType -> if (filter?.isBlocker().isTrue()) FilterAction.FILTER_ACTION_BLOCKER_DELETE else FilterAction.FILTER_ACTION_PERMISSION_DELETE
                        else -> if (filter?.isBlocker().isTrue()) FilterAction.FILTER_ACTION_PERMISSION_TRANSFER else FilterAction.FILTER_ACTION_BLOCKER_TRANSFER
                    }
                }
                Timber.e("CreateFilterFragment observeLiveData existingFilterLiveData filterAction ${ binding?.filterWithCountryCode?.filter?.filterAction}")
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

    private fun handleSuccessFilterAction(filter: Filter) {
        Timber.e("CreateFilterFragment handleSuccessFilterAction")
        (activity as MainActivity).apply {
            showInfoMessage(String.format(filter.filterAction?.successText()?.let { getString(it) }
                .orEmpty(),
                filter.filter, getString(filter.conditionTypeName())), false)
            showInterstitial()
            getAllData()
            findNavController().navigate(if (binding?.filterWithCountryCode?.filter?.isBlocker().isTrue())
                CreateFilterFragmentDirections.startListBlockerFragment()
            else CreateFilterFragmentDirections.startListPermissionFragment()
            )
        }
    }

    override fun showInfoScreen() {
        Timber.e("CreateFilterFragment showInfoScreen")
        val info = when {
            binding?.filterWithCountryCode?.filter?.isTypeStart().isTrue() -> Info.INFO_CREATE_FILTER_START
            binding?.filterWithCountryCode?.filter?.isTypeContain().isTrue() -> Info.INFO_CREATE_FILTER_CONTAIN
            else -> Info.INFO_CREATE_FILTER_FULL
        }
        findNavController().navigate(
            DetailsNumberDataFragmentDirections.startInfoFragment(info = InfoData(
            title = getString(info.title()),
            description = getString(info.description()))
        ))
    }
}
