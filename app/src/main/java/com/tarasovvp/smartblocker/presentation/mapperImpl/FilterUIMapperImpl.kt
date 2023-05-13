package com.tarasovvp.smartblocker.presentation.mapperImpl

import com.tarasovvp.smartblocker.domain.entities.db_entities.Filter
import com.tarasovvp.smartblocker.domain.mappers.FilterUIMapper
import com.tarasovvp.smartblocker.presentation.ui_models.*
import com.tarasovvp.smartblocker.utils.extensions.orZero

class FilterUIMapperImpl : FilterUIMapper {

    override fun mapToUIModel(from: Filter): FilterUIModel {
        return FilterUIModel(from.filter, from.conditionType.orZero(), from.filterType.orZero(),
            from.filterName.orEmpty(), from.countryCode.orEmpty(), from.country.orEmpty(), from.created)
    }

    override fun mapFromUIModel(to: FilterUIModel): Filter {
        return Filter(to.filter, to.conditionType.orZero(), to.filterType.orZero(),
            to.filterName, to.countryCode, to.country, to.created)
    }

    override fun mapToUIModelList(fromList: List<Filter>): List<FilterUIModel> {
        return fromList.map { mapToUIModel(it) }
    }

    override fun mapFromUIModelList(toList: List<FilterUIModel>): List<Filter> {
        return toList.map { mapFromUIModel(it) }
    }
}