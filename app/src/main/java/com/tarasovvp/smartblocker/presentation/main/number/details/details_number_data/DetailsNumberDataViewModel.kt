package com.tarasovvp.smartblocker.presentation.main.number.details.details_number_data

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.domain.usecases.DetailsNumberDataUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import com.tarasovvp.smartblocker.presentation.mappers.CallWithFilterUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.CountryCodeUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.FilterWithFilteredNumberUIMapper
import com.tarasovvp.smartblocker.presentation.ui_models.FilterWithCountryCodeUIModel
import com.tarasovvp.smartblocker.presentation.ui_models.NumberDataUIModel
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
    val countryCodeLiveData = MutableLiveData<FilterWithCountryCodeUIModel>()
    val blockHiddenLiveData = MutableLiveData<Boolean>()

    fun filterListWithNumber(number: String) {
        showProgress()
        launch {
            val filterList = detailsNumberDataUseCase.allFilterWithFilteredNumbersByNumber(number)
            filterListLiveData.postValue(filterWithFilteredNumberUIMapper.mapToUIModelList(filterList))
        }
    }

    fun filteredCallsByNumber(number: String) {
        launch {
            val filteredCallList = detailsNumberDataUseCase.allFilteredCallsByNumber(number)
            filteredCallListLiveData.postValue(callWithFilterUIMapper.mapToUIModelList(filteredCallList))
            hideProgress()
        }
    }

    fun getCountryCode(code: Int?, filterWithCountryCodeUIModel: FilterWithCountryCodeUIModel) {
        launch {
            val countryCode = code?.let { detailsNumberDataUseCase.getCountryCodeByCode(it) } ?: CountryCode()
            countryCodeLiveData.postValue(filterWithCountryCodeUIModel.apply { countryCodeUIModel = countryCodeUIMapper.mapToUIModel(countryCode)  })
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