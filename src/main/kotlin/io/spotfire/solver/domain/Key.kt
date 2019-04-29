package io.spotfire.solver.domain
import com.squareup.moshi.Json

data class Key(
    @Json(name = "camelot_code")
    val camelotCode: String?,
    @Json(name = "camelot_position")
    val camelotPosition: Int?,
    @Json(name = "id")
    val id: String?,
    @Json(name = "label")
    val label: String,
    @Json(name = "mode")
    val mode: String?,
    @Json(name = "root_note")
    val rootNote: RootNote?
)