package io.spotfire.solver.lambda

import com.squareup.moshi.Json
import io.spotfire.solver.domain.OptimizationJob
import kotlinx.serialization.Serializable

@Serializable
data class SolverHandlerPayload(
  val job: OptimizationJob,
  @Json(name = "bearer_token")
  val accessToken: String?,
  @Json(name = "graphql_endpoint_url")
  val graphqlEndpointURL: String?
)
