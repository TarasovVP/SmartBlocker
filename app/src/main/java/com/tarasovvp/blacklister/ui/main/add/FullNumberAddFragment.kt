package com.tarasovvp.blacklister.ui.main.add

import android.os.Bundle
import android.util.ArrayMap
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.text.isDigitsOnly
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.FragmentFullNumberAddBinding
import com.tarasovvp.blacklister.extensions.*
import com.tarasovvp.blacklister.model.Filter
import java.util.*

class FullNumberAddFragment(private var filter: Filter?) :
    BaseAddFragment<FragmentFullNumberAddBinding>(filter) {

    override var layoutId = R.layout.fragment_full_number_add
    override val viewModelClass = AddViewModel::class.java

    private var countryCodeMap: ArrayMap<String, Int?>? = null
    private var phoneUtil = PhoneNumberUtil.getInstance()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCountrySpinner()
    }

    override fun initViews() {
        binding?.apply {
            title = fullNumberAddTitle
            icon = fullNumberAddIcon
            filterInput = fullNumberAddInput
            submitButton = fullNumberSubmit
            contactByFilterList = fullNumberContactByFilterList
        }
    }

    private fun setCountrySpinner() {
        countryCodeMap = ArrayMap<String, Int?>()
        countryCodeMap?.put(getString(R.string.no_country_code), null)
        Locale.getAvailableLocales().forEach { locale ->
            if (locale.country.isNotEmpty() && locale.country.isDigitsOnly()
                    .not()
            ) countryCodeMap?.put(locale.flagEmoji() + locale.country,
                phoneUtil.getCountryCodeForRegion(locale.country))
        }

        val countryAdapter = context?.let {
            ArrayAdapter(it,
                android.R.layout.simple_spinner_item,
                countryCodeMap?.keys.orEmpty().toTypedArray())
        }
        binding?.fullNumberCountryCodeSpinner?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    spinner: AdapterView<*>?,
                    tv: View?,
                    position: Int,
                    id: Long,
                ) {
                    binding?.fullNumberCountryCodeValue?.text =
                        if (countryCodeMap?.valueAt(position).isNotNull()) String.format("+%s",
                            countryCodeMap?.valueAt(position)) else String.EMPTY
                }

                override fun onNothingSelected(p0: AdapterView<*>?) = Unit

            }
        binding?.fullNumberCountryCodeSpinner?.adapter = countryAdapter
        binding?.fullNumberCountryCodeSpinner?.setSelection(countryCodeMap?.indexOfKey(Locale(Locale.getDefault().language,
            context?.getUserCountry().orEmpty()).flagEmoji() + context?.getUserCountry()
            ?.uppercase()).orZero())
        binding?.fullNumberCountryCodeValue?.text =
            if (filter?.filter.orEmpty()
                    .isValidPhoneNumber(context?.getUserCountry().orEmpty())
            ) filter?.nationalNumber(context?.getUserCountry()
                .orEmpty()) else filter?.filter.orEmpty()
    }
}