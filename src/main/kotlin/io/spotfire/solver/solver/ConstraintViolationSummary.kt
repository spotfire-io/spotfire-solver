package io.spotfire.solver.solver

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal
@Serializable
data class ConstraintViolationSummary(
  @SerialName("constraint_name")
  val constraintName: String,

  @SerialName("violation_count")
  val violationCount: Int,

  @SerialName("score_impact")
  val scoreImpact: String
) {
  constructor(cmt: ConstraintMatchTotal): this(
    cmt.constraintName,
    cmt.constraintMatchCount,
    cmt.score.toString()
  )
}
