package ke.eston.repoviewer.domain.result

data class BaseResult<T>(var data: T? = null, var error: BaseError? = null) {
    val isSuccess: Boolean
        get() = error == null
}
