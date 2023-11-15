package io.spotfire.solver.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.spotfire.solver.lambda.SolverHandlerPayload

fun Route.solverRouting() {
  route("/solver") {
    post {
      val payload = call.receive<SolverHandlerPayload>()

      call.respondText("Route registered correctly", status = HttpStatusCode.Created)
    }
  }
}