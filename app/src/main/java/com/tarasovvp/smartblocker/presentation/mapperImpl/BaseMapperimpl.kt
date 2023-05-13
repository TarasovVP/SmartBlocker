package com.tarasovvp.smartblocker.presentation.mapperImpl

import com.tarasovvp.smartblocker.domain.mappers.BaseMapper

abstract class BaseMapperImpl<From, To>: BaseMapper<From, To> {

    override fun mapToUIModelList(fromList: List<From>): List<To> {
        return fromList.map { mapToUIModel(it) }
    }

    override fun mapFromUIModelList(toList: List<To>): List<From> {
        return toList.map { mapFromUIModel(it) }
    }
}