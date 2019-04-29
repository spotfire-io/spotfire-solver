package io.spotfire.solver.solver

import com.squareup.moshi.Json
import io.spotfire.solver.domain.PlaylistSolution
import org.optaplanner.core.api.solver.Solver

data class SolverStatus(
  @Json(name = "constraint_violations")
  val constraintViolations: List<ConstraintViolationSummary>,

  @Json(name = "best_score")
  val bestScore: String,

  @Json(name = "time_millis_spent")
  val timeMillisSpent: Long
)
{
  companion object {
    fun fromSolution(solver: Solver<PlaylistSolution>, solution: PlaylistSolution): SolverStatus {
      val scoreDirector = solver.scoreDirectorFactory.buildScoreDirector()
      scoreDirector.workingSolution = solution
      solver.bestScore
      return SolverStatus(
        bestScore = solver.bestScore.toShortString(),
        timeMillisSpent = solver.timeMillisSpent,
        constraintViolations = scoreDirector
          .constraintMatchTotals
          .sortedBy { it.score }
          .map { cmt -> ConstraintViolationSummary(cmt) }
      )
    }
  }
}