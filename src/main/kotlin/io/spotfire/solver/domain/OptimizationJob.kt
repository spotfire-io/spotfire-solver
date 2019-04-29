package io.spotfire.solver.domain

import com.squareup.moshi.Json

data class OptimizationJob(
  @Json(name = "id")
  val id: String,

  @Json(name = "extract_path")
  val extractPath: String
)