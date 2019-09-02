package com.example.githubclient.presentation.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.githubclient.R
import com.example.githubclient.data.models.FollowerModel
import kotlinx.android.synthetic.main.item_user_card.view.*


class FollowersAdapter(private val onClick: (item: FollowerModel) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_PROGRESS = 1
    }

    private val items = ArrayList<FollowerModel?>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                val view = inflater.inflate(R.layout.item_user_card, parent, false)
                FollowerViewHolder(view)
            }
            else -> {
                val view = inflater.inflate(R.layout.item_progress, parent, false)
                ProgressViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FollowerViewHolder -> holder.bind(items[position]!!)
            is ProgressViewHolder -> {
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position] != null) VIEW_TYPE_ITEM
        else VIEW_TYPE_PROGRESS
    }

    override fun getItemCount(): Int = items.size

    fun update(list: List<FollowerModel>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    fun setItems(list: List<FollowerModel?>) {
        items.clear()
        items.addAll(list)
    }

    fun showProgress() {
        items.add(null)
        notifyItemInserted(items.size - 1)
    }

    fun hideProgress() {
        items.removeAt(items.size - 1)
        notifyItemRemoved(items.size)
    }

    fun getItems(): List<FollowerModel?> = items

    inner class FollowerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION)
                    onClick(items[position]!!)
            }
        }

        fun bind(follower: FollowerModel) {
            itemView.name.text = follower.login
            val requestOptions = RequestOptions().error(R.drawable.ic_launcher_background)
            Glide.with(itemView.context)
                .setDefaultRequestOptions(requestOptions)
                .load(follower.avatar)
                .into(itemView.avatar)
        }
    }

    inner class ProgressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}