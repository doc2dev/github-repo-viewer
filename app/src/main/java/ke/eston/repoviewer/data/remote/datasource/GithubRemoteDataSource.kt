package ke.eston.repoviewer.data.remote.datasource

import com.slack.eithernet.ApiResult
import ke.eston.repoviewer.data.remote.dto.ErrorDto
import ke.eston.repoviewer.data.remote.dto.RepositoryDto

interface GithubRemoteDataSource {
    suspend fun getRepositories(
        handle: String,
        page: Int,
        perPage: Int
    ): ApiResult<List<RepositoryDto>, ErrorDto>
}