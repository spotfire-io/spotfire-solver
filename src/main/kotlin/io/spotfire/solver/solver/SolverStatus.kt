package io.spotfire.solver.solver

import com.squareup.moshi.Json
import io.spotfire.solver.domain.PlaylistSolution
import kotlinx.serialization.Serializable
import org.optaplanner.core.api.solver.Solver
@Serializable
data class SolverStatus(
  val constraintViolations: List<ConstraintViolationSummary>,

  val bestScore: String,

  val timeMillisSpent: Long
)
{
  companion object {
    fun fromSolution(solver: Solver<PlaylistSolution>, solution: PlaylistSolution): SolverStatus {
      val scoreDirector = solver.scoreDirectorFactory.buildScoreDirector()
      scoreDirector.workingSolution = solution
      solver.bestScore
      return SolverStatus(
        bestScore = solver.bestScore.toString(),
        timeMillisSpent = solver.timeMillisSpent,
        constraintViolations = scoreDirector
          .constraintMatchTotals
          .sortedBy { it.score }
          .map { cmt -> ConstraintViolationSummary(cmt) }
      )
    }
  }
}