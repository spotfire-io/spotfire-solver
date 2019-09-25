package io.spotfire.solver.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaRuntime
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.github.cdimascio.dotenv.dotenv
import io.spotfire.solver.domain.OptimizationJob
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertNotNull
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.jsonwebtoken.Jwts
import java.sql.Date


object SolverHandlerSpec : Spek({
  val handler = SolverHandler()
  val dotenv = dotenv {
    ignoreIfMissing = true
  }
  val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

  val payloadAdapter = moshi.adapter(SolverHandlerPayload::class.java)

  val accessToken = dotenv.get("ACCESS_TOKEN")
  beforeGroup {
    assertNotNull(accessToken, "Must specify ACCESS_TOKEN")
//    val claims = Jwts.parser().parseClaimsJws(accessToken).body
//    assert(claims.expiration > java.util.Date(), { -> "Access Token Expired ${claims.expiration}"})
  }

  describe("a service invocation") {
    val payload = mapOf(
      "job" to mapOf(
        "id" to "lies",
        "extractPath" to dotenv.get(
          "EXTRACT_PATH",
          "https://s3.amazonaws.com/spotfire-extracts/c77bfcd0a39efa1f6390bc1230a2368e24cf574c6d3d6d0834d0b69b3afa52ee.tar.gz"
        )
      ),
      "accessToken" to accessToken!!,
      "graphqlEndpointURL" to dotenv.get("GRAPHQL_ENDPOINT_URL", "http://api.spotfire.spantree.net")
    )

    val context = mock<Context> {
      on { logger } doReturn LambdaRuntime.getLogger()
    }

    val resp = handler.handleRequest(payload, context)

    it("returns a response") {
      println(resp)
    }
  }
})