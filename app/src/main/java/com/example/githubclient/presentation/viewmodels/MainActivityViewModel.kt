package com.example.githubclient.presentation.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.example.githubclient.R
import com.example.githubclient.data.repositories.Repository
import com.example.githubclient.presentation.ResponseResult
import kotlinx.coroutines.*
import retrofit2.HttpException
import java.net.UnknownHostException

class MainActivityViewModel(private val repository: Repository) : ViewModel() {
    private val liveData = MutableLiveData<ResponseResult>()
    private var viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private var loading: Boolean = false
    private var lastPage: Boolean = false
    private var loadingPage = 1

    fun loadInfo(login: String) {
        loadingPage = 1
        lastPage = false
        liveData.value = ResponseResult.ShowProgress

        val exceptionHandler = CoroutineExceptionHandler { _, throwable -> loadInfoError(throwable) }
        viewModelScope.launch(exceptionHandler) {
            val userResult = withContext(Dispatchers.IO) { repository.getUserInfo(login) }
            val followersResult =
                withContext(Dispatchers.IO) { repository.getUserFollowers(login, loadingPage) }
            liveData.value = ResponseResult.ShowUserInfo(userResult)
            if (followersResult.isEmpty()) {
                lastPage = true
                liveData.value = ResponseResult.ShowEmptyFollowers(R.string.user_no_followers)
            } else {
                liveData.value = ResponseResult.ShowFollowersList(followersResult)
            }
            liveData.value = ResponseResult.HideProgress
        }
    }

    fun loadNextPage(login: String) {
        if (loading || lastPage) return
        liveData.value = ResponseResult.ShowNextPageProgress
        loading = true
        loadingPage++

        val exceptionHandler = CoroutineExceptionHandler { _, throwable -> nextPageError(throwable) }
        viewModelScope.launch(exceptionHandler) {
            val followersResult = withContext(Dispatchers.IO) {
                repository.getUserFollowers(login, loadingPage)
            }
            liveData.value = ResponseResult.HideNextPageProgress
            if (followersResult.isEmpty()) lastPage = true
            else liveData.value = ResponseResult.ShowNextPage(followersResult)
            loading = false
        }
    }

    private fun loadInfoError(throwable: Throwable) {
        handleError(throwable)
        liveData.value = ResponseResult.HideProgress
    }

    private fun nextPageError(throwable: Throwable) {
        loadingPage--
        liveData.value = ResponseResult.HideNextPageProgress
        handleNextPageError(throwable)
        loading = false
    }

    private fun handleError(error: Throwable) {
        when (error) {
            is UnknownHostException -> {
                liveData.value = ResponseResult.ShowError(R.string.error_network)
            }
            is HttpException -> {
                if (error.code() == 404) liveData.value =
                    ResponseResult.ShowError(R.string.error_existing_user)
                else liveData.value = ResponseResult.ShowError(R.string.error_connection)
            }
            else -> {
                liveData.value = ResponseResult.ShowError(R.string.error_connection)
            }
        }
    }

    private fun handleNextPageError(error: Throwable) {
        when (error) {
            is UnknownHostException -> {
                liveData.value =
                    ResponseResult.ShowNextPageError(R.string.error_network)
            }
            else -> {
                liveData.value =
                    ResponseResult.ShowNextPageError(R.string.error_connection)
            }
        }
    }

    fun getLiveData(): LiveData<ResponseResult> {
        return liveData
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.coroutineContext.cancelChildren()
    }

}