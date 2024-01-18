package ke.eston.repoviewer.ui.viewmodel

import app.cash.turbine.testIn
import io.mockk.coEvery
import io.mockk.mockk
import ke.eston.repoviewer.domain.model.Repository
import ke.eston.repoviewer.domain.repository.GithubRepository
import ke.eston.repoviewer.domain.result.BaseError
import ke.eston.repoviewer.domain.result.BaseResult
import ke.eston.repoviewer.ui.model.RepositoryListEvent
import ke.eston.repoviewer.ui.model.RepositoryListState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GithubViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val githubRepository: GithubRepository = mockk()
    private lateinit var githubViewModel: GithubViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        githubViewModel = GithubViewModel(githubRepository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit correct user handle after change`() = runTest {
        coEvery {
            githubRepository.getRepositories(any(), any(), any())
        } coAnswers  {
            delay(700)
            BaseResult(data = emptyList())
        }

        val userHandleState = githubViewModel.userHandle.testIn(this)

        userHandleState.awaitItem().apply {
            assertThat(this, `is`(""))
        }

        githubViewModel.handleEvent(RepositoryListEvent.OnUserHandleChange("user"))

        userHandleState.awaitItem().apply {
            assertThat(this, `is`("user"))
        }

        userHandleState.cancelAndIgnoreRemainingEvents()
    }

    @Test
    fun `should emit correct list state when fetching repositories returns list`() = runTest {
        val testRepositories = listOf(
            Repository(
                id = 1L,
                name = "test",
                language = "test",
                ownerHandle = "test",
                ownerAvatarUrl = "test"
            ),
            Repository(
                id = 2L,
                name = "test2",
                language = "test2",
                ownerHandle = "test2",
                ownerAvatarUrl = "test2"
            )
        )
        coEvery {
            githubRepository.getRepositories(any(), any(), any())
        } coAnswers  {
            delay(700)
            BaseResult(data = testRepositories)
        }

        val listStateFlow = githubViewModel.repositoryListState.testIn(this)

        listStateFlow.awaitItem().apply {
            assert(this == RepositoryListState.Idle)
        }

        githubViewModel.handleEvent(RepositoryListEvent.OnUserHandleChange("user"))

        listStateFlow.awaitItem().apply {
            assert(this == RepositoryListState.Loading)
        }

        listStateFlow.awaitItem().apply {
            assert(this is RepositoryListState.Success)
            val state = this as RepositoryListState.Success
            assertThat(state.repositories.size, `is`(2))
        }

        listStateFlow.cancelAndIgnoreRemainingEvents()
    }

    @Test
    fun `should emit correct list state when fetching repositories returns empty list`() = runTest {
        coEvery {
            githubRepository.getRepositories(any(), any(), any())
        } coAnswers  {
            delay(700)
            BaseResult(data = emptyList())
        }

        val listStateFlow = githubViewModel.repositoryListState.testIn(this)

        listStateFlow.awaitItem().apply {
            assert(this == RepositoryListState.Idle)
        }

        githubViewModel.handleEvent(RepositoryListEvent.OnUserHandleChange("user"))

        listStateFlow.awaitItem().apply {
            assert(this == RepositoryListState.Loading)
        }

        listStateFlow.awaitItem().apply {
            assert(this == RepositoryListState.Empty)
        }

        listStateFlow.cancelAndIgnoreRemainingEvents()
    }

    @Test
    fun `should emit correct list state when fetching repositories returns error`() = runTest {
        coEvery {
            githubRepository.getRepositories(any(), any(), any())
        } coAnswers  {
            delay(700)
            BaseResult(error = BaseError("error"))
        }

        val listStateFlow = githubViewModel.repositoryListState.testIn(this)

        listStateFlow.awaitItem().apply {
            assert(this == RepositoryListState.Idle)
        }

        githubViewModel.handleEvent(RepositoryListEvent.OnUserHandleChange("user"))

        listStateFlow.awaitItem().apply {
            assert(this == RepositoryListState.Loading)
        }

        listStateFlow.awaitItem().apply {
            assert(this is RepositoryListState.Error)
            val state = this as RepositoryListState.Error
            assertThat(state.message, `is`("error"))
        }

        listStateFlow.cancelAndIgnoreRemainingEvents()
    }

    @Test
    fun `should emit correct list state when making paginated call`() = runTest {
        val page1 = listOf(
            Repository(
                id = 1L,
                name = "test",
                language = "test",
                ownerHandle = "test",
                ownerAvatarUrl = "test"
            ),
            Repository(
                id = 2L,
                name = "test2",
                language = "test2",
                ownerHandle = "test2",
                ownerAvatarUrl = "test2"
            )
        )
        val page2 = listOf(
            Repository(
                id = 3L,
                name = "test3",
                language = "test3",
                ownerHandle = "test3",
                ownerAvatarUrl = "test3"
            ),
            Repository(
                id = 4L,
                name = "test4",
                language = "test4",
                ownerHandle = "test4",
                ownerAvatarUrl = "test4"
            )
        )

        coEvery {
            githubRepository.getRepositories(any(), any(), any())
        } coAnswers  {
            delay(700)
            BaseResult(data = page1)
        }

        val listStateFlow = githubViewModel.repositoryListState.testIn(this)

        listStateFlow.awaitItem().apply {
            assert(this == RepositoryListState.Idle)
        }

        githubViewModel.handleEvent(RepositoryListEvent.OnUserHandleChange("user"))

        listStateFlow.awaitItem().apply {
            assert(this == RepositoryListState.Loading)
        }

        listStateFlow.awaitItem().apply {
            assert(this is RepositoryListState.Success)
            val state = this as RepositoryListState.Success
            assertThat(state.repositories.size, `is`(2))
        }

        coEvery {
            githubRepository.getRepositories(any(), any(), any())
        } coAnswers  {
            delay(700)
            BaseResult(data = page2)
        }

        githubViewModel.handleEvent(RepositoryListEvent.OnLoadMore)

        listStateFlow.awaitItem().apply {
            assert(this is RepositoryListState.LoadingMore)
        }

        listStateFlow.awaitItem().apply {
            assert(this is RepositoryListState.Success)
            val state = this as RepositoryListState.Success
            assertThat(state.repositories.size, `is`(4))
        }

        listStateFlow.cancelAndIgnoreRemainingEvents()
    }
}