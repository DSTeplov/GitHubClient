package com.example.githubclient.data.repositories

import com.example.githubclient.data.models.FollowerModel
import com.example.githubclient.data.models.UserInfo
import com.example.githubclient.data.network.RetrofitService
import kotlinx.coroutines.Deferred

class RepositoryImpl : Repository {
    override fun getUserInfo(login: String): Deferred<UserInfo> = RetrofitService.getApiService().getUserInfo(login)

    override fun getUserFollowers(login: String, pageNumber: Int): Deferred<List<FollowerModel>> =
        RetrofitService.getApiService().getUserFollowers(login, pageNumber)
}