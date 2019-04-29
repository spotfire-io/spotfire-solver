package io.spotfire.solver.solver

import io.spotfire.solver.domain.PlaylistSolution
import io.spotfire.solver.domain.RestPlaylistTrack
import org.optaplanner.core.api.solver.Solver
import org.slf4j.LoggerFactory

object ConstraintViolationReporter {
  val log = LoggerFactory.getLogger(this.javaClass)

  fun getViolationSummaries(solver: Solver<PlaylistSolution>, solution: PlaylistSolution): List<ConstraintViolationSummary> {
    val scoreDirector = solver.scoreDirectorFactory.buildScoreDirector()
    scoreDirector.workingSolution = solution

    return scoreDirector.constraintMatchTotals.sortedBy { it.score }.map { mt ->
      ConstraintViolationSummary(mt.constraintName, mt.constraintMatchCount, mt.score.toShortString())
    }
  }

  fun printViolations(solver: Solver<*>, solution: PlaylistSolution) {
    val scoreDirector = solver.scoreDirectorFactory.buildScoreDirector()
    scoreDirector.workingSolution = solution

    log.info("Score: ${solution.score}")

    scoreDirector.constraintMatchTotals.sortedBy { it.score }.forEach { mt ->
      val violationSummary = "${mt.constraintName} -> violations: ${mt.constraintMatchCount}, score impact: ${mt.score.toShortString()}"
      log.info(violationSummary)
      mt.constraintMatchSet.sortedBy { it.score }.forEachIndexed { i, match ->
        log.debug("  - Violation $i, score impact: (${match.score})")
        match.justificationList.forEach { obj ->
          if(obj is RestPlaylistTrack) {
            log.debug("    - ${obj.previousTrack} -> $obj")
          }
        }
      }
    }
  }
}