package fr.groupbees.domain

import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val playerName: String,
    val playerDob: String,
    val position: String,
    val club: String,
    val brandSponsorAndUsed: String,
    val appearances: String
) : java.io.Serializable