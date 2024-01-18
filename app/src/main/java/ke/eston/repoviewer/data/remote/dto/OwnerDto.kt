package ke.eston.repoviewer.data.remote.dto

import com.google.gson.annotations.SerializedName

data class OwnerDto(
    val id: Long,
    val login: String,
    @SerializedName("avatar_url")
    val avatarUrl: String
)
