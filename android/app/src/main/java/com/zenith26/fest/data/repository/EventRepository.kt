package com.zenith26.fest.data.repository

import com.zenith26.fest.data.model.ApiError
import com.zenith26.fest.data.model.Event
import com.zenith26.fest.data.model.RegistrationRequest
import com.zenith26.fest.data.model.RegistrationResponse
import com.zenith26.fest.network.NetworkClient
import retrofit2.Response

/**
 * Result wrapper for API operations.
 */
sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Error(val code: Int?, val message: String) : NetworkResult<Nothing>()
    data class Failure(val exception: Throwable) : NetworkResult<Nothing>()
}

/**
 * Repository handling events and registrations data from FastAPI backend.
 */
class EventRepository {

    private val apiService get() = NetworkClient.getService()

    suspend fun getEvents(category: String? = null, search: String? = null): NetworkResult<List<Event>> {
        return try {
            val filterCategory = if (category.isNullOrBlank() || category.equals("All", ignoreCase = true)) {
                null
            } else {
                category
            }
            val filterSearch = if (search.isNullOrBlank()) null else search.trim()

            val response = apiService.getEvents(filterCategory, filterSearch)
            handleResponse(response)
        } catch (e: Exception) {
            NetworkResult.Failure(e)
        }
    }

    suspend fun getEvent(eventId: Int): NetworkResult<Event> {
        return try {
            val response = apiService.getEvent(eventId)
            handleResponse(response)
        } catch (e: Exception) {
            NetworkResult.Failure(e)
        }
    }

    suspend fun registerForEvent(
        eventId: Int,
        payload: RegistrationRequest
    ): NetworkResult<RegistrationResponse> {
        return try {
            val response = apiService.registerForEvent(eventId, payload)
            handleResponse(response)
        } catch (e: Exception) {
            NetworkResult.Failure(e)
        }
    }

    private fun <T> handleResponse(response: Response<T>): NetworkResult<T> {
        if (response.isSuccessful) {
            val body = response.body()
            return if (body != null) {
                NetworkResult.Success(body)
            } else {
                NetworkResult.Error(response.code(), "Empty response received from server.")
            }
        }

        // Parse FastAPI {"detail": "..."} error format
        val errorBodyStr = response.errorBody()?.string()
        val errorDetail = try {
            if (!errorBodyStr.isNullOrBlank()) {
                val parsed = NetworkClient.gson.fromJson(errorBodyStr, ApiError::class.java)
                parsed?.detail ?: errorBodyStr
            } else {
                "HTTP error ${response.code()}: ${response.message()}"
            }
        } catch (_: Exception) {
            errorBodyStr ?: "HTTP error ${response.code()}: ${response.message()}"
        }

        return NetworkResult.Error(response.code(), errorDetail)
    }
}
