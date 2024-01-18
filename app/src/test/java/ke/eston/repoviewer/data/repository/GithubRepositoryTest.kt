package ke.eston.repoviewer.data.repository

import com.slack.eithernet.ApiResult
import io.mockk.coEvery
import io.mockk.mockk
import ke.eston.repoviewer.data.remote.datasource.GithubRemoteDataSource
import ke.eston.repoviewer.data.remote.dto.ErrorDto
import ke.eston.repoviewer.data.remote.dto.OwnerDto
import ke.eston.repoviewer.data.remote.dto.RepositoryDto
import ke.eston.repoviewer.domain.model.Repository
import ke.eston.repoviewer.domain.repository.GithubRepository
import ke.eston.repoviewer.domain.result.BaseResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GithubRepositoryTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val githubRemoteDataSource: GithubRemoteDataSource = mockk()
    private lateinit var githubRepository: GithubRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        githubRepository = GithubRepositoryImpl(githubRemoteDataSource)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should return correctly on success`() = runTest {
        val data = listOf(
            RepositoryDto(
                id = 1L,
                owner = OwnerDto(1L, "owner", "test"),
                name = "test",
                language = "test"
            )
        )

        coEvery {
            githubRemoteDataSource.getRepositories(any(), any(), any())
        } returns ApiResult.success(data)

        val result = githubRepository.getRepositories("user", 1, 10)
        assert(result.isSuccess)
        val dataOut = result.data!!
        assert(dataOut.size == 1)
        dataOut[0].apply {
            assert(id == 1L)
            assert(name == "test")
            assert(ownerHandle == "owner")
        }
    }

    @Test
    fun `should return correctly on error`() = runTest {
        coEvery {
            githubRemoteDataSource.getRepositories(any(), any(), any())
        } returns ApiResult.httpFailure(401, ErrorDto(message = "not_allowed"))

        val result = githubRepository.getRepositories("user", 1, 10)
        assert(!result.isSuccess)
        val error = result.error!!
        assert(error.code == 401)
        assert(error.message == "not_allowed")
    }
}