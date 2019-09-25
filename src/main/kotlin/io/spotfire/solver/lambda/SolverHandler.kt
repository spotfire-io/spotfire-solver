package io.spotfire.solver.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.spotfire.solver.domain.OptimizationJob
import io.spotfire.solver.solver.ConstraintViolationReporter
import io.spotfire.solver.solver.PlaylistSolverFactory
import io.spotfire.solver.solver.ProblemBuilder
import io.spotfire.solver.solver.SolverStatus
import io.spotfire.solver.solver.UpdateJobSolverEventListener
import okhttp3.OkHttpClient
import okhttp3.Request
import org.codehaus.plexus.archiver.tar.TarGZipUnArchiver
import org.codehaus.plexus.logging.console.ConsoleLoggerManager
import org.optaplanner.core.impl.solver.AbstractSolver
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

class SolverHandler : RequestHandler<Map<String, Any>, Map<String, Any>> {
  companion object {
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val jobAdapter = moshi.adapter(OptimizationJob::class.java)
    val payloadAdapter = moshi.adapter(SolverHandlerPayload::class.java)
    val solverStatusAdapter = moshi.adapter(SolverStatus::class.java)
    val httpClient = OkHttpClient()
    val uaLogger = ConsoleLoggerManager("info")
    val extractPathRegex = """(.+)/(.+)\.tar.gz""".toRegex()
    val logger: Logger = LoggerFactory.getLogger(SolverHandler::class.java)
  }

  private fun extractToDir(srcFile: File, destDir: File) {
    val ua = TarGZipUnArchiver(srcFile)
    val logger = uaLogger.getLoggerForComponent(srcFile.absolutePath, "extractor")
    ua.enableLogging(logger)
    ua.destDirectory = destDir
    ua.extract()
  }

  private fun getExtractHashFromPath(path: String): String? {
    val matchResult = extractPathRegex.matchEntire(path)
    return matchResult?.groupValues?.get(2)
  }

  private fun downloadAndExtractFile(path: String): File {
    logger.info("Extracting file from $path")
    getExtractHashFromPath(path)!!.let { extractHash ->
      val extractDir = File("/tmp/spotfire-extracts/$extractHash")
      if (!extractDir.exists() || extractDir.list().isEmpty()) {
        extractDir.mkdirs()
        var attempts = 0
        while(attempts++ < 5) {
          val extractReq = Request.Builder().url(path).build()
          val extractResp = httpClient.newCall(extractReq).execute()

          if(extractResp.code() != 200) {
            val pauseMs = 500*Math.pow(2.toDouble(), attempts.toDouble()).toLong()
            logger.warn("Received status code ${extractResp.code()} when attempting to fetch $path, attempt #${attempts}. Pausing ${pauseMs} before retrying.")
            Thread.sleep(pauseMs)
          } else {
            val tmpFile = createTempFile(suffix = ".tar.gz")
            extractResp.body()?.byteStream()?.use { bodyStream ->
              tmpFile.outputStream().use { tmpFileStream ->
                bodyStream.copyTo(tmpFileStream)
              }
              logger.info("Wrote extract file to $tmpFile")
              logger.info("Extracted file to $extractDir")
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

  override fun handleRequest(input: Map<String, Any>, context: Context): Map<String, Any> {
    try {
      val jobMap = input.get("job") as HashMap<String, String>
      logger.info("received input for request foo $input")
      jobMap["id"]?.let { jobId ->
        jobMap["extractPath"]?.let { extractPath ->
          val accessToken = input["accessToken"] as String?
          val graphqlEndpointURL = input["graphqlEndpointURL"] as String?
          val job = OptimizationJob(id = jobId, extractPath = extractPath)

          val extractDir = downloadAndExtractFile(extractPath)
          logger.info("Building problem from extract folder")
          val builder = ProblemBuilder(extractDir.absolutePath)
          val problem = builder.build()

          logger.info("Starting solver")
          val solver = PlaylistSolverFactory().getSolver(problem)
          if (graphqlEndpointURL != null && accessToken != null) {
            val listener = UpdateJobSolverEventListener(solver, job, graphqlEndpointURL, accessToken)
            solver.addEventListener(listener)
            if (solver is AbstractSolver) {
              solver.addPhaseLifecycleListener(listener)
            }
          } else {
            logger.warn("Not adding update listener bc endpoint or access token not provided in payload")
          }


          val solution = solver.solve(problem)

          logger.info("Finished solving in ${solver.timeMillisSpent} with best score of ${solver.bestScore.toShortString()}")

          val status = SolverStatus.fromSolution(solver, solution)

          return mapOf(
            "statusCode" to 200,
            "body" to solverStatusAdapter.toJson(status)
          )
        } ?: run {
          throw Exception("Extract path not provided for job input")
        }
      } ?: run {
        throw Exception("Job ID not provided for job input")
      }
    } catch(e: Exception) {
      logger.error("An error occurred", e)
      return mapOf(
        "statusCode" to 500,
        "body" to "An error occurred: ${e.message}"
      )
    }
  }
}