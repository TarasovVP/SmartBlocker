package com.tarasovvp.smartblocker.presentation.mapperImpl

import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.presentation.mappers.CountryCodeUIMapper
import com.tarasovvp.smartblocker.presentation.ui_models.*

class CountryCodeUIMapperImpl : CountryCodeUIMapper {

    override fun mapToUIModel(from: CountryCode): CountryCodeUIModel {
        return CountryCodeUIModel(from.country, from.countryCode.orEmpty(), from.numberFormat.orEmpty())
    }

    override fun mapFromUIModel(to: CountryCodeUIModel): CountryCode {
        return CountryCode(to.country, to.countryCode, to.numberFormat)
    }

    override fun mapToUIModelList(fromList: List<CountryCode>): List<CountryCodeUIModel> {
        return fromList.map { mapToUIModel(it) }
    }

    override fun mapFromUIModelList(toList: List<CountryCodeUIModel>): List<CountryCode> {
        return toList.map { mapFromUIModel(it) }
    }
}