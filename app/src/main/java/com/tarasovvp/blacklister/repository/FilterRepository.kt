package com.tarasovvp.blacklister.repository

import com.tarasovvp.blacklister.extensions.EMPTY
import com.tarasovvp.blacklister.model.Filter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

open class FilterRepository {

    val realDataBaseRepository = RealDataBaseRepository

    suspend fun getHashMapFromFilterList(filterList: List<Filter>): Map<String, List<Filter>> =
        withContext(Dispatchers.Default) {
            filterList.groupBy { if (it.filter.isNotEmpty()) it.filter[0].toString() else String.EMPTY }
        }

}