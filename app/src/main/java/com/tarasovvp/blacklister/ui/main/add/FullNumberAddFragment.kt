package com.tarasovvp.blacklister.ui.main.add

import android.os.Bundle
import android.util.ArrayMap
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.text.isDigitsOnly
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.DELETE_FILTER
import com.tarasovvp.blacklister.databinding.FragmentFullNumberAddBinding
import com.tarasovvp.blacklister.extensions.*
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.model.BlackFilter
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.model.Info
import com.tarasovvp.blacklister.model.WhiteFilter
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.utils.DebouncingTextChangeListener
import com.tarasovvp.blacklister.utils.setSafeOnClickListener
import java.util.*

class FullNumberAddFragment(private var filter: Filter?) :
    BaseFragment<FragmentFullNumberAddBinding, AddViewModel>() {

    override var layoutId = R.layout.fragment_full_number_add
    override val viewModelClass = AddViewModel::class.java

    private var countryCodeMap: ArrayMap<String, Int>? = null
    private var phoneUtil = PhoneNumberUtil.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.fullNumberAddInput?.setText(filter?.filter.orEmpty())
        binding?.fullNumberAddIcon?.setImageResource(if (filter?.isBlackFilter.isTrue()) R.drawable.ic_black_filter else R.drawable.ic_white_filter)
        setCountrySpinner()
        initViewsWithData(filter, false)
        setExistNumberChecking()
        setClickListeners()
        setFragmentResultListener(DELETE_FILTER) { _, _ ->
            filter?.let {
                viewModel.deleteFilter(it)
            }
        }
    }

    private fun setCountrySpinner() {
        countryCodeMap = ArrayMap<String, Int>()
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
        countryAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.fullNumberCountryCode?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    spinner: AdapterView<*>?,
                    tv: View?,
                    position: Int,
                    id: Long,
                ) {
                    binding?.fullNumberAddInput?.setText(String.format("+%s",
                        countryCodeMap?.valueAt(position)))
                }

                override fun onNothingSelected(p0: AdapterView<*>?) = Unit

            }
        binding?.fullNumberCountryCode?.adapter = countryAdapter
        binding?.fullNumberCountryCode?.setSelection(countryCodeMap?.indexOfKey(Locale(Locale.getDefault().language,
            context?.getUserCountry().orEmpty()).flagEmoji() + context?.getUserCountry()
            ?.uppercase()).orZero())
    }

    private fun setExistNumberChecking() {
        viewModel.checkFilterExist(filter?.filter.orEmpty(),
            filter?.isBlackFilter.isTrue())
        binding?.fullNumberAddInput?.addTextChangedListener(DebouncingTextChangeListener(lifecycle) {
            val phoneNumber: Phonenumber.PhoneNumber? = try {
                phoneUtil.parse(it.orEmpty(),
                    countryCodeMap?.keyAt(binding?.fullNumberCountryCode?.selectedItemPosition.orZero()))
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
            viewModel.checkFilterExist(it.toString(), filter?.isBlackFilter.isTrue())
        })
    }

    private fun initViewsWithData(filter: Filter?, fromDb: Boolean) {
        binding?.apply {
            fullNumberAddTitle.text =
                if (fullNumberAddInput.text.isEmpty()) getString(R.string.add_filter_message) else String.format(
                    if (fromDb && filter.isNotNull()) getString(R.string.edit_filter_with_filter_message) else getString(
                        R.string.add_filter_with_filter_message),
                    fullNumberAddInput.text)
            fullNumberSubmit.isVisible =
                fullNumberAddInput.text.isNotEmpty()
            fullNumberSubmit.text =
                if (fromDb && filter.isNotNull()) getString(R.string.edit) else getString(R.string.add)
            if (fullNumberSubmit.text.isNotEmpty()) {
                viewModel.checkContactListByFilter(getFilter())
            }
        }
    }

    private fun setClickListeners() {
        binding?.apply {
            fullNumberAddConditionsInfo.setSafeOnClickListener {
                fullNumberAddConditionsInfo.showPopUpWindow(Info(title = getString(R.string.add_conditions_title),
                    description = getString(R.string.add_conditions_info),
                    icon = R.drawable.ic_test))
            }
            //TODO implement button click
            fullNumberSubmit.setSafeOnClickListener {
                filter?.let {
                    findNavController().navigate(AddFragmentDirections.startDeleteFilterDialog(
                        filter = it))
                }
            }
            fullNumberSubmit.setSafeOnClickListener {
                viewModel.insertFilter(getFilter())
            }
        }
    }


    private fun getFilter(): Filter {
        val filter = if (filter?.isBlackFilter.isTrue()) {
            BlackFilter(filter = binding?.fullNumberAddInput?.text.toString())
        } else {
            WhiteFilter(filter = binding?.fullNumberAddInput?.text.toString())
        }
        return filter
    }

    override fun observeLiveData() {
        with(viewModel) {
            existFilterLiveData.observe(viewLifecycleOwner) { filter ->
                initViewsWithData(filter, true)
            }
            queryContactListLiveData.safeSingleObserve(viewLifecycleOwner) { contactList ->
                var filterAddInfoText = ""
                contactList.filterNot {
                    if (filter?.isBlackFilter.isTrue()) it.isWhiteFilter && SharedPreferencesUtil.isWhiteListPriority else it.isBlackFilter && SharedPreferencesUtil.isWhiteListPriority.not()
                }.apply {
                    if (this.isNotEmpty()) filterAddInfoText += String.format(getString(R.string.block_add_info),
                        if (filter?.isBlackFilter.isTrue()) getString(R.string.can_block) else getString(
                            R.string.can_unblock),
                        this.size)
                }
                contactList.filter {
                    if (filter?.isBlackFilter.isTrue()) it.isWhiteFilter && SharedPreferencesUtil.isWhiteListPriority else it.isBlackFilter && SharedPreferencesUtil.isWhiteListPriority.not()
                }.apply {
                    if (filterAddInfoText.isNotEmpty()) filterAddInfoText += "\n"
                    if (this.isNotEmpty()) filterAddInfoText += String.format(getString(R.string.not_block_add_info),
                        if (filter?.isBlackFilter.isTrue()) getString(R.string.can_block) else getString(
                            R.string.can_unblock),
                        this.size)
                }
            }
            insertFilterLiveData.safeSingleObserve(viewLifecycleOwner) { number ->
                handleSuccessFilterAction(String.format(getString(R.string.filter_added), number))
            }
            deleteFilterLiveData.safeSingleObserve(viewLifecycleOwner) {
                handleSuccessFilterAction(String.format(getString(R.string.delete_filter_from_list),
                    filter?.filter.orEmpty()))
            }
        }
    }

    private fun handleSuccessFilterAction(message: String) {
        (activity as MainActivity).apply {
            showMessage(message, false)
            getAllData()
        }
        findNavController().popBackStack()
    }

}