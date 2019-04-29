package io.spotfire.solver.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger
import com.amazonaws.services.lambda.runtime.LambdaRuntime
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.expect

object SolverHandlerSpec : Spek({
  val handler = SolverHandler()

  describe("a service invocation") {
    val extractPath = "https://s3.amazonaws.com/spotfire-extracts/bfecb4726880f7d43b486ed69b56d48c13b1e3756a9269c003ced97d9383f215.tar.gz"

    val req = mapOf(
      "body" to """{"extract_path": "$extractPath"}"""
    )

    val context = mock<Context> {
      on { logger } doReturn LambdaRuntime.getLogger()
    }

    val resp = handler.handleRequest(req, context)

    it("returns a response") {
      println(resp)
    }
  }
})