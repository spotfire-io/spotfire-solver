package io.spotfire.solver.domain

import com.squareup.moshi.Json

data class OriginalPlaylistTrack(
  @Json(name = "track")
  override var track: Track?,

  @Json(name = "order")
  override var position: Int?,

  @Transient
  override var keyDistance: Int? = Int.MAX_VALUE

  // @Transient
  // override var nextTrack: PlaylistTrack? = null
): PlaylistTrack