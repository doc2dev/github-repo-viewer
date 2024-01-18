package ke.eston.repoviewer.ui.model

sealed interface RepositoryListEvent {
    data object Idle: RepositoryListEvent
    data object OnLoadMore: RepositoryListEvent
    data class OnUserHandleChange(val handle: String): RepositoryListEvent
}