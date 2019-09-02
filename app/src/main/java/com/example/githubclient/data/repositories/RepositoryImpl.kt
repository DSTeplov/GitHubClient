package com.example.githubclient.data.repositories

import com.example.githubclient.data.models.FollowerModel
import com.example.githubclient.data.models.UserInfo
import com.example.githubclient.data.network.RetrofitService

class RepositoryImpl : Repository {
    override suspend fun getUserInfo(login: String): UserInfo = RetrofitService.getApiService().getUserInfo(login).await()

    override suspend fun getUserFollowers(login: String, pageNumber: Int): List<FollowerModel> =
        RetrofitService.getApiService().getUserFollowers(login, pageNumber).await()
}