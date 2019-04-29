package io.spotfire.solver.domain

import com.squareup.moshi.Json

data class AudioFeatures(
    @Json(name = "acousticness")
    val acousticness: Double,
    @Json(name = "danceability")
    val danceability: Double,
    @Json(name = "energy")
    val energy: Double,
    @Json(name = "instrumentalness")
    val instrumentalness: Double,
    @Json(name = "key")
    var key: Key,
    @Json(name = "liveness")
    val liveness: Double,
    @Json(name = "speechiness")
    val speechiness: Double,
    @Json(name = "tempo")
    val tempo: Double,
    @Json(name = "time_signature")
    val timeSignature: Int,
    @Json(name = "valence")
    val valence: Double
)