package io.spotfire.solver.domain

import com.squareup.moshi.Json

data class User(
    @Json(name = "display_name")
    val displayName: String,
    @Json(name = "user_id")
    val userId: String
)