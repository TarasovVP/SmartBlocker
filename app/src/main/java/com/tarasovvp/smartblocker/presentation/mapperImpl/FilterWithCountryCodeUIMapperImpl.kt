package com.tarasovvp.smartblocker.presentation.mapperImpl

import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.presentation.mappers.CountryCodeUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.FilterUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.FilterWithCountryCodeUIMapper
import com.tarasovvp.smartblocker.presentation.ui_models.*

class FilterWithCountryCodeUIMapperImpl(private val filterUIMapper: FilterUIMapper, private val countryCodeUIMapper: CountryCodeUIMapper) : FilterWithCountryCodeUIMapper {

    override fun mapToUIModel(from: FilterWithCountryCode): FilterWithCountryCodeUIModel {
        return FilterWithCountryCodeUIModel(filterUIModel = from.filter?.let { filterUIMapper.mapToUIModel(it) },
            countryCodeUIModel = from.countryCode?.let { countryCodeUIMapper.mapToUIModel(it) })
    }

    override fun mapFromUIModel(to: FilterWithCountryCodeUIModel): FilterWithCountryCode {
        return FilterWithCountryCode(filter = to.filterUIModel?.let { filterUIMapper.mapFromUIModel(it) },
            countryCode = to.countryCodeUIModel?.let { countryCodeUIMapper.mapFromUIModel(it) })
    }

    override fun mapToUIModelList(fromList: List<FilterWithCountryCode>): List<FilterWithCountryCodeUIModel> {
        return fromList.map { mapToUIModel(it) }
    }

    override fun mapFromUIModelList(toList: List<FilterWithCountryCodeUIModel>): List<FilterWithCountryCode> {
        return toList.map { mapFromUIModel(it) }
    }
}