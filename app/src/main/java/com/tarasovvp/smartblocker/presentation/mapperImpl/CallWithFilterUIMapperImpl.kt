package com.tarasovvp.smartblocker.presentation.mapperImpl

import com.tarasovvp.smartblocker.domain.entities.db_views.CallWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.presentation.mappers.CallUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.CallWithFilterUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.FilterUIMapper
import com.tarasovvp.smartblocker.presentation.ui_models.*

class CallWithFilterUIMapperImpl(private val callUIMapper: CallUIMapper, private val filterUIMapper: FilterUIMapper) : CallWithFilterUIMapper {

    override fun mapToUIModel(from: CallWithFilter): CallWithFilterUIModel {
        return CallWithFilterUIModel(callUIModel = from.call?.let { callUIMapper.mapToUIModel(it) },
            filterUIModel = from.filterWithCountryCode?.filter?.let { filterUIMapper.mapToUIModel(it) })
    }

    override fun mapFromUIModel(to: CallWithFilterUIModel): CallWithFilter {
        return CallWithFilter(call = to.callUIModel?.let { callUIMapper.mapFromUIModel(it) },
            filterWithCountryCode = FilterWithCountryCode(filter = to.filterUIModel?.let { filterUIMapper.mapFromUIModel(it) }))
    }

    override fun mapToUIModelList(fromList: List<CallWithFilter>): List<CallWithFilterUIModel> {
        return fromList.map { mapToUIModel(it) }
    }

    override fun mapFromUIModelList(toList: List<CallWithFilterUIModel>): List<CallWithFilter> {
        return toList.map { mapFromUIModel(it) }
    }
}