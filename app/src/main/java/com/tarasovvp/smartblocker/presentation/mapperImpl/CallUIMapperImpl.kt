package com.tarasovvp.smartblocker.presentation.mapperImpl

import com.tarasovvp.smartblocker.domain.entities.models.Call
import com.tarasovvp.smartblocker.presentation.mappers.CallUIMapper
import com.tarasovvp.smartblocker.presentation.ui_models.*
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import com.tarasovvp.smartblocker.utils.extensions.orZero

class CallUIMapperImpl : CallUIMapper {

    override fun mapToUIModel(from: Call): CallUIModel {
        return CallUIModel(from.callId, from.callName.orEmpty(), from.number.orEmpty(), from.type.orEmpty(), from.callDate.orEmpty(),
            from.photoUrl.orEmpty(), from.isFilteredCall.isTrue(), from.filteredNumber.orEmpty(), from.filteredConditionType.orZero())
    }

    override fun mapFromUIModel(to: CallUIModel): Call {
        return Call(to.callId, to.callName, to.number, to.type, to.callDate,
            to.photoUrl, to.isFilteredCall, to.filteredNumber, to.conditionType)
    }

    override fun mapToUIModelList(fromList: List<Call>): List<CallUIModel> {
        return fromList.map { mapToUIModel(it) }
    }

    override fun mapFromUIModelList(toList: List<CallUIModel>): List<Call> {
        return toList.map { mapFromUIModel(it) }
    }
}