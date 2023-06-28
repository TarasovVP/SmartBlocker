package com.tarasovvp.smartblocker.presentation.main.number.create

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentCreateFilterBinding
import com.tarasovvp.smartblocker.domain.enums.FilterAction
import com.tarasovvp.smartblocker.domain.enums.Info
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.COUNTRY_CODE
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.COUNTRY_CODE_START
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.DEFAULT_FILTER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTER_ACTION
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.MASK_CHAR
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PLUS_CHAR
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.SPACE
import com.tarasovvp.smartblocker.presentation.base.BaseDetailsFragment
import com.tarasovvp.smartblocker.presentation.main.MainActivity
import com.tarasovvp.smartblocker.presentation.main.number.details.details_number_data.DetailsNumberDataFragmentDirections
import com.tarasovvp.smartblocker.presentation.ui_models.ContactWithFilterUIModel
import com.tarasovvp.smartblocker.presentation.ui_models.CountryCodeUIModel
import com.tarasovvp.smartblocker.presentation.ui_models.FilterWithCountryCodeUIModel
import com.tarasovvp.smartblocker.presentation.ui_models.FilterWithFilteredNumberUIModel
import com.tarasovvp.smartblocker.utils.AppPhoneNumberUtil
import com.tarasovvp.smartblocker.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint
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
                        }
                        binding?.executePendingBindings()
                    }
                }
            }
        }
        binding?.createFilterNumberList?.adapter = createFilterAdapter
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e("createFilterTAG", "CreateFilterFragment onCreateView filter ${binding?.filterWithCountryCode?.filterWithFilteredNumberUIModel?.filter} getRawText ${binding?.createFilterInput?.getRawText()} getNumberMask ${binding?.createFilterInput?.getNumberMask()}")
        return super.onCreateView(inflater, container, savedInstanceState)
    }
    override fun initViews() {
        binding?.apply {
            filterToInput = true
            filterWithCountryCode = args.filterWithCountryCodeUIModel?.apply {
                filterWithFilteredNumberUIModel.filterAction = this.filterWithFilteredNumberUIModel.filterAction ?: FilterAction.FILTER_ACTION_INVALID
            }
            if (filterWithCountryCode?.filterWithFilteredNumberUIModel?.isTypeContain().isTrue()) {
                createFilterInput.setHint(R.string.creating_filter_enter_hint)
            } else {
                setCountryCode(filterWithCountryCode?.countryCodeUIModel)
            }
            (activity as? MainActivity)?.toolbar?.title = getString(if (filterWithCountryCode?.filterWithFilteredNumberUIModel?.isBlocker().isTrue()) R.string.creating_blocker else R.string.creating_permission)
            setKeyboardHidden()
            setFilterTextChangeListener()
            executePendingBindings()
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
        viewModel.getMatchedContactWithFilterList(binding?.filterWithCountryCode)
    }

    private fun setFilterTextChangeListener() {
        binding?.apply {
            createFilterInput.doAfterTextChanged {
                val inputText = if (filterWithCountryCode?.filterWithFilteredNumberUIModel?.isTypeContain().isTrue()) createFilterInput.inputText() else createFilterInput.getRawText()
                Log.e("createFilterTAG", "CreateFilterFragment doAfterTextChanged before inputText $inputText getNumberMask ${createFilterInput.getNumberMask()} createFilterInput.inputText ${createFilterInput.inputText()} filter ${binding?.filterWithCountryCode?.filterWithFilteredNumberUIModel?.filter} getRawText ${createFilterInput.getRawText()}")
                if (isNonUniqueInput(filterWithCountryCode, inputText, it.toString()) || inputText.contains(MASK_CHAR) || inputText.contains(SPACE)) {
                    return@doAfterTextChanged
                }
                    filterToInput = false
                    filterWithCountryCode = filterWithCountryCode?.apply {
                        filterWithFilteredNumberUIModel.filter = inputText
                        if ((filterWithFilteredNumberUIModel.isTypeFull() && createFilterInput.inputText().contains(MASK_CHAR)).not()) {
                            viewModel.checkFilterExist(createFilter())
                        }
                    }
                    getData()
                Log.e("createFilterTAG", "CreateFilterFragment doAfterTextChanged after inputText $inputText getNumberMask ${createFilterInput.getNumberMask()} createFilterInput.inputText ${createFilterInput.inputText()} filter ${binding?.filterWithCountryCode?.filterWithFilteredNumberUIModel?.filter} getRawText ${createFilterInput.getRawText()}")
            }
        }
    }

    private fun isNonUniqueInput(
        filterWithCountryCode: FilterWithCountryCodeUIModel?,
        inputText: String,
        editable: String
    ): Boolean {
        return filterWithCountryCode?.filterWithFilteredNumberUIModel?.filter == inputText
                && isHintInput(filterWithCountryCode, editable).not()
                && filterWithCountryCode.filterWithFilteredNumberUIModel.isTypeContain().isNotTrue()
                && binding?.filterToInput.isNotTrue()
    }
    private fun isHintInput(filterWithCountryCode: FilterWithCountryCodeUIModel?, inputText: String): Boolean {
        return (filterWithCountryCode?.conditionTypeFullHint() == inputText && filterWithCountryCode.filterWithFilteredNumberUIModel.isTypeFull())
                || (filterWithCountryCode?.conditionTypeStartHint() == inputText && filterWithCountryCode.filterWithFilteredNumberUIModel.isTypeStart())
    }

    private fun setCountryCode(countryCode: CountryCodeUIModel?) {
        binding?.apply {
            Log.e("createFilterTAG", "CreateFilterFragment setCountryCode before getNumberMask ${createFilterInput.getNumberMask()} inputText ${createFilterInput.inputText()} filter ${binding?.filterWithCountryCode?.filterWithFilteredNumberUIModel?.filter}")
            filterToInput = true
            filterWithCountryCode = filterWithCountryCode?.apply {
                countryCode?.let { filterWithCountryCode?.countryCodeUIModel = countryCode }
                createFilterCountryCodeSpinner.text = countryCode?.countryEmoji()
                when {
                    filterWithFilteredNumberUIModel.isTypeFull() -> createFilterInput.setNumberMask(filterWithCountryCode?.conditionTypeFullHint().orEmpty())
                    filterWithFilteredNumberUIModel.isTypeStart() -> createFilterInput.setNumberMask(filterWithCountryCode?.conditionTypeStartHint().orEmpty())
                }
            }
            Log.e("createFilterTAG", "CreateFilterFragment setCountryCode after getNumberMask ${createFilterInput.getNumberMask()} inputText ${createFilterInput.inputText()} filter ${binding?.filterWithCountryCode?.filterWithFilteredNumberUIModel?.filter}")
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            contactWithFilterLiveData.safeSingleObserve(viewLifecycleOwner) { numberDataList ->
                this@CreateFilterFragment.numberDataUIModelList = ArrayList(numberDataList)
                setNumberDataUIModelList()
            }
            existingFilterLiveData.safeSingleObserve(viewLifecycleOwner) { existingFilter ->
                binding?.filterWithCountryCode = binding?.filterWithCountryCode?.apply {
                    filterWithFilteredNumberUIModel.filterAction = when (existingFilter.filterType) {
                        DEFAULT_FILTER -> if (binding?.filterWithCountryCode?.isInValidPhoneNumber(appPhoneNumberUtil).isTrue()) FilterAction.FILTER_ACTION_INVALID else if (filterWithFilteredNumberUIModel.isBlocker()) FilterAction.FILTER_ACTION_BLOCKER_CREATE else FilterAction.FILTER_ACTION_PERMISSION_CREATE
                        filterWithFilteredNumberUIModel.filterType -> if (filterWithFilteredNumberUIModel.isBlocker()) FilterAction.FILTER_ACTION_BLOCKER_DELETE else FilterAction.FILTER_ACTION_PERMISSION_DELETE
                        else -> if (filterWithFilteredNumberUIModel.isBlocker()) FilterAction.FILTER_ACTION_PERMISSION_TRANSFER else FilterAction.FILTER_ACTION_BLOCKER_TRANSFER
                    }
                }
            }
            filterActionLiveData.safeSingleObserve(viewLifecycleOwner) { filter ->
                handleSuccessFilterAction(filter)
            }
        }
    }

    private fun setNumberDataUIModelList() {
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
         binding?.filterWithCountryCode?.filterWithFilteredNumberUIModel?.apply {
             val info = when {
                isTypeStart() -> if (isBlocker()) Info.INFO_CREATE_BLOCKER_START else Info.INFO_CREATE_PERMISSION_START
                isTypeContain() -> if (isBlocker()) Info.INFO_CREATE_BLOCKER_CONTAIN else Info.INFO_CREATE_PERMISSION_CONTAIN
                else -> if (isBlocker()) Info.INFO_CREATE_BLOCKER_FULL else Info.INFO_CREATE_PERMISSION_FULL
            }
            findNavController().navigate(
                DetailsNumberDataFragmentDirections.startInfoFragment(info = info))
        }
    }
}
