package io.spotfire.solver.domain

import kotlinx.serialization.Serializable

@Serializable
data class SolverHandlerPayload(
  val job: OptimizationJob,
  val accessToken: String?,
  val graphqlEndpointURL: String?
)