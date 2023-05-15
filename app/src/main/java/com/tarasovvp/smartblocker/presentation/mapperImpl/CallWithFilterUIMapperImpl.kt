package com.tarasovvp.smartblocker.presentation.mapperImpl

import com.tarasovvp.smartblocker.domain.entities.db_views.CallWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.entities.models.Call
import com.tarasovvp.smartblocker.presentation.mappers.CallWithFilterUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.FilterUIMapper
import com.tarasovvp.smartblocker.presentation.ui_models.*
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import com.tarasovvp.smartblocker.utils.extensions.orZero

class CallWithFilterUIMapperImpl(private val filterUIMapper: FilterUIMapper) : CallWithFilterUIMapper {

    override fun mapToUIModel(from: CallWithFilter): CallWithFilterUIModel {
        return CallWithFilterUIModel(from.call?.callId.orZero(), from.call?.callName.orEmpty(), from.call?.number.orEmpty(), from.call?.type.orEmpty(), from.call?.callDate.orEmpty(),
            from.call?.photoUrl.orEmpty(), from.call?.isFilteredCall.isTrue(), from.call?.filteredNumber.orEmpty(), from.call?.filteredConditionType.orZero(),
            filterUIModel = from.filterWithCountryCode?.filter?.let { filterUIMapper.mapToUIModel(it) })
    }

    override fun mapFromUIModel(to: CallWithFilterUIModel): CallWithFilter {
        return CallWithFilter(call = Call(to.callId, to.callName, to.number, to.type, to.callDate,
            to.photoUrl, to.isFilteredCall, to.filteredNumber, to.conditionType),
            filterWithCountryCode = FilterWithCountryCode(filter = to.filterUIModel?.let { filterUIMapper.mapFromUIModel(it) }))
    }

    override fun mapToUIModelList(fromList: List<CallWithFilter>): List<CallWithFilterUIModel> {
        return fromList.map { mapToUIModel(it) }
    }

    override fun mapFromUIModelList(toList: List<CallWithFilterUIModel>): List<CallWithFilter> {
        return toList.map { mapFromUIModel(it) }
    }
}