package fr.groupbees.domain

import fr.groupbees.domain.exception.TeamStatsRawValidatorException
import kotlinx.serialization.Serializable
import java.util.*


@Serializable
data class TeamPlayerStatsRaw(
    val nationality: String,
    val fifaRanking: Int,
    val nationalTeamKitSponsor: String,
    val position: String,
    val nationalTeamJerseyNumber: Int?,
    val playerDob: String,
    val club: String,
    val playerName: String,
    val appearances: String,
    val goalsScored: String,
    val assistsProvided: String,
    val dribblesPerNinety: String,
    val interceptionsPerNinety: String,
    val tacklesPerNinety: String,
    val totalDuelsWonPerNinety: String,
    val savePercentage: String,
    val cleanSheets: String,
    val brandSponsorAndUsed: String
) : java.io.Serializable {

    fun validateFields(): TeamPlayerStatsRaw {
        if (Objects.isNull(nationality) || nationality == "") {
            throw TeamStatsRawValidatorException(PLAYER_NATIONALITY_EMPTY_ERROR_MESSAGE)
        }
        return this
    }

    companion object {
        const val PLAYER_NATIONALITY_EMPTY_ERROR_MESSAGE = "Player nationality name cannot be null or empty"
    }
}