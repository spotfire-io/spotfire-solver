package io.spotfire.solver.solver

import io.spotfire.solver.domain.PlaylistSolution
import io.spotfire.solver.domain.RestPlaylistTrack
import org.optaplanner.core.api.solver.Solver
import org.optaplanner.core.api.solver.SolverFactory
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType
import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig
import org.optaplanner.core.config.heuristic.selector.move.generic.SwapMoveSelectorConfig
import org.optaplanner.core.config.heuristic.selector.move.generic.chained.TailChainSwapMoveSelectorConfig
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig
import org.optaplanner.core.config.localsearch.decider.acceptor.AcceptorConfig
import org.optaplanner.core.config.localsearch.decider.forager.LocalSearchForagerConfig
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel
import org.optaplanner.core.config.solver.EnvironmentMode
import org.optaplanner.core.config.solver.termination.TerminationConfig

class PlaylistSolverFactory{
  private val drlResourcePathRoot = "io/spotfire/solver/rules"
  private val drlFilenames = listOf(
    "rules.drl"
  )
  private val drlResourcePaths = drlFilenames.map { f -> "$drlResourcePathRoot/$f" }.toTypedArray()

  private val constructionHeuristicPhaseConfig = ConstructionHeuristicPhaseConfig()
    .withConstructionHeuristicType(ConstructionHeuristicType.CHEAPEST_INSERTION)

  private fun getLocalSearchPhaseConfig(problem: PlaylistSolution): LocalSearchPhaseConfig {
    val foragerConfig = LocalSearchForagerConfig()
    foragerConfig.acceptedCountLimit = Math.max(Math.sqrt(problem.restTracks!!.size.toDouble()).toInt(), 4)

    val config = LocalSearchPhaseConfig()
      .withMoveSelectorConfig(
        UnionMoveSelectorConfig(listOf(
          TailChainSwapMoveSelectorConfig(),
          // ChangeMoveSelectorConfig(),
          SwapMoveSelectorConfig()
        ))
          .withCacheType(SelectionCacheType.STEP)
        // .withSelectionOrder(SelectionOrder.PROBABILISTIC)
      )
      .withAcceptorConfig(AcceptorConfig()
        .withSimulatedAnnealingStartingTemperature("[0]hard/[8/8/8]soft")
        // .withLateAcceptanceSize(400)
        .withEntityTabuRatio(0.05)
        // .withValueTabuRatio(0.05)
      )
      .withForagerConfig(foragerConfig)

    config.terminationConfig = TerminationConfig()
      .withUnimprovedStepCountLimit(1000)

    return config
  }

  fun getSolver(problem: PlaylistSolution): Solver<PlaylistSolution> {
    val factory = SolverFactory.createEmpty<PlaylistSolution>()
    val config = factory.solverConfig

    config
      .withMoveThreadCount("Math.max(availableProcessorCount - 2, 2)")
      .withSolutionClass(PlaylistSolution::class.java)
       .withEnvironmentMode(EnvironmentMode.FULL_ASSERT)
      .withEntityClasses(RestPlaylistTrack::class.java)
      .withScoreDirectorFactory(ScoreDirectorFactoryConfig()
        .withScoreDrls(*drlResourcePaths)
        .withInitializingScoreTrend(InitializingScoreTrendLevel.ONLY_DOWN.name))
      .withPhases(
        constructionHeuristicPhaseConfig,
        getLocalSearchPhaseConfig(problem)
      )
      .withTerminationConfig(TerminationConfig()
        .withMinutesSpentLimit(10)
        .withUnimprovedMinutesSpentLimit(1)
      )

    return factory.buildSolver()
  }
}