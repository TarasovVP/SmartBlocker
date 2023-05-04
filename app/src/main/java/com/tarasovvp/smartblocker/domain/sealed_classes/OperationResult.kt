package com.tarasovvp.smartblocker.domain.sealed_classes

sealed class OperationResult<out T> {
    data class Success<T>(val data: T? = null) : OperationResult<T>()
    data class Failure(val errorMessage: String? = null) : OperationResult<Nothing>()
}
