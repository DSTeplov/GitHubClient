package com.example.githubclient.data.repositories

import com.example.githubclient.data.models.FollowerModel
import com.example.githubclient.data.models.UserInfo
import kotlinx.coroutines.Deferred

interface Repository {
    fun getUserInfo(login: String): Deferred<UserInfo>
    fun getUserFollowers(login: String, pageNumber: Int): Deferred<List<FollowerModel>>
}