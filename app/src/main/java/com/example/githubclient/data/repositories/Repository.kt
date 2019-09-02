package com.example.githubclient.data.repositories

import com.example.githubclient.data.models.FollowerModel
import com.example.githubclient.data.models.UserInfo

interface Repository {
    suspend fun getUserInfo(login: String): UserInfo
    suspend fun getUserFollowers(login: String, pageNumber: Int): List<FollowerModel>
}