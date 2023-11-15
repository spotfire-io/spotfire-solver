package io.spotfire.solver.solver

import com.squareup.moshi.Json
import kotlinx.serialization.Serializable
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal
@Serializable
data class ConstraintViolationSummary(
  val constraintName: String,

  val violationCount: Int,

  val scoreImpact: String
) {
  constructor(cmt: ConstraintMatchTotal): this(
    cmt.constraintName,
    cmt.constraintMatchCount,
    cmt.score.toString()
  )
}