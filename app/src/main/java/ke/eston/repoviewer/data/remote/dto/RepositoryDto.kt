package ke.eston.repoviewer.data.remote.dto

data class RepositoryDto(
    val id: Long,
    val owner: OwnerDto,
    val name: String,
    val language: String
)
