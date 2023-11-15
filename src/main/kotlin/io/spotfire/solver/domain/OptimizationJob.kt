package io.spotfire.solver.domain

import kotlinx.serialization.Serializable

@Serializable
data class OptimizationJob(
  val id: String,
  val extractPath: String
)