package com.tarasovvp.smartblocker.presentation.dialogs.country_code_search_dialog

import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.models.entities.CountryCode
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.COUNTRY_CODE
import com.tarasovvp.smartblocker.databinding.DialogCountryCodeSearchBinding
import com.tarasovvp.smartblocker.domain.enums.EmptyState
import com.tarasovvp.smartblocker.utils.extensions.safeSingleObserve
import com.tarasovvp.smartblocker.utils.extensions.setSafeOnClickListener
import com.tarasovvp.smartblocker.presentation.base.BaseDialog
import dagger.hilt.android.AndroidEntryPoint

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
        binding?.countryCodeSearchList?.adapter = countryCodeSearchAdapter
        binding?.countryCodeEmpty?.setDescription(EmptyState.EMPTY_STATE_QUERY.description)
        binding?.countryCodeSearchCancel?.setSafeOnClickListener {
            dismiss()
        }
        viewModel.getCountryCodeList()
        viewModel.countryCodeListLiveData.safeSingleObserve(viewLifecycleOwner) { countryCodeList ->
            setCountryCodeSearchList(countryCodeList)
        }
    }

    private fun setCountryCodeSearchList(countryCodeList: List<CountryCode>) {
        countryCodeSearchAdapter?.countryCodeList = countryCodeList
        binding?.countryCodeEmpty?.isVisible = countryCodeList.isEmpty()
        binding?.countryCodeSearchInput?.doAfterTextChanged { searchText ->
            countryCodeSearchAdapter?.countryCodeList =
                countryCodeList.filter {
                    it.countryCode.contains(searchText.toString().lowercase())
                            || it.country.lowercase().contains(searchText.toString().lowercase())
                }
            countryCodeSearchAdapter?.notifyDataSetChanged()
            binding?.countryCodeEmpty?.isVisible =
                countryCodeSearchAdapter?.countryCodeList.orEmpty().isEmpty()
        }
        countryCodeSearchAdapter?.notifyDataSetChanged()
    }
}