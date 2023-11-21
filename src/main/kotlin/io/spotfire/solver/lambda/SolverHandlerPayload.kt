package io.spotfire.solver.lambda

import io.spotfire.solver.domain.OptimizationJob
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SolverHandlerPayload(
  val job: OptimizationJob,
  @SerialName("bearer_token")
  val accessToken: String?,
  @SerialName("graphql_endpoint_url")
  val graphqlEndpointURL: String?
)
