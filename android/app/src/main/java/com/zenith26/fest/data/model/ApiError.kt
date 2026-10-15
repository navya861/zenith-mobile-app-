package com.zenith26.fest.data.model

import com.google.gson.annotations.SerializedName

/**
 * Standard FastAPI error response structure: {"detail": "..."}
 */
data class ApiError(
    @SerializedName("detail")
    val detail: String? = null
)
