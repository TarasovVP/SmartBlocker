package com.tarasovvp.smartblocker.presentation.main.number.details.details_number_data

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.usecases.DetailsNumberDataUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import com.tarasovvp.smartblocker.presentation.mappers.CallWithFilterUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.CountryCodeUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.FilterWithFilteredNumberUIMapper
import com.tarasovvp.smartblocker.presentation.ui_models.CountryCodeUIModel
import com.tarasovvp.smartblocker.presentation.ui_models.NumberDataUIModel
import com.tarasovvp.smartblocker.utils.extensions.isNotNull
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailsNumberDataViewModel @Inject constructor(
    application: Application,
    private val detailsNumberDataUseCase: DetailsNumberDataUseCase,
    private val filterWithFilteredNumberUIMapper: FilterWithFilteredNumberUIMapper,
    private val callWithFilterUIMapper: CallWithFilterUIMapper,
    private val countryCodeUIMapper: CountryCodeUIMapper
) : BaseViewModel(application) {

    val filterListLiveData = MutableLiveData<List<NumberDataUIModel>>()
    val filteredCallListLiveData = MutableLiveData<List<NumberDataUIModel>>()
    val currentCountryCodeLiveData = MutableLiveData<CountryCodeUIModel>()
    val countryCodeLiveData = MutableLiveData<CountryCodeUIModel>()
    val blockHiddenLiveData = MutableLiveData<Boolean>()

    fun filterListWithNumber(number: String) {
        showProgress()
        launch {
            val filterList = detailsNumberDataUseCase.allFilterWithFilteredNumbersByNumber(number)
            filterListLiveData.postValue(filterWithFilteredNumberUIMapper.mapToUIModelList(filterList))
        }
    }

    fun filteredCallsByNumber(number: String, name: String) {
        launch {
            val filteredCallList = detailsNumberDataUseCase.allFilteredCallsByNumber(number, name)
            filteredCallListLiveData.postValue(callWithFilterUIMapper.mapToUIModelList(filteredCallList))
            hideProgress()
        }
    }

    fun getCurrentCountryCode() {
        launch {
            detailsNumberDataUseCase.getCurrentCountryCode().collect { countryCode ->
                currentCountryCodeLiveData.postValue(countryCode.takeIf { it.isNotNull() }?.let { countryCodeUIMapper.mapToUIModel(it) } ?: CountryCodeUIModel())
            }
        }
    }

    fun getCountryCode(code: Int?) {
        launch {
            val countryCode = code?.let { detailsNumberDataUseCase.getCountryCodeByCode(it) } ?: CountryCode()
            countryCodeLiveData.postValue(countryCodeUIMapper.mapToUIModel(countryCode))
        }
    }

    fun getBlockHidden() {
        launch {
            detailsNumberDataUseCase.getBlockHidden().collect { blockHidden ->
                blockHiddenLiveData.postValue(blockHidden.isTrue())
            }
        }
    }
}