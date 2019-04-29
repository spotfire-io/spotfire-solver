package io.spotfire.solver.domain

import com.squareup.moshi.Json
import org.optaplanner.core.api.domain.lookup.PlanningId
import java.text.FieldPosition

data class FirstPlaylistTrack(
    @Json(name = "track")
    override var track: Track?
    // override var nextTrack: PlaylistTrack? = null,
    // override var position: Int? = 1
) : PlaylistTrack {
  override fun toString() = "$track"

  @PlanningId
  val trackId = this.track?.trackId

  override fun equals(other: Any?): Boolean {
    return if(other is RestPlaylistTrack) {
      this.track?.trackId == other.track?.trackId
    } else {
      false
    }
  }

  override fun hashCode(): Int = track.hashCode()
}