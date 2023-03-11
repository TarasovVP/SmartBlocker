package com.tarasovvp.smartblocker.ui.dialogs.country_code_search_dialog

import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants.COUNTRY
import com.tarasovvp.smartblocker.databinding.DialogCountryCodeSearchBinding
import com.tarasovvp.smartblocker.enums.EmptyState
import com.tarasovvp.smartblocker.extensions.safeObserve
import com.tarasovvp.smartblocker.extensions.setSafeOnClickListener
import com.tarasovvp.smartblocker.models.CountryCode
import com.tarasovvp.smartblocker.ui.base.BaseDialog
import com.tarasovvp.smartblocker.ui.main.number.create.CreateFilterViewModel

class CountryCodeSearchDialog : BaseDialog<DialogCountryCodeSearchBinding>() {

    override var layoutId = R.layout.dialog_country_code_search
    private val viewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(activity ?: this)[CreateFilterViewModel::class.java]
    }

    private var countryCodeSearchAdapter: CountryCodeSearchAdapter? = null

    override fun initUI() {
        countryCodeSearchAdapter =
            CountryCodeSearchAdapter(arrayListOf()) { countryCode ->
                findNavController().navigateUp()
                setFragmentResult(COUNTRY, bundleOf(COUNTRY to countryCode))
            }
        binding?.countryCodeSearchList?.adapter = countryCodeSearchAdapter
        binding?.countryCodeEmpty?.emptyState = EmptyState.EMPTY_STATE_QUERY
        binding?.countryCodeSearchCancel?.setSafeOnClickListener {
            dismiss()
        }
        viewModel.countryCodeListLiveData.safeObserve(viewLifecycleOwner) { countryCodeList ->
            setCountryCodeSearchList(countryCodeList)
        }
    }

    private fun setCountryCodeSearchList(countryCodeList: List<CountryCode>) {
        countryCodeSearchAdapter?.countryCodeList = countryCodeList
        binding?.countryCodeEmpty?.root?.isVisible = countryCodeList.isEmpty()
        binding?.countryCodeSearchInput?.doAfterTextChanged { searchText ->
            countryCodeSearchAdapter?.countryCodeList =
                countryCodeList.filter {
                    it.countryCode.contains(searchText.toString().lowercase())
                            || it.country.lowercase().contains(searchText.toString().lowercase())
                }
            countryCodeSearchAdapter?.notifyDataSetChanged()
            binding?.countryCodeEmpty?.root?.isVisible =
                countryCodeSearchAdapter?.countryCodeList.orEmpty().isEmpty()
        }
    }
}