package ke.eston.repoviewer.data.repository

import com.slack.eithernet.ApiResult
import ke.eston.repoviewer.data.remote.datasource.GithubRemoteDataSource
import ke.eston.repoviewer.domain.model.Repository
import ke.eston.repoviewer.domain.repository.GithubRepository
import ke.eston.repoviewer.domain.result.BaseError
import ke.eston.repoviewer.domain.result.BaseResult
import timber.log.Timber

class GithubRepositoryImpl(
    private val dataSource: GithubRemoteDataSource
) : GithubRepository {
    override suspend fun getRepositories(
        userHandle: String,
        page: Int,
        perPage: Int
    ): BaseResult<List<Repository>> {
        val result = BaseResult<List<Repository>>()
        when (val apiResult = dataSource.getRepositories(userHandle, page, perPage)) {
            is ApiResult.Success -> {
                result.data = apiResult.value.map {
                    Timber.tag("FOO").d("API item: $it")
                    Repository(
                        it.id,
                        it.name,
                        it.language,
                        it.owner.login,
                        it.owner.avatarUrl,
                    )
                }
            }

            else -> {
                result.error = BaseError.of(apiResult)
            }
        }
        return result
    }
}