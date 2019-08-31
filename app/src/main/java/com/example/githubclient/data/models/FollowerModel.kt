package com.example.githubclient.data.models

import com.google.gson.annotations.SerializedName

data class FollowerModel(
    @SerializedName("login") val login: String,
    @SerializedName("avatar_url") val avatar: String
)