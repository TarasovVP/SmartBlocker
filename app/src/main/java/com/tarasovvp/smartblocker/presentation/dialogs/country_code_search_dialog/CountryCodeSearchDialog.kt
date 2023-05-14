package com.tarasovvp.smartblocker.presentation.dialogs.country_code_search_dialog

import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.COUNTRY_CODE
import com.tarasovvp.smartblocker.databinding.DialogCountryCodeSearchBinding
import com.tarasovvp.smartblocker.domain.enums.EmptyState
import com.tarasovvp.smartblocker.utils.extensions.safeSingleObserve
import com.tarasovvp.smartblocker.utils.extensions.setSafeOnClickListener
import com.tarasovvp.smartblocker.presentation.base.BaseDialog
import com.tarasovvp.smartblocker.presentation.ui_models.CountryCodeUIModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class CountryCodeSearchDialog : BaseDialog<DialogCountryCodeSearchBinding>() {

    override var layoutId = R.layout.dialog_country_code_search

    private val viewModel by lazy {
        ViewModelProvider(this)[CountryCodeSearchViewModel::class.java]
    }
    private var countryCodeSearchAdapter: CountryCodeSearchAdapter? = null

    override fun initUI() {
        countryCodeSearchAdapter =
            CountryCodeSearchAdapter(arrayListOf()) { countryCode ->
                setFragmentResult(COUNTRY_CODE, bundleOf(COUNTRY_CODE to countryCode))
                findNavController().navigateUp()
            }
        binding?.apply {
            countryCodeSearchList.adapter = countryCodeSearchAdapter
            countryCodeEmpty.setDescription(EmptyState.EMPTY_STATE_QUERY.description())
            countryCodeSearchCancel.setSafeOnClickListener {
                dismiss()
            }
        }
        getAppLanguage()
    }

    private fun getAppLanguage() {
        viewModel.getAppLanguage()
        viewModel.appLangLiveDataLiveData.safeSingleObserve(viewLifecycleOwner) { appLang ->
            getCountryCodeSearchList(appLang)
        }
    }

    private fun getCountryCodeSearchList(appLang: String) {
        viewModel.getCountryCodeList()
        viewModel.countryCodeListLiveData.safeSingleObserve(viewLifecycleOwner) { countryCodeList ->
            setCountryCodeSearchList(countryCodeList, appLang)
        }
    }

    private fun setCountryCodeSearchList(countryCodeList: List<CountryCodeUIModel>, appLang: String) {
        binding?.apply {
            countryCodeSearchAdapter?.countryCodeList = countryCodeList
            countryCodeEmpty.isVisible = countryCodeList.isEmpty()
            countryCodeSearchInput.doAfterTextChanged { searchText ->
                countryCodeSearchAdapter?.countryCodeList =
                    countryCodeList.filter {
                        it.countryCode.contains(searchText.toString().lowercase()) || Locale(appLang, it.country).displayCountry.lowercase().contains(searchText.toString().lowercase())
                    }
                countryCodeSearchAdapter?.notifyDataSetChanged()
                countryCodeEmpty.isVisible =
                    countryCodeSearchAdapter?.countryCodeList.orEmpty().isEmpty()
            }
            countryCodeSearchAdapter?.notifyDataSetChanged()
        }
    }
}