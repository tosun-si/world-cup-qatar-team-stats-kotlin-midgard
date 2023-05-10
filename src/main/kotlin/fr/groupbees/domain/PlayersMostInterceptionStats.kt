package fr.groupbees.domain

import kotlinx.serialization.Serializable

@Serializable
data class PlayersMostInterceptionStats(
    val players: List<Player>,
    val interceptions: String,
) : java.io.Serializable