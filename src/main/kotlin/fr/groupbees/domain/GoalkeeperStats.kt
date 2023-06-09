package fr.groupbees.domain

import kotlinx.serialization.Serializable

@Serializable
data class GoalkeeperStats(
    val playerName: String,
    val appearances: String,
    val savePercentage: String,
    val cleanSheets: String,
    val club: String
) : java.io.Serializable