package io.spotfire.solver.domain

import com.squareup.moshi.Json

data class Track(
    @Json(name = "album")
    var album: Album,
    @Json(name = "artists")
    var artists: List<Artist>,
    @Json(name = "duration_ms")
    val durationMs: Int,
    @Json(name = "explicit")
    val explicit: Boolean,
    @Json(name = "features")
    val features: AudioFeatures,
    @Json(name = "id")
    val id: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "popularity")
    val popularity: Int,
    @Json(name = "track_id")
    val trackId: String,
    @Json(name = "track_number")
    val trackNumber: Int
) {
  override fun toString(): String {
    val artistsList = this.artists.map { a -> a.name }.joinToString(" + ")
    return "$artistsList - $name (${features.key.camelotCode})"
  }
}