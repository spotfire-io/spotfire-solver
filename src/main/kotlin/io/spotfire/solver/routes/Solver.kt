package io.spotfire.solver.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.spotfire.solver.lambda.SolverHandler
import io.spotfire.solver.domain.SolverHandlerPayload
import io.spotfire.solver.solver.PlaylistSolverFactory
import io.spotfire.solver.solver.ProblemBuilder
import io.spotfire.solver.solver.SolverStatus
import io.spotfire.solver.solver.UpdateJobSolverEventListener
import okhttp3.Request
import org.codehaus.plexus.archiver.tar.TarGZipUnArchiver
import org.optaplanner.core.impl.solver.AbstractSolver
import java.io.File


private fun extractToDir(srcFile: File, destDir: File) {
  val ua = TarGZipUnArchiver(srcFile)
  val logger = SolverHandler.uaLogger.getLoggerForComponent(srcFile.absolutePath, "extractor")
  ua.enableLogging(logger)
  ua.destDirectory = destDir
  ua.extract()
}

private fun getExtractHashFromPath(path: String): String? {
  val matchResult = SolverHandler.extractPathRegex.matchEntire(path)
  return matchResult?.groupValues?.get(2)
}

private fun downloadAndExtractFile(path: String): File {
  SolverHandler.logger.info("Extracting file from $path")
  getExtractHashFromPath(path)!!.let { extractHash ->
    val extractDir = File("/tmp/spotfire-extracts/$extractHash")
    if (!extractDir.exists() || extractDir.list().isEmpty()) {
      extractDir.mkdirs()
      var attempts = 0
      while(attempts++ < 5) {
        val extractReq = Request.Builder().url(path).build()
        val extractResp = SolverHandler.httpClient.newCall(extractReq).execute()

        if(extractResp.code() != 200) {
          val pauseMs = 500*Math.pow(2.toDouble(), attempts.toDouble()).toLong()
          SolverHandler.logger.warn("Received status code ${extractResp.code()} when attempting to fetch $path, attempt #${attempts}. Pausing ${pauseMs} before retrying.")
          Thread.sleep(pauseMs)
        } else {
          val tmpFile = createTempFile(suffix = ".tar.gz")
          extractResp.body()?.byteStream()?.use { bodyStream ->
            tmpFile.outputStream().use { tmpFileStream ->
              bodyStream.copyTo(tmpFileStream)
            }
            SolverHandler.logger.info("Wrote extract file to $tmpFile")
            SolverHandler.logger.info("Extracted file to $extractDir")
            extractToDir(tmpFile, extractDir)
          } ?: run {
            throw Exception("Could not get bytestream from extract file $path")
          }
          break
        }
      }
      if(attempts > 5) {
        throw Exception("Could not retrieve file from path $path")
      }
    }

    return extractDir
  }
}
fun Route.solverRouting() {
  route("/solver") {
    post {

      try {
        val payload = call.receive<SolverHandlerPayload>()

        val extractDir = downloadAndExtractFile(payload.job.extractPath)
        SolverHandler.logger.info("Building problem from extract folder")
        val builder = ProblemBuilder(extractDir.absolutePath)
        val problem = builder.build()

        SolverHandler.logger.info("Starting solver")
        val solver = PlaylistSolverFactory().getSolver(problem)
        if (payload.graphqlEndpointURL != null && payload.accessToken != null) {
          val listener =
            UpdateJobSolverEventListener(solver, payload.job, payload.graphqlEndpointURL, payload.accessToken)
          solver.addEventListener(listener)
          if (solver is AbstractSolver) {
            solver.addPhaseLifecycleListener(listener)
          }
        } else {
          SolverHandler.logger.warn("Not adding update listener bc endpoint or access token not provided in payload")
        }


        val solution = solver.solve(problem)

        SolverHandler.logger.info("Finished solving in ${solver.timeMillisSpent} with best score of ${solver.bestScore.toShortString()}")

        val status = SolverStatus.fromSolution(solver, solution)

        call.respond(status)

      } catch (e: Exception) {
        SolverHandler.logger.error("An error occurred", e)
        call.respondText("An error occurred: ${e.message}", status = HttpStatusCode.InternalServerError)
      }

      call.respondText("Route registered correctly", status = HttpStatusCode.Created)
    }
  }
}