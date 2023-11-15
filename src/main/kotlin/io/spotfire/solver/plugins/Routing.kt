package io.spotfire.solver.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.spotfire.solver.routes.solverRouting

fun Application.configureRouting() {
  routing {
    solverRouting()
  }
}