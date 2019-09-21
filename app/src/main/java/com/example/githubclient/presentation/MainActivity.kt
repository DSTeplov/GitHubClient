package com.example.githubclient.presentation

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.githubclient.R
import com.example.githubclient.data.models.FollowerModel
import com.example.githubclient.data.models.UserInfo
import com.example.githubclient.presentation.adapter.DiffCallback
import com.example.githubclient.presentation.adapter.FollowersAdapter
import com.example.githubclient.presentation.adapter.ItemDecorator
import com.example.githubclient.presentation.viewmodels.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {
    companion object {
        const val ARG_LOGIN = "login_arg"
        const val ARG_NUMBER = "number_arg"
        private const val MAX_NUMBER_PAGE = 4

        fun startNewActivity(context: Context, login: String, number: Int) {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(ARG_LOGIN, login)
            if (number == MAX_NUMBER_PAGE) {
                intent.putExtra(ARG_NUMBER, 1)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            } else {
                intent.putExtra(ARG_NUMBER, number)
            }
            context.startActivity(intent)
        }
    }

    private val viewModel: MainActivityViewModel by viewModel()
    private lateinit var adapter: FollowersAdapter
    private var loginUser: String? = ""
    private var number = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initList()
        getArgs()
        initSearchView()
        initToolbar(number != 1)
        viewModel.getLiveData().observe(this, Observer { result ->
            when (result) {
                is ResponseResult.ShowProgress -> progressBar.visibility = View.VISIBLE
                is ResponseResult.HideProgress -> progressBar.visibility = View.GONE
                is ResponseResult.ShowUserInfo -> setupUserCard(result.user)
                is ResponseResult.ShowFollowersList -> showFollowersList(result.list)
                is ResponseResult.ShowNextPage -> showNextPage(result.list)
                is ResponseResult.ShowNextPageProgress -> adapter.showProgress()
                is ResponseResult.HideNextPageProgress -> adapter.hideProgress()
                is ResponseResult.ShowEmptyFollowers -> showErrorMessage(result.message)
                is ResponseResult.ShowError -> {
                    userCard.visibility = View.GONE
                    showErrorMessage(result.error)
                }
                is ResponseResult.ShowNextPageError ->
                    Snackbar.make(rootLayout, result.error, Snackbar.LENGTH_LONG).show()
            }
        })
        if (!loginUser.isNullOrEmpty()) {
            viewModel.loadInfo(loginUser!!)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initToolbar(homeAsUpEnabled: Boolean) {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowHomeEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(homeAsUpEnabled)
    }

    private fun getArgs() {
        loginUser = intent.getStringExtra(ARG_LOGIN)
        number = intent.getIntExtra(ARG_NUMBER, 1)
    }

    private fun initList() {
        val layoutManager = LinearLayoutManager(this)
        followersList.layoutManager = layoutManager
        adapter = FollowersAdapter(::onClick)
        val itemDecorator = ItemDecorator(30)
        followersList.addItemDecoration(itemDecorator)
        followersList.adapter = adapter
        followersList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            var visibleItems: Int = 0
            var firstVisibleItemPosition: Int = 0
            var offset: Int = 0

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                visibleItems = layoutManager.childCount
                firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                offset = layoutManager.itemCount

                if (dy > 0) {
                    if (firstVisibleItemPosition + visibleItems >= offset) {
                        viewModel.loadNextPage(loginUser!!)
                    }
                }
            }
        })
    }

    private fun initSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (!p0.isNullOrEmpty()) {
                    loginUser = p0
                    viewModel.loadInfo(loginUser!!)
                }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }
        })
    }

    private fun setupUserCard(user: UserInfo) {
        userCard.visibility = View.VISIBLE
        errorMessage.visibility = View.GONE
        if (user.name.isNullOrEmpty())
            nameUser.text = getString(com.example.githubclient.R.string.user_no_name)
        else
            nameUser.text = user.name
        val requestOptions = RequestOptions().error(R.drawable.ic_launcher_background)
        Glide.with(this)
            .setDefaultRequestOptions(requestOptions)
            .load(user.avatar)
            .into(avatarUser)
    }

    private fun showFollowersList(list: List<FollowerModel>) {
        followersList.visibility = View.VISIBLE
        errorMessage.visibility = View.GONE
        val diff = DiffCallback(adapter.getItems(), list)
        val diffResult = DiffUtil.calculateDiff(diff)
        adapter.setItems(list)
        diffResult.dispatchUpdatesTo(adapter)
    }

    private fun showNextPage(list: List<FollowerModel>) {
        val sumList = ArrayList<FollowerModel?>()
        sumList.addAll(adapter.getItems())
        sumList.addAll(list)
        val diff = DiffCallback(adapter.getItems(), sumList)
        val diffResult = DiffUtil.calculateDiff(diff)
        adapter.setItems(sumList)
        diffResult.dispatchUpdatesTo(adapter)
    }

    private fun showErrorMessage(message: Int) {
        followersList.visibility = View.GONE
        errorMessage.visibility = View.VISIBLE
        errorMessage.text = getString(message)
    }

    private fun onClick(item: FollowerModel) {
        number = intent.getIntExtra(ARG_NUMBER, 1) + 1
        startNewActivity(this, item.login, number)
    }
}
