package com.tarasovvp.smartblocker.presentation.main.number.list.listfilter

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.tarasovvp.smartblocker.domain.usecases.ListFilterUseCase
import com.tarasovvp.smartblocker.presentation.mappers.CountryCodeUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.FilterWithFilteredNumberUIMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListPermissionFilterViewModel
    @Inject
    constructor(
        application: Application,
        listFilterUseCase: ListFilterUseCase,
        filterWithFilteredNumberUIMapper: FilterWithFilteredNumberUIMapper,
        countryCodeUIMapper: CountryCodeUIMapper,
        val savedStateHandle: SavedStateHandle,
    ) : BaseListFilterViewModel(
            application,
            listFilterUseCase,
            filterWithFilteredNumberUIMapper,
            countryCodeUIMapper,
        )
