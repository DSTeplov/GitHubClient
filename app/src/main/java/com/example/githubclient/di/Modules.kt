package com.example.githubclient.di

import com.example.githubclient.data.network.RetrofitService
import com.example.githubclient.data.repositories.Repository
import com.example.githubclient.data.repositories.RepositoryImpl
import com.example.githubclient.presentation.viewmodels.MainActivityViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val mainModule: Module = module {
    // factory { RepositoryImpl(get()) as Repository }
    factory<Repository> { RepositoryImpl(get()) }
    viewModel { MainActivityViewModel(get()) }
    factory { RetrofitService.getApiService() }
}