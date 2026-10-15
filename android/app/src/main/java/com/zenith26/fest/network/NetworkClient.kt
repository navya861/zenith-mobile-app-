package com.zenith26.fest.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.zenith26.fest.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton networking manager for creating and maintaining the Retrofit API service.
 * Allows runtime configuration of backend Base URL for emulator vs. physical device testing.
 */
object NetworkClient {

    /**
     * Default base URL configured via build config (defaults to http://10.0.2.2:8000/).
     * For physical device testing, use the local Wi-Fi IP (e.g., http://192.168.1.X:8000/).
     */
    var currentBaseUrl: String = BuildConfig.DEFAULT_BASE_URL
        private set

    val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    @Volatile
    private var apiService: ZenithApiService? = null

    fun getService(): ZenithApiService {
        return apiService ?: synchronized(this) {
            apiService ?: buildRetrofit(currentBaseUrl).create(ZenithApiService::class.java).also {
                apiService = it
            }
        }
    }

    /**
     * Updates backend base URL at runtime (e.g. for switching from emulator 10.0.2.2 to LAN IP).
     */
    fun updateBaseUrl(newUrl: String) {
        val sanitizedUrl = if (newUrl.endsWith("/")) newUrl else "$newUrl/"
        if (currentBaseUrl != sanitizedUrl) {
            currentBaseUrl = sanitizedUrl
            synchronized(this) {
                apiService = buildRetrofit(sanitizedUrl).create(ZenithApiService::class.java)
            }
        }
    }

    private fun buildRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}
