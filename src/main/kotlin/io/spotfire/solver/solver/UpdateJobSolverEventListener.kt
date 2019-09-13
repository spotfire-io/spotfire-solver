package io.spotfire.solver.solver

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.spotfire.solver.domain.PlaylistSolution
import org.optaplanner.core.api.solver.Solver
import org.optaplanner.core.api.solver.event.BestSolutionChangedEvent
import org.optaplanner.core.api.solver.event.SolverEventListener
import org.optaplanner.core.impl.phase.event.PhaseLifecycleListener
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope
import org.optaplanner.core.impl.phase.scope.AbstractStepScope
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope
import io.github.cdimascio.dotenv.dotenv
import io.spotfire.solver.domain.OptimizationJob
import io.spotfire.solver.domain.PlaylistSnapshot
import io.spotfire.solver.graphql.GraphQLRequest
import io.spotfire.solver.lambda.SolverHandler
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class UpdateJobSolverEventListener(
  val solver: Solver<PlaylistSolution>,
  val optimizationJob: OptimizationJob,
  val endpoint: String,
  val accessToken: String
) : SolverEventListener<PlaylistSolution>, PhaseLifecycleListener<PlaylistSolution> {
  companion object {
    // val dotenv = dotenv()
    val httpClient = OkHttpClient.Builder().build()
    val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val gqlRequestAdapter = moshi.adapter(GraphQLRequest::class.java)
    val logger: Logger = LoggerFactory.getLogger(SolverEventListener::class.java)
  }

  override fun phaseStarted(phaseScope: AbstractPhaseScope<PlaylistSolution>?) {
    // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun solvingStarted(solverScope: DefaultSolverScope<PlaylistSolution>?) {
    // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun stepStarted(stepScope: AbstractStepScope<PlaylistSolution>?) {
    // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun solvingEnded(solverScope: DefaultSolverScope<PlaylistSolution>) {
    val solution = solverScope.bestSolution
    val status = SolverStatus.fromSolution(solver, solution)
    sendStatus(status, solution)
    completeJob(solution)
  }

  override fun phaseEnded(phaseScope: AbstractPhaseScope<PlaylistSolution>?) {
    // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun stepEnded(stepScope: AbstractStepScope<PlaylistSolution>?) {
    // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun bestSolutionChanged(event: BestSolutionChangedEvent<PlaylistSolution>) {
    val status = SolverStatus.fromSolution(solver, event.newBestSolution)
    sendStatus(status, event.newBestSolution)
  }

  fun completeJob(solution: PlaylistSolution) {
    val trackIds = mutableListOf<String?>(solution.firstPlaylistTrack!!.trackId!!)
    val restTracks = solution.restPlaylistTrackRange!!
    while(!trackIds.contains(null)) {
      val lastId = trackIds.last()
      val next = restTracks.find { t -> t.previousTrack?.track?.trackId == lastId}
      trackIds.add(next?.trackId)
    }

    val gqlRequest = GraphQLRequest(
      """
        mutation {
          completePlaylistOptimization(
            jobId: "${optimizationJob.id}",
            trackIds: ["${trackIds.filterNotNull().joinToString("""","""")}"]
          ) {
            id
          }
        }
      """.trimIndent()
    )

    executeGqlRequest(gqlRequest)
  }

  fun sendStatus(status: SolverStatus, solution: PlaylistSolution) {
    val constraintViolationJsons = status.constraintViolations.map { cv ->
      """{
        constraint_name: "${cv.constraintName}",
        violation_count: ${cv.violationCount},
        score_impact: "${cv.scoreImpact}"
      }"""
    }

    val gqlRequest = GraphQLRequest(
      """
        mutation {
          createSolverStatusUpdate(
            data: {
              job: { connect: { id: "${optimizationJob.id}" } }
              best_score: "${status.bestScore}"
              time_millis_spent: ${status.timeMillisSpent}
              constraint_violations: {
                create: [
                  ${constraintViolationJsons.joinToString(",")}
                ]
              }
            }
          ) {
            id
          }
        }
      """.trimIndent()
    )

    executeGqlRequest(gqlRequest)
  }

  private fun executeGqlRequest(gqlRequest: GraphQLRequest) {
    val body = RequestBody.create(
      MediaType.get("application/json"),
      gqlRequestAdapter.toJson(gqlRequest)
    )

    val req = Request.Builder()
      .url(endpoint)
      .header("Authorization", "Bearer $accessToken")
      .post(body)
      .build()

    val resp = httpClient.newCall(req).execute()
    if (!resp.isSuccessful) {
      logger.error("Received error: ${resp.code()}")
    }
  }
}