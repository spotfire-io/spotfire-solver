package io.spotfire.solver.domain

import com.squareup.moshi.Json

data class RootNote(
    @Json(name = "label")
    val label: String
)