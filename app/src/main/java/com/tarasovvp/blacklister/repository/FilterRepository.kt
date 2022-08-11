package com.tarasovvp.blacklister.repository

import com.tarasovvp.blacklister.extensions.toHashMapFromList
import com.tarasovvp.blacklister.model.Filter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

open class FilterRepository {

    val realDataBaseRepository = RealDataBaseRepository

    suspend fun getHashMapFromFilterList(filterList: List<Filter>): HashMap<String, List<Filter>> =
        withContext(Dispatchers.Default) {
            filterList.toHashMapFromList()
        }

}