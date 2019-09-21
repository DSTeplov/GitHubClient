package com.example.githubclient.data.network

import com.example.githubclient.data.models.FollowerModel
import com.example.githubclient.data.models.UserInfo
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("users/{user}")
    suspend fun getUserInfo(@Path("user") user: String): UserInfo

    @GET("users/{user}/followers")
    suspend fun getUserFollowers(@Path("user") user: String, @Query("page") page: Int?): List<FollowerModel>
}