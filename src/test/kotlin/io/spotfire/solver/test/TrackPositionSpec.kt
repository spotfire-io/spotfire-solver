package io.spotfire.solver.test

import io.spotfire.solver.domain.*
import io.spotfire.solver.domain.TrackPosition
import org.kie.api.io.ResourceType
import org.kie.internal.builder.KnowledgeBuilderFactory
import org.kie.internal.io.ResourceFactory
import org.kie.internal.logger.KnowledgeRuntimeLoggerFactory
import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScoreHolder
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals
import kotlin.test.assertTrue

fun makeTrack(trackId: String, albumId: String):Track {
  return Track(
    trackId = trackId,
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
    features = null,
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
    assertEquals( trackPositions.map { it.position }.toHashSet(), (1..12).toHashSet(),  "Tracks are numbered 1 to 12")
  }
})