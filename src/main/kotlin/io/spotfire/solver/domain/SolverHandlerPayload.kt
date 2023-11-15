package io.spotfire.solver.domain

import kotlinx.serialization.Serializable
@Serializable
data class Customer(val id: String, val firstName: String, val lastName: String, val email: String)

@Serializable
data class SolverHandlerPayload(
  val job: OptimizationJob,
  val accessToken: String?,
  val graphqlEndpointURL: String?
)