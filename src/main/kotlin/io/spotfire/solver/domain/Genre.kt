package io.spotfire.solver.domain

import com.squareup.moshi.Json

data class Genre(
    @Json(name = "name")
    val name: String
)