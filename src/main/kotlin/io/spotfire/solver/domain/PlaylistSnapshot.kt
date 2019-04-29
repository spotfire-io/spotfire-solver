package io.spotfire.solver.domain
import com.squareup.moshi.Json

data class PlaylistSnapshot(
    @Json(name = "id")
    val id: String,
    @Json(name = "loaded_tracks")
    val loadedTracks: Int,
    @Json(name = "playlist")
    val playlist: OriginalPlaylist,
    @Json(name = "snapshot_id")
    val snapshotId: String,
    @Json(name = "track_count")
    val trackCount: Int
)

