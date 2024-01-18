package ke.eston.repoviewer.data.remote.api

import com.slack.eithernet.ApiResultCallAdapterFactory
import com.slack.eithernet.ApiResultConverterFactory
import ke.eston.repoviewer.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

fun getRetrofit(): Retrofit {
    val clientBuilder = OkHttpClient.Builder()
    if (BuildConfig.DEBUG) {
        clientBuilder.addInterceptor(loggingInterceptor())
    }
    clientBuilder.addInterceptor(authInterceptor())

    return Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addCallAdapterFactory(ApiResultCallAdapterFactory)
        .addConverterFactory(ApiResultConverterFactory)
        .addConverterFactory(GsonConverterFactory.create())
        .client(clientBuilder.build())
        .build()
}

private fun authInterceptor(): Interceptor {
    return Interceptor { chain ->
        val token = BuildConfig.GITHUB_API_KEY
        var request = chain.request()
        request = request.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return@Interceptor chain.proceed(request)
    }
}

private fun loggingInterceptor(): HttpLoggingInterceptor {
    val logger = HttpLoggingInterceptor.Logger {
        Timber.tag("OkHttp").d(it)
    }
    return HttpLoggingInterceptor(logger).apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }
}
