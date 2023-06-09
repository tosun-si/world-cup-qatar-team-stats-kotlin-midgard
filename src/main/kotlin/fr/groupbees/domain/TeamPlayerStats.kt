package fr.groupbees.domain

import fr.groupbees.domain.exception.TeamFifaRankingUnknownException
import kotlinx.serialization.Serializable

@Serializable
data class TeamPlayerStats(
    val teamName: String,
    val teamTotalGoals: Int,
    val fifaRanking: Int = 0,
    val nationalTeamKitSponsor: String,
    val topScorers: TopScorersStats,
    val bestPassers: BestPassersStats,
    val bestDribblers: BestDribblersStats,
    val goalKeeper: GoalkeeperStats,
    val playersMostAppearances: PlayersMostAppearancesStats,
    val playersMostDuelsWon: PlayersMostDuelsWonStats,
    val playersMostInterception: PlayersMostInterceptionStats,
    val playersMostSuccessfulTackles: PlayersMostSuccessfulTacklesStats
) : java.io.Serializable {

    fun addFifaRankingToTeamStats(teamFifaRankingList: List<TeamFifaRanking>): TeamPlayerStats {
        val currentTeamFifaRanking = teamFifaRankingList
            .find { it.teamName == teamName }
            ?: throw TeamFifaRankingUnknownException("Team ranking unknown for team $teamName")

        return this.copy(
            fifaRanking = currentTeamFifaRanking.fifaRanking,
        )
    }

    companion object {

        fun computeTeamPlayerStats(
            nationality: String,
            teamPlayersStatsRaw: Iterable<TeamPlayerStatsRaw>
        ): TeamPlayerStats {
            val topScorersValue = teamPlayersStatsRaw
                .filter { isInteger(it.goalsScored) }
                .maxBy { it.goalsScored.toInt() }
                .goalsScored

            val topScorers = teamPlayersStatsRaw
                .filter { it.goalsScored == topScorersValue }
                .filter { it.goalsScored != "0" }
                .map { toPlayer(it) }

            val bestPassersValue = teamPlayersStatsRaw
                .filter { isInteger(it.assistsProvided) }
                .maxBy { it.assistsProvided.toInt() }
                .assistsProvided

            val bestPassers = teamPlayersStatsRaw
                .filter { it.assistsProvided == bestPassersValue }
                .filter { it.assistsProvided != "0" }
                .map { toPlayer(it) }

            val bestDribblersValue = teamPlayersStatsRaw
                .filter { isFloat(it.dribblesPerNinety) }
                .maxBy { it.dribblesPerNinety.toFloat() }
                .dribblesPerNinety

            val bestDribblers = teamPlayersStatsRaw
                .filter { it.dribblesPerNinety == bestDribblersValue }
                .map { toPlayer(it) }

            val playerMostAppearancesValue = teamPlayersStatsRaw
                .filter { isInteger(it.appearances) }
                .maxBy { it.appearances.toInt() }
                .appearances

            val playerWithMostAppearance = teamPlayersStatsRaw
                .filter { it.appearances == playerMostAppearancesValue }
                .map { toPlayer(it) }

            val playersMostDuelsWonValue = teamPlayersStatsRaw
                .filter { isFloat(it.totalDuelsWonPerNinety) }
                .maxBy { it.totalDuelsWonPerNinety.toFloat() }
                .totalDuelsWonPerNinety

            val playersMostDuelsWon = teamPlayersStatsRaw
                .filter { it.totalDuelsWonPerNinety == playersMostDuelsWonValue }
                .map { toPlayer(it) }

            val playersMostInterceptionsValue = teamPlayersStatsRaw
                .filter { isFloat(it.interceptionsPerNinety) }
                .maxBy { it.interceptionsPerNinety.toFloat() }
                .interceptionsPerNinety

            val playersMostInterception = teamPlayersStatsRaw
                .filter { it.interceptionsPerNinety == playersMostInterceptionsValue }
                .map { toPlayer(it) }

            val playersMostSuccessfulTacklesValue = teamPlayersStatsRaw
                .filter { isFloat(it.tacklesPerNinety) }
                .maxBy { it.tacklesPerNinety.toFloat() }
                .tacklesPerNinety

            val playersMostSuccessfulTackles = teamPlayersStatsRaw
                .filter { it.tacklesPerNinety == playersMostSuccessfulTacklesValue }
                .map { toPlayer(it) }

            val currentGoalKeeperStats = teamPlayersStatsRaw
                .find { it.savePercentage != "-" }!!

            val teamTotalGoals = teamPlayersStatsRaw
                .map { it.goalsScored }
                .filter(::isInteger)
                .sumOf { it.toInt() }

            val topScorersStats = TopScorersStats(
                players = topScorers,
                goals = topScorersValue
            )

            val bestPassersStats = BestPassersStats(
                players = bestPassers,
                goalAssists = bestPassersValue
            )

            val bestDribblersStats = BestDribblersStats(
                players = bestDribblers,
                dribbles = bestDribblersValue
            )

            val playersMostAppearancesStats = PlayersMostAppearancesStats(
                players = playerWithMostAppearance,
                appearances = playerMostAppearancesValue
            )

            val playersMostDuelsWonStats = PlayersMostDuelsWonStats(
                players = playersMostDuelsWon,
                duels = playersMostDuelsWonValue
            )

            val playersMostInterceptionStats = PlayersMostInterceptionStats(
                players = playersMostInterception,
                interceptions = playersMostInterceptionsValue
            )

            val playersMostSuccessfulTacklesStats = PlayersMostSuccessfulTacklesStats(
                players = playersMostSuccessfulTackles,
                successfulTackles = playersMostSuccessfulTacklesValue
            )

            val goalKeeperStats = GoalkeeperStats(
                playerName = currentGoalKeeperStats.playerName,
                club = currentGoalKeeperStats.club,
                appearances = currentGoalKeeperStats.appearances,
                savePercentage = currentGoalKeeperStats.savePercentage,
                cleanSheets = currentGoalKeeperStats.cleanSheets
            )

            val currentTeam = teamPlayersStatsRaw.first()

            return TeamPlayerStats(
                teamName = nationality,
                teamTotalGoals = teamTotalGoals,
                nationalTeamKitSponsor = currentTeam.nationalTeamKitSponsor,
                topScorers = topScorersStats,
                bestPassers = bestPassersStats,
                bestDribblers = bestDribblersStats,
                playersMostAppearances = playersMostAppearancesStats,
                playersMostDuelsWon = playersMostDuelsWonStats,
                playersMostInterception = playersMostInterceptionStats,
                playersMostSuccessfulTackles = playersMostSuccessfulTacklesStats,
                goalKeeper = goalKeeperStats
            )
        }

        private fun toPlayer(teamPlayerStats: TeamPlayerStatsRaw): Player {
            return Player(
                playerName = teamPlayerStats.playerName,
                playerDob = teamPlayerStats.playerDob,
                position = teamPlayerStats.position,
                club = teamPlayerStats.club,
                brandSponsorAndUsed = teamPlayerStats.brandSponsorAndUsed,
                appearances = teamPlayerStats.appearances,
            )
        }

        private fun isInteger(str: String): Boolean {
            return str.toIntOrNull() != null
        }

        private fun isFloat(str: String): Boolean {
            return str.toFloatOrNull() != null
        }
    }
}
