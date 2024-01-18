package ke.eston.repoviewer.ui.model

import ke.eston.repoviewer.domain.model.Repository

sealed interface RepositoryListState {
    data object Idle: RepositoryListState
    data object Loading: RepositoryListState
    data object Empty: RepositoryListState
    data class Success(val repositories: List<Repository>): RepositoryListState
    data class Error(val message: String): RepositoryListState
}