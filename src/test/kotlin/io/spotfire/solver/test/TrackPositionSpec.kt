package io.spotfire.solver.test

import io.spotfire.solver.domain.*
import io.spotfire.solver.domain.TrackPosition
import io.spotfire.solver.solver.ConstraintViolationReporter
import io.spotfire.solver.solver.PlaylistSolverFactory
import org.kie.api.io.ResourceType
import org.kie.api.runtime.rule.FactHandle
import org.kie.internal.builder.KnowledgeBuilderFactory
import org.kie.internal.io.ResourceFactory
import org.kie.internal.logger.KnowledgeRuntimeLoggerFactory
import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScoreHolder
import org.optaplanner.core.config.solver.EnvironmentMode
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

fun makeTrack(
  trackId: String,
  albumId: String,
  key: Key = Key(
    id = null,
    label = "Bminor",
    mode = null,
    rootNote = null,
    camelotCode = null,
    camelotPosition = 0
  )
):Track {
  return Track(
    trackId = trackId,
    features = AudioFeatures(
      tempo= 104.817,
      valence = 0.477,
      energy = 0.767,
      liveness = 0.117,
      speechiness = 0.11,
      acousticness = 0.366,
      timeSignature = 3,
      danceability = 0.415,
      instrumentalness = 0.797,
      key = key
    ),
    album = Album(
      albumId = albumId,
      artists = null,
      albumType = null,
      genres = null,
      id = albumId,
      label = null,
      name = null,
      popularity = null,
      releaseDatePrecision = null
    ),
    artists = emptyList(),
    durationMs = 1,
    explicit = false,
    id = trackId,

    name = "${trackId}Name",
    popularity = 1,
    trackNumber = 1
  )
}

object TrackPositionSpec : Spek({
  val kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder()
  val drlStream = this.javaClass.getResourceAsStream("/io/spotfire/solver/rules/rules.drl")
  kbuilder.add(ResourceFactory.newInputStreamResource(drlStream), ResourceType.DRL)

  describe("Track position is set correctly") {
    val kbase = kbuilder.newKieBase()
    val ksession = kbase.newKieSession()
    val scoreHolder = BendableBigDecimalScoreHolder(true, 100, 100)
    ksession.setGlobal("scoreHolder", scoreHolder)


    val firstTrack = FirstPlaylistTrack(makeTrack("first", "album1"))
    ksession.insert(firstTrack)

    var prevTrack: PlaylistTrack = firstTrack
    for(i in 0..10) {
      val nextTrack = RestPlaylistTrack(previousTrack = prevTrack, track = makeTrack("track${i}", "album1"))
      ksession.insert(nextTrack)
      prevTrack = nextTrack
    }

    ksession.fireAllRules()

    val trackPositions = ksession.getObjects { f -> f is TrackPosition } as Collection<TrackPosition>
    assertTrue( trackPositions.size == 12, "There are 12 tracks" )
    assertEquals( trackPositions.map { it.position }.toHashSet(), (1..12).toHashSet(),  "Track positions are numbered 1 to 12")
    assertEquals( trackPositions.map { it.position }.toList(), trackPositions.map { it.track.position }.toList(),  "Tracks are numbered same as track positions")
  }

  describe("Key repeats are avoided") {

    val key0 = Key(
      id = null,
      label = "Bminor",
      mode = null,
      rootNote = null,
      camelotCode = null,
      camelotPosition = 0
    )

    val key1 = Key(
      id = null,
      label = "Bminor",
      mode = null,
      rootNote = null,
      camelotCode = null,
      camelotPosition = 1
    )

    val firstTrack = makeTrack("first", "album1", key0)
    val restTracks = (1..8)
        .map { makeTrack("track${it}Key0", "album1", key0) }
        .plus( makeTrack("trackKey1", "album1", key1) )

    val firstPlaylistTrack = FirstPlaylistTrack(firstTrack)

    val problem = PlaylistSolution(
      firstPlaylistTrack = firstPlaylistTrack,
      restTracks = restTracks,
      firstPlaylistTrackRange = listOf(firstPlaylistTrack),
      restPlaylistTrackRange = restTracks.map { RestPlaylistTrack(it) },
      albums = emptyList(),
      artists = emptyList(),
      genres = emptyList(),
      keys = emptyList()
    )

    val solver = PlaylistSolverFactory().getSolver(
      problem,
      secondsSpentLimit = 5,
      envMode = EnvironmentMode.FULL_ASSERT
    )

    val solution = solver.solve(problem)

    assertTrue(ConstraintViolationReporter.getViolationSummaries(solver, solution)
      .filter { it.constraintName == "Track key repeats should be avoided" }
      .isEmpty(),
      "Track key repeat has no violations"
    )

    assertEquals(
      "trackKey1",
      solution.orderedTrackList[4].track?.trackId,
      "Track is placed in all pertinent windows"
    )
  }
})