package io.spotfire.solver.domain
import com.squareup.moshi.Json
import java.time.LocalDate

data class Album(
    @Json(name = "album_id")
    val albumId: String,
    @Json(name = "artists")
    var artists: List<Artist>?,
    @Json(name = "album_type")
    val albumType: String?,
    @Json(name = "genres")
    var genres: List<Genre>?,
    @Json(name = "id")
    val id: String,
    @Json(name = "label")
    val label: String?,
    @Json(name = "name")
    val name: String?,
    @Json(name = "popularity")
    val popularity: Int?,
    // @Json(name = "release_date")
    // val releaseDate: LocalDate,
    @Json(name = "release_date_precision")
    val releaseDatePrecision: ReleaseDatePrecision?
)