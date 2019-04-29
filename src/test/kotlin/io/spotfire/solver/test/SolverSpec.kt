package io.spotfire.solver.test

import com.squareup.moshi.Moshi
import io.spotfire.solver.solver.ConstraintViolationReporter
import io.spotfire.solver.solver.PlaylistSolverFactory
import io.spotfire.solver.solver.ProblemBuilder
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.BufferedReader
import kotlin.streams.toList
import kotlin.test.assertNotNull

object SolverSpec : Spek({
  fun <T>readFromJson(path: String, moshi: Moshi, clazz: Class<T>, associate: (List<T>) -> Pair<String, T>): List<T> {
    val adapter = moshi.adapter<T>(clazz)
    this.javaClass
    .getResourceAsStream(path)
      .bufferedReader()
      .use { br: BufferedReader ->
        return br
          .lines()
          .map<T>(adapter::fromJson)
          .filter { it != null }
          .toList()
      }
  }

  describe("A playlist package") {
    val folderPath = "/io/spotfire/solver/test/cedricShimmerWithHeat"

    val builder = ProblemBuilder(folderPath, readFromClasspath = true)
    val problem = builder.build()
    val solver = PlaylistSolverFactory().getSolver(problem)

    // this.javaClass.getResourceAsStream("${folderPath}/restTracks.jsonl").bufferedReader().use { reader ->
    //   reader.lines().forEach { line ->
    //     val pt = playlistTrackAdapter.fromJson(line)
    //     println(pt?.track)
    // }

    // }
    it("says hello") {
      val solution = solver.solve(problem)
      ConstraintViolationReporter.printViolations(solver, solution)
      assertNotNull(solution)
      // println(solution)
    }
  }
})