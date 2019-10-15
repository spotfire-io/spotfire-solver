package io.spotfire.solver.domain

import com.squareup.moshi.Json
import io.spotfire.solver.solver.PreviousTrackUpdatedListener
import org.optaplanner.core.api.domain.entity.PlanningEntity
import org.optaplanner.core.api.domain.lookup.PlanningId
import org.optaplanner.core.api.domain.variable.*

@PlanningEntity
data class RestPlaylistTrack(
  @Json(name = "track")
  override var track: Track? = null,

  @PlanningVariable(
    graphType = PlanningVariableGraphType.CHAINED,
    valueRangeProviderRefs = ["firstTrack", "restTracks"]
  )
  var previousTrack: PlaylistTrack? = null,
  //override var nextTrack: PlaylistTrack? = null,

  @CustomShadowVariable(
     variableListenerClass = PreviousTrackUpdatedListener::class,
     sources = [
       PlanningVariableReference(variableName = "previousTrack")
     ]
   )
  override var position: Int? = null,

  @CustomShadowVariable(
    variableListenerClass = PreviousTrackUpdatedListener::class,
    sources = [PlanningVariableReference(variableName = "previousTrack")]
  )
  override var keyDistance: Int? = Int.MAX_VALUE,

  @CustomShadowVariable(
    variableListenerClass = PreviousTrackUpdatedListener::class,
    sources = [PlanningVariableReference(variableName = "previousTrack")]
  )
  var exponentialDecayKeyDistance: Double? = null
) : PlaylistTrack {
  override fun toString() = "$track"

  override fun equals(other: Any?): Boolean {
    return if(other is RestPlaylistTrack) {
      this.track?.trackId == other.track?.trackId
    } else {
      false
    }
  }

  @PlanningId
  val trackId = this.track?.trackId

  override fun hashCode(): Int = track.hashCode()
}