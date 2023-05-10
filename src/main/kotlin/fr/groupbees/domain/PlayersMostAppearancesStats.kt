package fr.groupbees.domain

import kotlinx.serialization.Serializable

@Serializable
data class PlayersMostAppearancesStats(
    val players: List<Player>,
    val appearances: String,
) : java.io.Serializable