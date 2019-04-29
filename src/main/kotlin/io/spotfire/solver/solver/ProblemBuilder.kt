package io.spotfire.solver.solver

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.spotfire.solver.domain.Album
import io.spotfire.solver.domain.Artist
import io.spotfire.solver.domain.FirstPlaylistTrack
import io.spotfire.solver.domain.Genre
import io.spotfire.solver.domain.Key
import io.spotfire.solver.domain.OriginalPlaylistTrack
import io.spotfire.solver.domain.RestPlaylistTrack
import io.spotfire.solver.domain.PlaylistSolution
import java.io.File
import kotlin.streams.toList

class ProblemBuilder(
  val extractDirPath: String,
  val readFromClasspath: Boolean = false
) {
  val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

  fun <T>getValuesFromFile(filename: String, clazz: Class<T>): List<T> {
    val adapter = moshi.adapter<T>(clazz)
    val path = "$extractDirPath/$filename"
    if(readFromClasspath) {
      this.javaClass.getResourceAsStream(path)
    } else {
      File(path).inputStream()
    }.bufferedReader().use { br ->
        return br
          .lines()
          .map<T>(adapter::fromJson)
          .filter { it != null }
          .toList()
      }
  }

  val artists = getValuesFromFile("artists.jsonl", Artist::class.java)
  private val artistLookup = artists.associate { a -> a.artistId to a }

  private val albums = getValuesFromFile("albums.jsonl", Album::class.java)
    .map { album ->
      album.artists = album.artists?.map { artist -> artistLookup[artist.artistId]!! }
      album
    }
  private val albumLookup = albums.associate { a -> a.albumId to a }

  private val keys = getValuesFromFile("keys.jsonl", Key::class.java)
  private val keyLookup = keys.associate { k -> k.label to k }

  private val tracks = getValuesFromFile("playlistTracks.jsonl", OriginalPlaylistTrack::class.java)
    .filterNot { opt -> opt.track == null }
    .distinctBy { opt -> opt.track!!.trackId }
    .map { pt ->
      val track = pt.track!!
      track.features.key = keyLookup[track.features.key.label]!!
      track.album = albumLookup[track.album.albumId]!!
      track.artists = track.artists.map { a -> artistLookup[a.artistId]!! }
      track
    }

  private val genres = listOf(
    albums.flatMap { a -> a.genres!!.map { g -> g.name } },
    artists.flatMap { a -> a.genres!!.map { g -> g.name } }
  ).flatten().distinct().map { name -> Genre(name) }
  private val genreLookup = genres.associate { g -> g.name to g }

  private val lookupGenre = { g: Genre -> genreLookup[g.name] ?: error("Could not find genre ${g.name}") }

  fun build(): PlaylistSolution {
    val firstTrack = FirstPlaylistTrack(tracks.first())
    val restTracks = tracks.drop(1)
    val restPlaylistTracks = restTracks.map { track -> RestPlaylistTrack(track) }
    // restPlaylistTracks[0].previousTrack = firstTrack
    // restPlaylistTracks.drop(1).zip(restPlaylistTracks).map {
    //   val (currentTrack, previousTrack) = it
    //   currentTrack.previousTrack = previousTrack
    // }

    return PlaylistSolution(
      firstPlaylistTrack = firstTrack,
      firstPlaylistTrackRange = listOf(firstTrack),
      restPlaylistTrackRange = restPlaylistTracks,
      restTracks = restTracks,
      keys = keys,
      artists = artists.map { a ->
        a.genres = a.genres!!.map(lookupGenre)
        a
      },
      albums = albums.map { a ->
        a.genres = a.genres!!.map(lookupGenre)
        a
      },
      genres = genres
    )
  }

}