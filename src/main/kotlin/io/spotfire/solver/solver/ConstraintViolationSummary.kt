package io.spotfire.solver.solver

import com.squareup.moshi.Json
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal

data class ConstraintViolationSummary(
  @Json(name = "constraint_name")
  val constraintName: String,

  @Json(name = "violation_count")
  val violationCount: Int,

  @Json(name = "score_impact")
  val scoreImpact: String
) {
  constructor(cmt: ConstraintMatchTotal): this(
    cmt.constraintName,
    cmt.constraintMatchCount,
    cmt.score.toShortString()
  )
}