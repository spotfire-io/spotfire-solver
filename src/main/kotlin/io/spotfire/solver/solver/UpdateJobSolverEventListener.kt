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
import io.spotfire.solver.graphql.GraphQLRequest
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

class UpdateJobSolverEventListener(
  val solver: Solver<PlaylistSolution>,
  val optimizationJob: OptimizationJob
) : SolverEventListener<PlaylistSolution>, PhaseLifecycleListener<PlaylistSolution> {
  companion object {
    // val dotenv = dotenv()
    val httpClient = OkHttpClient.Builder().build()
    val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val gqlRequestAdapter = moshi.adapter(GraphQLRequest::class.java)
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

  override fun solvingEnded(solverScope: DefaultSolverScope<PlaylistSolution>?) {
    // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun phaseEnded(phaseScope: AbstractPhaseScope<PlaylistSolution>?) {
    // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun stepEnded(stepScope: AbstractStepScope<PlaylistSolution>?) {
    // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun bestSolutionChanged(event: BestSolutionChangedEvent<PlaylistSolution>) {
    val status = SolverStatus.fromSolution(solver, event.newBestSolution)
  }

  fun sendStatus(status: SolverStatus, solution: PlaylistSolution) {
    val constraintViolationJsons = status.constraintViolations.map { cv ->
      """"{
        constraint_name: "${cv.constraintName}",
        violation_count: ${cv.violationCount},
        score_impact: ${cv.scoreImpact}
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

    val body = RequestBody.create(
      okhttp3.MediaType.get("application/json"),
      gqlRequestAdapter.toJson(gqlRequest)
    )

    val req = Request.Builder().post(body)


  }

  // fun ReceiveChannel<SolverStatus>.throttle(
  //   wait: Long = 200,
  //   context: CoroutineContext
  // ): ReceiveChannel<SolverStatus> = produce<SolverStatus> {
  //   var nextTime = 0L
  //   consumeEach {
  //     val curTime = System.currentTimeMillis()
  //     if (curTime >= nextTime) {
  //       nextTime = curTime + wait
  //       send(it)
  //     }
  //   }
}