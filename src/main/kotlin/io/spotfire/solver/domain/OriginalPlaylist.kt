package io.spotfire.solver.domain

import com.squareup.moshi.Json

data class OriginalPlaylist(
    @Json(name = "created_at")
    val createdAt: String,
    @Json(name = "href")
    val href: String?,
    @Json(name = "id")
    val id: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "owner")
    val owner: User,
    @Json(name = "playlist_id")
    val playlistId: String,
    @Json(name = "updated_at")
    val updatedAt: String
)