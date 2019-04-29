package io.spotfire.solver.domain
import com.squareup.moshi.Json

data class Artist(
    @Json(name = "artist_id")
    val artistId: String,
    @Json(name = "follower_count")
    val followerCount: Int?,
    @Json(name = "genres")
    var genres: List<Genre>?,
    @Json(name = "id")
    val id: String,
    @Json(name = "name")
    val name: String?,
    @Json(name = "popularity")
    val popularity: Int?
)