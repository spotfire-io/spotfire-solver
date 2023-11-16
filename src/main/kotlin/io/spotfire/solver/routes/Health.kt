package io.spotfire.solver.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.healthRouting() {
  get("/health/ready") {
    call.respondText("Ready")
  }
  get("/health/live") {
    call.respondText("Alive")
  }
}
