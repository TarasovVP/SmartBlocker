package com.tarasovvp.smartblocker.ui.dialogs.searchable_dialog

import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants.COUNTRY_CODE
import com.tarasovvp.smartblocker.databinding.DialogCountryCodeSearchBinding
import com.tarasovvp.smartblocker.ui.base.BaseDialog
import com.tarasovvp.smartblocker.utils.setSafeOnClickListener

class CountryCodeSearchDialog : BaseDialog<DialogCountryCodeSearchBinding>() {

    override var layoutId = R.layout.dialog_country_code_search

    private val args: CountryCodeSearchDialogArgs by navArgs()

    override fun initUI() {
        binding?.apply {
            val countryCodeSearchAdapter =
                CountryCodeSearchAdapter(args.countryCodeList?.toList()) { countryCode ->
                    setFragmentResult(COUNTRY_CODE, bundleOf(COUNTRY_CODE to countryCode))
                    dismiss()
                }
            countryCodeSearchList.adapter = countryCodeSearchAdapter
            countryCodeSearchInput.doAfterTextChanged { searchText ->
                countryCodeSearchAdapter.countryCodeList = args.countryCodeList?.filter {
                    it.countryCode.contains(searchText.toString()) || it.country.lowercase().contains(searchText.toString())
                }
                countryCodeSearchAdapter.notifyDataSetChanged()
            }
            countryCodeSearchCancel.setSafeOnClickListener {
                dismiss()
            }
        }
    }
}