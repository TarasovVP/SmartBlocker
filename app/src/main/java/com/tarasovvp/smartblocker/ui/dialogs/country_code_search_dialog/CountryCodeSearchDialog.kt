package com.tarasovvp.smartblocker.ui.dialogs.country_code_search_dialog

import android.util.Log
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants.COUNTRY_CODE
import com.tarasovvp.smartblocker.databinding.DialogCountryCodeSearchBinding
import com.tarasovvp.smartblocker.extensions.safeObserve
import com.tarasovvp.smartblocker.extensions.safeSingleObserve
import com.tarasovvp.smartblocker.models.CountryCode
import com.tarasovvp.smartblocker.ui.base.BaseDialog
import com.tarasovvp.smartblocker.ui.number_data.details.filter_add.FilterAddViewModel
import com.tarasovvp.smartblocker.utils.setSafeOnClickListener

class CountryCodeSearchDialog : BaseDialog<DialogCountryCodeSearchBinding>() {

    override var layoutId = R.layout.dialog_country_code_search
    private val viewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(activity ?: this)[FilterAddViewModel::class.java]
    }

    override fun initUI() {
        binding?.countryCodeSearchCancel?.setSafeOnClickListener {
            dismiss()
        }
        viewModel.countryCodeListLiveData.safeObserve(viewLifecycleOwner) { countryCodeList ->
            setCountryCodeSearchAdapter(countryCodeList)
        }
    }

    private fun setCountryCodeSearchAdapter(countryCodeList: List<CountryCode>) {
        val countryCodeSearchAdapter =
            CountryCodeSearchAdapter(countryCodeList) { countryCode ->
                setFragmentResult(COUNTRY_CODE, bundleOf(COUNTRY_CODE to countryCode))
                dismiss()
            }
        binding?.countryCodeSearchList?.adapter = countryCodeSearchAdapter
        binding?.countryCodeSearchInput?.doAfterTextChanged { searchText ->
            countryCodeSearchAdapter.countryCodeList =
                countryCodeList.filter {
                    it.countryCode.contains(searchText.toString().lowercase())
                            || it.country.lowercase().contains(searchText.toString().lowercase())
                }
            countryCodeSearchAdapter.notifyDataSetChanged()
        }
    }
}