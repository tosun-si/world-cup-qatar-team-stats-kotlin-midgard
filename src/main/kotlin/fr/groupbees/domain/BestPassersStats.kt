package fr.groupbees.domain

import kotlinx.serialization.Serializable

@Serializable
data class BestPassersStats(
    val players: List<Player>,
    val goalAssists: String,
) : java.io.Serializable