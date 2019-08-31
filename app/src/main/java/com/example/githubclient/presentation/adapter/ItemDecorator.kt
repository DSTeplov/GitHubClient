package com.example.githubclient.presentation.adapter

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

class ItemDecorator(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.top = spaceHeight
    }
}