package io.spotfire.solver.domain

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty
import org.optaplanner.core.api.domain.solution.PlanningScore
import org.optaplanner.core.api.domain.solution.PlanningSolution
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty
import org.optaplanner.core.api.domain.solution.drools.ProblemFactProperty
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider
import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScore
import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore

@PlanningSolution
data class PlaylistSolution (
  @ProblemFactProperty
  val firstPlaylistTrack: FirstPlaylistTrack? = null,

  @ValueRangeProvider(id = "firstTrack")
  val firstPlaylistTrackRange: List<FirstPlaylistTrack>? = null,

  @PlanningEntityCollectionProperty
  @ValueRangeProvider(id = "restTracks")
  val restPlaylistTrackRange: List<RestPlaylistTrack>? = null,

  @ProblemFactCollectionProperty
  val restTracks: List<Track>? = null,

  @ProblemFactCollectionProperty
  val genres: List<Genre>? = null,

  @ProblemFactCollectionProperty
  val artists: List<Artist>? = null,

  @ProblemFactCollectionProperty
  val albums: List<Album>? = null,

  @ProblemFactCollectionProperty
  val keys: List<Key>? = null,

  @PlanningScore(bendableHardLevelsSize = 1, bendableSoftLevelsSize = 3)
  var score: BendableBigDecimalScore? = null
)