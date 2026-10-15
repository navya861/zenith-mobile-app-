package com.zenith26.fest.network

import com.zenith26.fest.data.model.Event
import com.zenith26.fest.data.model.RegistrationRequest
import com.zenith26.fest.data.model.RegistrationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit REST interface for the Zenith '26 FastAPI backend.
 * Endpoints and query parameters strictly preserve the existing backend contracts.
 */
interface ZenithApiService {

    /**
     * GET /api/events
     * Supports optional category and search filters.
     */
    @GET("api/events")
    suspend fun getEvents(
        @Query("category") category: String? = null,
        @Query("search") search: String? = null
    ): Response<List<Event>>

    /**
     * GET /api/events/{event_id}
     * Returns event details by ID.
     */
    @GET("api/events/{event_id}")
    suspend fun getEvent(
        @Path("event_id") eventId: Int
    ): Response<Event>

    /**
     * POST /api/events/{event_id}/register
     * Submits student registration for an event.
     */
    @POST("api/events/{event_id}/register")
    suspend fun registerForEvent(
        @Path("event_id") eventId: Int,
        @Body payload: RegistrationRequest
    ): Response<RegistrationResponse>
}
