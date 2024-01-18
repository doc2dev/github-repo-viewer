package ke.eston.repoviewer.domain.model

data class Repository(
    val id: Long,
    val name: String,
    val language: String,
    val ownerHandle: String,
    val ownerAvatarUrl: String
)
