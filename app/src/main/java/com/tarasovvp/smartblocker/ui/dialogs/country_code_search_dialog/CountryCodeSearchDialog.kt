package com.tarasovvp.smartblocker.ui.dialogs.country_code_search_dialog

import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants.COUNTRY_CODE
import com.tarasovvp.smartblocker.databinding.DialogCountryCodeSearchBinding
import com.tarasovvp.smartblocker.models.CountryCode
import com.tarasovvp.smartblocker.ui.base.BaseDialog
import com.tarasovvp.smartblocker.utils.setSafeOnClickListener

class CountryCodeSearchDialog : BaseDialog<DialogCountryCodeSearchBinding>() {

    override var layoutId = R.layout.dialog_country_code_search
    private val args: CountryCodeSearchDialogArgs by navArgs()
    private val countryCodeList: ArrayList<CountryCode> = arrayListOf()

    override fun initUI() {
        countryCodeList.addAll(args.countryCodeList.orEmpty())
        binding?.apply {
            val countryCodeSearchAdapter =
                CountryCodeSearchAdapter(countryCodeList) { countryCode ->
                    setFragmentResult(COUNTRY_CODE, bundleOf(COUNTRY_CODE to countryCode))
                    dismiss()
                }
            countryCodeSearchList.adapter = countryCodeSearchAdapter
            countryCodeSearchInput.doAfterTextChanged { searchText ->
                countryCodeSearchAdapter.countryCodeList =
                    countryCodeList.filter {
                        it.countryCode.contains(searchText.toString().lowercase())
                                || it.country.lowercase().contains(searchText.toString().lowercase())
                    }
                countryCodeSearchAdapter.notifyDataSetChanged()
            }
            countryCodeSearchCancel.setSafeOnClickListener {
                dismiss()
            }
        }
    }
}