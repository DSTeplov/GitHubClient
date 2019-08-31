package com.example.githubclient.presentation

import com.example.githubclient.data.models.FollowerModel
import com.example.githubclient.data.models.UserInfo

sealed class ResponseResult {
    object ShowProgress : ResponseResult()
    object HideProgress : ResponseResult()
    object ShowNextPageProgress : ResponseResult()
    object HideNextPageProgress : ResponseResult()
    data class ShowFollowersList(val list: List<FollowerModel>) : ResponseResult()
    data class ShowNextPage(val list: List<FollowerModel>) : ResponseResult()
    data class ShowUserInfo(val user: UserInfo) : ResponseResult()
    data class ShowError(val error: Int) : ResponseResult()
    data class ShowNextPageError(val error: Int) : ResponseResult()
    data class ShowEmptyFollowers(val message: Int) : ResponseResult()
}