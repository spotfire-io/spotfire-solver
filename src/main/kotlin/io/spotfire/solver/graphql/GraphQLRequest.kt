package io.spotfire.solver.graphql

import com.squareup.moshi.Json

data class GraphQLRequest(
  @Json(name = "query")
  val query: String,

  @Json(name = "operationName")
  val operationName: String? = null,

  @Json(name = "variables")
  val variables: Map<String, Any> = mapOf()
)