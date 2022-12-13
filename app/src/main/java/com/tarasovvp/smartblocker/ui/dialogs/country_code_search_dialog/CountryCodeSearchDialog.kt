package com.tarasovvp.smartblocker.ui.dialogs.country_code_search_dialog

import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants.COUNTRY_CODE
import com.tarasovvp.smartblocker.databinding.DialogCountryCodeSearchBinding
import com.tarasovvp.smartblocker.extensions.safeSingleObserve
import com.tarasovvp.smartblocker.ui.base.BaseDialog
import com.tarasovvp.smartblocker.ui.number_data.filter_add.FilterAddViewModel
import com.tarasovvp.smartblocker.utils.setSafeOnClickListener

class CountryCodeSearchDialog : BaseDialog<DialogCountryCodeSearchBinding>() {

    override var layoutId = R.layout.dialog_country_code_search

    val viewModel: FilterAddViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(this)[FilterAddViewModel::class.java]
    }

    override fun initUI() {
            viewModel.getCountryCodeList()
        viewModel.getCountryCodeList()
        binding?.apply {
            val countryCodeSearchAdapter =
                CountryCodeSearchAdapter(listOf()) { countryCode ->
                    setFragmentResult(COUNTRY_CODE, bundleOf(COUNTRY_CODE to countryCode))
                    dismiss()
                }
            countryCodeSearchList.adapter = countryCodeSearchAdapter
            viewModel.countryCodeListLiveData.safeSingleObserve(viewLifecycleOwner) {
                countryCodeSearchAdapter.countryCodeList = it
            }
            countryCodeSearchInput.doAfterTextChanged { searchText ->
                countryCodeSearchAdapter.countryCodeList = countryCodeSearchAdapter.countryCodeList?.filter {
                    it.countryCode.contains(searchText.toString()) || it.country.lowercase()
                        .contains(searchText.toString())
                }
                countryCodeSearchAdapter.notifyDataSetChanged()
            }
            countryCodeSearchCancel.setSafeOnClickListener {
                dismiss()
            }
        }
    }
}