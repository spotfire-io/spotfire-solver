package io.spotfire.solver.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OptimizationJob(
  @SerialName("id")
  val id: String,
  @SerialName("extract_path")
  val extractPath: String
)
