package io.spotfire.solver.solver

import io.spotfire.solver.domain.RestPlaylistTrack
import org.optaplanner.core.impl.domain.variable.listener.VariableListener
import org.optaplanner.core.impl.score.director.ScoreDirector

class PreviousTrackUpdatedListener : VariableListener<RestPlaylistTrack> {
  override fun afterEntityAdded(scoreDirector: ScoreDirector<*>, playlistTrack: RestPlaylistTrack) {
    update(scoreDirector, playlistTrack)
  }


  override fun afterVariableChanged(scoreDirector: ScoreDirector<*>, playlistTrack: RestPlaylistTrack) {
    update(scoreDirector, playlistTrack)
  }

  private fun update(scoreDirector: ScoreDirector<*>, playlistTrack: RestPlaylistTrack) {
    updateKeyDistance(scoreDirector, playlistTrack)
    // updateExponentialDecayKeyDistance(scoreDirector, playlistTrack)
  }

  private fun updateKeyDistance(scoreDirector: ScoreDirector<*>, playlistTrack: RestPlaylistTrack) {
    var distance: Int? = null
    playlistTrack.track?.features?.key?.let { thisKey ->
      playlistTrack.previousTrack?.track?.features?.key?.let { previousKey ->
        val noteDistance = Math.abs(
          thisKey.camelotPosition!! - previousKey.camelotPosition!!
        )
        val modeDistance = if (thisKey != previousKey) 1 else 0
        distance = (if (noteDistance < 6) noteDistance else noteDistance - (noteDistance % 6)) + modeDistance
      }
    }
    if(playlistTrack.keyDistance != distance) {
      scoreDirector.beforeVariableChanged(playlistTrack, "keyDistance")
      playlistTrack.keyDistance = distance
      scoreDirector.afterVariableChanged(playlistTrack, "keyDistance")
    }
  }

  override fun beforeEntityRemoved(scoreDirector: ScoreDirector<*>?, playlistTrack: RestPlaylistTrack?) {
    // do nothing
  }

  override fun beforeEntityAdded(scoreDirector: ScoreDirector<*>?, playlistTrack: RestPlaylistTrack?) {
    // do nothing
  }

  override fun afterEntityRemoved(scoreDirector: ScoreDirector<*>?, playlistTrack: RestPlaylistTrack?) {
    // do nothing
  }

  override fun beforeVariableChanged(scoreDirector: ScoreDirector<*>?, playlistTrack: RestPlaylistTrack?) {
    // do nothing
  }



  // private fun updateExponentialDecayKeyDistance(scoreDirector: ScoreDirector<*>, playlistTrack: RestPlaylistTrack) {
  //   var distance = 0.0
  //   var cursor: RestPlaylistTrack = playlistTrack
  //   for(b in 1..8) {
  //     val kd = cursor.keyDistance
  //     if(kd != null) {
  //       distance += kd * (1/b)
  //     } else {
  //       break
  //     }
  //     val previousTrack = cursor.previousTrack
  //     if(previousTrack is RestPlaylistTrack) {
  //       cursor = previousTrack
  //     } else {
  //       break
  //     }
  //   }
  //   if(playlistTrack.exponentialDecayKeyDistance != distance) {
  //     scoreDirector.beforeVariableChanged(playlistTrack, "exponentialDecayKeyDistance")
  //     playlistTrack.exponentialDecayKeyDistance = distance
  //     scoreDirector.afterVariableChanged(playlistTrack, "exponentialDecayKeyDistance")
  //   }
  // }


}