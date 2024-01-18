package ke.eston.repoviewer.domain.result

import com.slack.eithernet.ApiResult
import ke.eston.repoviewer.data.remote.dto.ErrorDto

data class BaseError(var message: String = "", var code: Int = -1) {
    companion object {
        fun of(apiResult: ApiResult<Any, ErrorDto>): BaseError {
            return when (apiResult) {
                is ApiResult.Failure.NetworkFailure -> {
                    apiResult.error.printStackTrace()
                    BaseError(
                        message = apiResult.error.message.orEmpty(),
                        code = 500
                    )
                }

                is ApiResult.Failure.HttpFailure -> BaseError(
                    message = apiResult.error?.message.orEmpty(),
                    code = apiResult.code
                )

                is ApiResult.Failure.ApiFailure -> BaseError(
                    message = apiResult.error?.message.orEmpty(),
                    code = 200
                )

                is ApiResult.Failure.UnknownFailure -> {
                    apiResult.error.printStackTrace()
                    BaseError(
                        message = apiResult.error.message.orEmpty(),
                        code = 500
                    )
                }

                else -> BaseError()
            }
        }
    }
}
