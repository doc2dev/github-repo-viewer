package ke.eston.repoviewer.data.remote.api

import com.slack.eithernet.ApiResult
import ke.eston.repoviewer.data.remote.dto.ErrorDto
import ke.eston.repoviewer.data.remote.dto.RepositoryDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApi {
    @GET("users/{handle}/repos")
    suspend fun getRepos(
        @Path("handle") handle: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): ApiResult<List<RepositoryDto>, ErrorDto>
}