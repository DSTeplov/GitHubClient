package com.example.githubclient.data.repositories

import com.example.githubclient.data.models.FollowerModel
import com.example.githubclient.data.models.UserInfo
import com.example.githubclient.data.network.ApiService

class RepositoryImpl(private val apiService: ApiService) : Repository {
    override suspend fun getUserInfo(login: String): UserInfo = apiService.getUserInfo(login)

    override suspend fun getUserFollowers(login: String, pageNumber: Int): List<FollowerModel> =
        apiService.getUserFollowers(login, pageNumber)
}