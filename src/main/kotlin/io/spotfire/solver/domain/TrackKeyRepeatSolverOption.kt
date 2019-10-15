package io.spotfire.solver.domain

class TrackKeyRepeatSolverOption(val windowSize: Int = 5): SolverOption {

  override val id: String
    get() = "Track key repeat solver option"

}