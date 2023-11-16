package io.spotfire.solver.solver

import com.squareup.moshi.Json
import io.spotfire.solver.domain.PlaylistSolution
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.optaplanner.core.api.solver.Solver
@Serializable
data class SolverStatus(
  @SerialName("constraint_violations")
  val constraintViolations: List<ConstraintViolationSummary>,
  @SerialName("best_score")
  val bestScore: String,
  @SerialName("time_millis_spent")
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
