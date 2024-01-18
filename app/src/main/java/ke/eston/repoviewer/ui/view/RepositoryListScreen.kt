package ke.eston.repoviewer.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ke.eston.repoviewer.R
import ke.eston.repoviewer.domain.model.Repository
import ke.eston.repoviewer.ui.model.RepositoryListEvent
import ke.eston.repoviewer.ui.model.RepositoryListState
import ke.eston.repoviewer.ui.theme.RepoViewerTheme
import ke.eston.repoviewer.ui.viewmodel.GithubViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun RepositoryListScreen() {
    val viewModel: GithubViewModel = koinViewModel()
    val searchQuery by viewModel.userHandle.collectAsStateWithLifecycle()
    val state by viewModel.repositoryListState.collectAsStateWithLifecycle()

    Content(
        userHandle = searchQuery,
        state = state,
        onQueryChange = {
            viewModel.handleEvent(RepositoryListEvent.OnUserHandleChange(it))
        },
        onLoadMore = {
            viewModel.handleEvent(RepositoryListEvent.OnLoadMore)
        }
    )
}

@Composable
private fun Content(
    userHandle: String = "",
    state: RepositoryListState = RepositoryListState.Idle,
    onQueryChange: (String) -> Unit = {},
    onLoadMore: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.label_repo_viewer),
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = userHandle,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = if (userHandle.isNotEmpty()) {
                {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            Icons.Rounded.Clear,
                            contentDescription = stringResource(R.string.content_clear_search),
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            } else null,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.None,
                imeAction = ImeAction.Done
            )
        )
        when (state) {
            RepositoryListState.Empty -> CommonScreen(
                title = stringResource(R.string.lable_no_repos, userHandle)
            )

            is RepositoryListState.Error -> CommonScreen(
                title = stringResource(R.string.label_error, userHandle)
            )

            RepositoryListState.Idle -> CommonScreen(
                title = stringResource(R.string.label_prompt)
            )

            RepositoryListState.Loading -> CommonScreen(
                title = stringResource(R.string.label_loading, userHandle),
                showLoading = true
            )

            is RepositoryListState.Success,
            is RepositoryListState.LoadingMore-> RepositoryList(
                userHandle,
                state,
                onLoadMore
            )
        }
    }
}

@Composable
fun RepositoryList(
    userHandle: String,
    state: RepositoryListState,
    onLoadMore: () -> Unit
) {
    val repositories: List<Repository> = when (state) {
        is RepositoryListState.Success -> state.repositories
        is RepositoryListState.LoadingMore -> state.repositories
        else -> emptyList()
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Spacer(Modifier.height(40.dp))
        if (userHandle.isNotEmpty()) {
            Text(
                stringResource(R.string.label_repos, userHandle),
                style = MaterialTheme.typography.titleMedium
            )
        }
        Spacer(Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier.weight(1.0f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(repositories) { index, repository ->
                RepositoryListItem(repository)
                if (index == repositories.lastIndex) {
                    SideEffect {
                        onLoadMore()
                    }
                }
            }
            if (state is RepositoryListState.LoadingMore) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
private fun CommonScreen(
    title: String,
    showLoading: Boolean = false,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(40.dp))
        if (showLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.height(20.dp))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
@FullScreenPreviews
fun RepositoryListScreenPreview() {
    RepoViewerTheme {
        Content()
    }
}