package com.tarasovvp.smartblocker.domain.usecases

import com.tarasovvp.smartblocker.presentation.ui_models.CountryCodeUIModel

interface CountryCodeSearchUseCase  {

    suspend fun getCountryCodeList(): List<CountryCodeUIModel>
}