package fr.groupbees.domain

import kotlinx.serialization.Serializable

@Serializable
data class TeamFifaRanking(
    val teamName: String,
    val fifaRanking: Int
) : java.io.Serializable