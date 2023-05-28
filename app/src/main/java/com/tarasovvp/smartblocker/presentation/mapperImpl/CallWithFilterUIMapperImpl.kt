package com.tarasovvp.smartblocker.presentation.mapperImpl

import com.tarasovvp.smartblocker.domain.entities.db_views.CallWithFilter
import com.tarasovvp.smartblocker.domain.entities.db_views.FilterWithFilteredNumber
import com.tarasovvp.smartblocker.domain.entities.models.Call
import com.tarasovvp.smartblocker.presentation.mappers.CallWithFilterUIMapper
import com.tarasovvp.smartblocker.presentation.mappers.FilterWithFilteredNumberUIMapper
import com.tarasovvp.smartblocker.presentation.ui_models.*
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import com.tarasovvp.smartblocker.utils.extensions.orZero

class CallWithFilterUIMapperImpl(private val filterWithFilteredNumberUIMapper: FilterWithFilteredNumberUIMapper) : CallWithFilterUIMapper {

    override fun mapToUIModel(from: CallWithFilter): CallWithFilterUIModel {
        return CallWithFilterUIModel(from.call?.callId.orZero(), from.call?.callName.orEmpty(), from.call?.number.orEmpty(), from.call?.type.orEmpty(), from.call?.callDate.orEmpty(),
            from.call?.photoUrl.orEmpty(), from.call?.isFilteredCall.isTrue(), from.call?.filteredNumber.orEmpty(), from.call?.filteredConditionType.orZero(),
            filterWithFilteredNumberUIModel = from.filterWithFilteredNumber?.let { filterWithFilteredNumberUIMapper.mapToUIModel(it) } ?: FilterWithFilteredNumberUIModel())
    }

    override fun mapFromUIModel(to: CallWithFilterUIModel): CallWithFilter {
        return CallWithFilter(call = Call(to.callId, to.callName, to.number, to.type, to.callDate,
            to.photoUrl, to.isFilteredCall, to.filteredNumber, to.conditionType),
            filterWithFilteredNumber = FilterWithFilteredNumber(filter = filterWithFilteredNumberUIMapper.mapFromUIModel(to.filterWithFilteredNumberUIModel).filter,
                filteredContacts = to.filterWithFilteredNumberUIModel.filteredContacts, filteredCalls = to.filterWithFilteredNumberUIModel.filteredCalls))
    }

    override fun mapToUIModelList(fromList: List<CallWithFilter>): List<CallWithFilterUIModel> {
        return fromList.map { mapToUIModel(it) }
    }

    override fun mapFromUIModelList(toList: List<CallWithFilterUIModel>): List<CallWithFilter> {
        return toList.map { mapFromUIModel(it) }
    }
}