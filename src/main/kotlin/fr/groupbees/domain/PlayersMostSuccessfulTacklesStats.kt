package fr.groupbees.domain

import kotlinx.serialization.Serializable

@Serializable
data class PlayersMostSuccessfulTacklesStats(
    val players: List<Player>,
    val successfulTackles: String
) : java.io.Serializable