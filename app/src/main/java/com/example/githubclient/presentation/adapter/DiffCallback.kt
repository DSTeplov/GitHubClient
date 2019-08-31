package com.example.githubclient.presentation.adapter

import android.support.v7.util.DiffUtil
import com.example.githubclient.data.models.FollowerModel
import java.util.*

class DiffCallback(private val oldList: List<FollowerModel?>, private val newList: List<FollowerModel?>) :
        DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            Objects.equals(oldList[oldItemPosition]?.login, newList[newItemPosition]?.login)

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            Objects.equals(oldList[oldItemPosition]?.avatar, newList[newItemPosition]?.avatar)

}