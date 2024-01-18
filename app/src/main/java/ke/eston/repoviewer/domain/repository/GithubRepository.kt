package ke.eston.repoviewer.domain.repository

import ke.eston.repoviewer.domain.model.Repository
import ke.eston.repoviewer.domain.result.BaseResult

interface GithubRepository {
    suspend fun getRepositories(
        userHandle: String,
        page: Int,
        perPage: Int
    ): BaseResult<List<Repository>>
}