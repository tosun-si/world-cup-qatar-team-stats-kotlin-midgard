package fr.groupbees.domain

import kotlinx.serialization.Serializable

@Serializable
data class PlayersMostDuelsWonStats(
    val players: List<Player>,
    val duels: String,
) : java.io.Serializable