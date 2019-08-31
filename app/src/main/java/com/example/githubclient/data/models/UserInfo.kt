package com.example.githubclient.data.models

import com.google.gson.annotations.SerializedName

data class UserInfo(
    @SerializedName("login") val login: String,
    @SerializedName("name") val name: String?,
    @SerializedName("avatar_url") val avatar: String?
)