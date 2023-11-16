package io.spotfire.solver.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OptimizationJob(
  @SerialName("id")
  val id: String,
  @SerialName("extractPath")
  val extractPath: String
)
