package ke.eston.repoviewer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ke.eston.repoviewer.domain.model.Repository
import ke.eston.repoviewer.domain.repository.GithubRepository
import ke.eston.repoviewer.ui.model.RepositoryListEvent
import ke.eston.repoviewer.ui.model.RepositoryListState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val PER_PAGE = 10

class GithubViewModel(
    private val githubRepository: GithubRepository,
    private val ioDispatcher: CoroutineDispatcher
): ViewModel() {
    private var currentPage = 1
    private var loadComplete = false
    private var userHandle = ""
    private var repositories = listOf<Repository>()

    private val _repositoryListState = MutableStateFlow<RepositoryListState>(
        RepositoryListState.Idle
    )
    val repositoryListState = _repositoryListState.asStateFlow()

    fun handleEvent(event: RepositoryListEvent) {
        when (event) {
            RepositoryListEvent.OnLoadMore -> loadMore()
            is RepositoryListEvent.OnUserHandleChange -> loadFresh(event)
            else -> Unit
        }
    }

    private fun loadFresh(event: RepositoryListEvent.OnUserHandleChange) {
        loadComplete = false
        currentPage = 1
        userHandle = event.handle
    }

    private fun loadMore() {
        if (!loadComplete) {
            currentPage += 1
            loadRepositories()
        }
    }

    private fun loadRepositories() = viewModelScope.launch(ioDispatcher) {
        if (currentPage == 1) {
            _repositoryListState.emit(RepositoryListState.Loading)
        }
        val result = githubRepository.getRepositories(userHandle, currentPage, PER_PAGE)
        if (result.isSuccess) {
            val repos = result.data!!
            if (repos.isEmpty()) {
                if (currentPage == 1) {
                    _repositoryListState.emit(RepositoryListState.Empty)
                }
                loadComplete = true
            } else {
                repositories = if (currentPage == 1) repos else repositories + repos
                _repositoryListState.emit(RepositoryListState.Success(repositories))
            }
        } else {
            _repositoryListState.emit(
                RepositoryListState.Error(result.error?.message ?: "Unknown error")
            )
        }
    }
}