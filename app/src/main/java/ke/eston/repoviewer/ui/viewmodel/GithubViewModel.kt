package ke.eston.repoviewer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ke.eston.repoviewer.domain.model.Repository
import ke.eston.repoviewer.domain.repository.GithubRepository
import ke.eston.repoviewer.ui.model.RepositoryListEvent
import ke.eston.repoviewer.ui.model.RepositoryListState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

private const val PER_PAGE = 20

@OptIn(FlowPreview::class)
class GithubViewModel(
    private val githubRepository: GithubRepository,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {
    private var currentPage = 1
    private var loadComplete = false
    private var repositories = listOf<Repository>()

    private val _repositoryListState = MutableStateFlow<RepositoryListState>(
        RepositoryListState.Idle
    )
    val repositoryListState = _repositoryListState.asStateFlow()

    private val _userHandle = MutableStateFlow("")
    var userHandle = _userHandle.asStateFlow()

    init {
        viewModelScope.launch {
            _userHandle
                .debounce(700)
                .collectLatest {
                    if (it.isEmpty()) {
                        _repositoryListState.emit(RepositoryListState.Idle)
                    } else {
                        loadFresh()
                    }
                }
        }
    }

    fun handleEvent(event: RepositoryListEvent) {
        when (event) {
            RepositoryListEvent.OnLoadMore -> loadMore()
            is RepositoryListEvent.OnUserHandleChange -> viewModelScope.launch {
                _userHandle.emit(event.handle.trim())
            }
            else -> Unit
        }
    }

    private fun loadFresh() {
        loadComplete = false
        currentPage = 1
        loadRepositories()
    }

    private fun loadMore() {
        if (!loadComplete) {
            currentPage += 1
            loadRepositories()
        }
    }

    private fun loadRepositories() = viewModelScope.launch(ioDispatcher) {
        if (currentPage == 1) {
            println("Emitting Loading")
            _repositoryListState.emit(RepositoryListState.Loading)
        } else {
            _repositoryListState.emit(RepositoryListState.LoadingMore(repositories))
        }
        val result = githubRepository.getRepositories(_userHandle.value, currentPage, PER_PAGE)
        if (result.isSuccess) {
            val repos = result.data!!
            if (repos.isEmpty()) {
                if (currentPage == 1) {
                    _repositoryListState.emit(RepositoryListState.Empty)
                }
                loadComplete = true
            } else {
                repositories = if (currentPage == 1) repos else repositories + repos
                println("Emitting Success")
                _repositoryListState.emit(RepositoryListState.Success(repositories))
            }
        } else {
            _repositoryListState.emit(
                RepositoryListState.Error(result.error?.message ?: "Unknown error")
            )
        }
    }
}