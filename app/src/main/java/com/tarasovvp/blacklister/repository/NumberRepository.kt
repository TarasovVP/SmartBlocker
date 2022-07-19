package com.tarasovvp.blacklister.repository

import com.tarasovvp.blacklister.extensions.hashMapFromList
import com.tarasovvp.blacklister.model.Number
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object NumberRepository {

    suspend fun getHashMapFromNumberList(numberList: List<Number>): HashMap<String, List<Number>> =
        withContext(
            Dispatchers.Default
        ) {
            numberList.hashMapFromList()
        }
}