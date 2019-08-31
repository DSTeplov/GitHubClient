package com.example.githubclient.presentation.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.example.githubclient.R
import com.example.githubclient.data.repositories.Repository
import com.example.githubclient.data.repositories.RepositoryImpl
import com.example.githubclient.presentation.ResponseResult
import kotlinx.coroutines.*
import retrofit2.HttpException
import java.net.UnknownHostException

class MainActivityViewModel : ViewModel() {
    private val liveData = MutableLiveData<ResponseResult>()
    private var viewModelJob = Job()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private var repository: Repository = RepositoryImpl()
    private var loading: Boolean = false
    private var lastPage: Boolean = false
    private var loadingPage = 1

    fun loadInfo(login: String) {
        loadingPage = 1
        lastPage = false
        liveData.value = ResponseResult.ShowProgress
        viewModelScope.launch {
            try {
                val user = withContext(Dispatchers.IO) { repository.getUserInfo(login) }
                val list = withContext(Dispatchers.IO) { repository.getUserFollowers(login, loadingPage) }
                val userResult = user.await()
                val followersResult = list.await()
                liveData.value = ResponseResult.ShowUserInfo(userResult)
                if (followersResult.isEmpty()) {
                    lastPage = true
                    liveData.value = ResponseResult.ShowEmptyFollowers(R.string.user_no_followers)
                } else {
                    liveData.value = ResponseResult.ShowFollowersList(followersResult)
                }
            } catch (e: Exception) {
                handleError(e)
            } finally {
                liveData.value = ResponseResult.HideProgress
            }
        }
    }

    fun loadNextPage(login: String) {
        if (loading || lastPage) return
        liveData.value = ResponseResult.ShowNextPageProgress
        loading = true
        loadingPage++
        viewModelScope.launch {
            try {
                val followersResult = withContext(Dispatchers.IO) {
                    repository.getUserFollowers(login, loadingPage)
                }.await()
                liveData.value = ResponseResult.HideNextPageProgress
                if (followersResult.isEmpty()) lastPage = true
                else liveData.value = ResponseResult.ShowNextPage(followersResult)
            } catch (e: Exception) {
                loadingPage--
                liveData.value = ResponseResult.HideNextPageProgress
                handleNextPageError(e)
            } finally {
                loading = false
            }
        }
    }

    private fun handleError(error: Exception) {
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

    private fun handleNextPageError(error: Exception) {
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