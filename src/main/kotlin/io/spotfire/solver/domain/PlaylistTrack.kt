package io.spotfire.solver.domain
import com.squareup.moshi.Json

interface PlaylistTrack {
    @Json(name = "track")
    var track: Track?
    var position: Int?
    var keyDistance: Int?
}

