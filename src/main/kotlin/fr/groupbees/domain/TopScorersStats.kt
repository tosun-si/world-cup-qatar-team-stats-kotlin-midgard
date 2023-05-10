package fr.groupbees.domain

import kotlinx.serialization.Serializable

@Serializable
data class TopScorersStats(
    val players: List<Player>,
    val goals: String,
) : java.io.Serializable