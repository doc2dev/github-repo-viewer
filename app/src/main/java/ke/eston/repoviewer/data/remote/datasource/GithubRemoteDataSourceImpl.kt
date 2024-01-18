package ke.eston.repoviewer.data.remote.datasource

import com.slack.eithernet.ApiResult
import ke.eston.repoviewer.data.remote.api.GithubApi
import ke.eston.repoviewer.data.remote.dto.ErrorDto
import ke.eston.repoviewer.data.remote.dto.RepositoryDto

class GithubRemoteDataSourceImpl(
    private val githubApi: GithubApi
): GithubRemoteDataSource {
    override suspend fun getRepositories(
        handle: String,
        page: Int,
        perPage: Int
    ): ApiResult<List<RepositoryDto>, ErrorDto> = githubApi.getRepos(handle, page, perPage)
}