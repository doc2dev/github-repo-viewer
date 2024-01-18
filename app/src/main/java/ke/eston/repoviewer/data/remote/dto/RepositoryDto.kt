package ke.eston.repoviewer.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RepositoryDto(
    val id: Long,
    val owner: OwnerDto,
    val name: String,

)
