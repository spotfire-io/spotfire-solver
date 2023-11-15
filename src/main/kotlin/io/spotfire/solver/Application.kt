package io.spotfire.solver

import com.typesafe.config.ConfigFactory
import io.spotfire.solver.plugins.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
  val config = ConfigFactory.load() // Automatically loads the application.conf
  val ktorConfig = HoconApplicationConfig(config)
  val port = ktorConfig.property("ktor.application.port").getString().toInt()
  val host = ktorConfig.property("ktor.application.host").getString()

  val server = embeddedServer(Netty, port = port, host = host, module = Application::module).start(wait = true)

  Runtime.getRuntime().addShutdownHook(Thread {
    server.stop(10, 60)
  })
}

fun Application.module() {
  configureSerialization()
  configureRouting()
}
