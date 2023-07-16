package com.tarasovvp.smartblocker.presentation.main.number.list.list_call

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.enums.NumberDataFiltering
import com.tarasovvp.smartblocker.domain.sealed_classes.Result
import com.tarasovvp.smartblocker.domain.usecases.ListCallUseCase
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.SECOND
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import com.tarasovvp.smartblocker.presentation.mappers.CallWithFilterUIMapper
import com.tarasovvp.smartblocker.presentation.ui_models.CallWithFilterUIModel
import com.tarasovvp.smartblocker.utils.extensions.isContaining
import com.tarasovvp.smartblocker.utils.extensions.isNetworkAvailable
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class ListCallViewModel @Inject constructor(
    private val application: Application,
    private val listCallUseCase: ListCallUseCase,
    private val callWithFilterUIMapper: CallWithFilterUIMapper,
    val savedStateHandle: SavedStateHandle
) : BaseViewModel(application) {

    val callListLiveData = MutableLiveData<List<CallWithFilterUIModel>>()
    val filteredCallListLiveData = MutableLiveData<List<CallWithFilterUIModel>>()
    val successDeleteNumberLiveData = MutableLiveData<Boolean>()
    val isReviewVoteLiveData = MutableLiveData<Boolean>()

    fun getCallList(refreshing: Boolean) {
        if (refreshing.not()) showProgress()
        launch {
            val callList = listCallUseCase.allCallWithFilters()
            callListLiveData.postValue(callWithFilterUIMapper.mapToUIModelList(callList))
            hideProgress()
        }
    }

    fun getFilteredCallList(callList: List<CallWithFilterUIModel>, searchQuery: String, filterIndexes: ArrayList<Int>) {
        launch {
            val filteredCallList = if (searchQuery.isBlank() && filterIndexes.isEmpty()) callList else callList.filter { callWithFilter ->
                (callWithFilter.callName isContaining searchQuery || callWithFilter.number isContaining searchQuery)
                        && ((callWithFilter.isFilteredCall.isTrue() && callWithFilter.type == Constants.BLOCKED_CALL && filterIndexes.contains(
                    NumberDataFiltering.CALL_BLOCKED.ordinal).isTrue())
                        || (callWithFilter.isFilteredCall.isTrue() && callWithFilter.type != Constants.BLOCKED_CALL && filterIndexes.contains(
                    NumberDataFiltering.CALL_PERMITTED.ordinal).isTrue())
                        || filterIndexes.isEmpty())
            }
            filteredCallListLiveData.postValue(filteredCallList)
            hideProgress()
        }
    }

    fun deleteCallList(filteredCallIdList: List<Int>) {
        showProgress()
        launch {
            listCallUseCase.deleteCallList(filteredCallIdList, application.isNetworkAvailable()) { operationResult ->
                when(operationResult) {
                    is Result.Success -> successDeleteNumberLiveData.postValue(true)
                    is Result.Failure -> exceptionLiveData.postValue(application.getString(R.string.app_network_unavailable_repeat))
                }
                hideProgress()
            }
        }
    }

    fun getReviewVoted() {
        launch {
            listCallUseCase.getReviewVoted().collect { reviewVote ->
                delay(SECOND)
                isReviewVoteLiveData.postValue(reviewVote)
            }
        }
    }

    fun setReviewVoted() {
        launch {
            listCallUseCase.setReviewVoted { operationResult ->
                if (operationResult is Result.Failure) operationResult.errorMessage?.let { exceptionLiveData.postValue(it) }
            }
        }
    }
}
