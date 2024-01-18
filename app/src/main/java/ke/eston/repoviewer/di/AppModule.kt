package ke.eston.repoviewer.di

import ke.eston.repoviewer.data.remote.api.GithubApi
import ke.eston.repoviewer.data.remote.api.getRetrofit
import ke.eston.repoviewer.data.remote.datasource.GithubRemoteDataSource
import ke.eston.repoviewer.data.remote.datasource.GithubRemoteDataSourceImpl
import ke.eston.repoviewer.data.repository.GithubRepositoryImpl
import ke.eston.repoviewer.domain.repository.GithubRepository
import ke.eston.repoviewer.ui.viewmodel.GithubViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { Dispatchers.IO }
    single { getRetrofit() }
    single { getRetrofit().create(GithubApi::class.java) }
    factory<GithubRemoteDataSource> { GithubRemoteDataSourceImpl(get()) }
    factory<GithubRepository> { GithubRepositoryImpl(get()) }
    viewModel { GithubViewModel(get(), get()) }
}