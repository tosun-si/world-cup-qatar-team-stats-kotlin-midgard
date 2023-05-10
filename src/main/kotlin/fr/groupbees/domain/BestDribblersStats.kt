package fr.groupbees.domain

import kotlinx.serialization.Serializable

@Serializable
data class BestDribblersStats(
    val players: List<Player>,
    val dribbles: String,
) : java.io.Serializable