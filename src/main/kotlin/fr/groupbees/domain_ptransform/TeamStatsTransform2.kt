package fr.groupbees.domain_ptransform

import fr.groupbees.domain.TeamFifaRanking
import fr.groupbees.domain.TeamPlayerStats
import fr.groupbees.domain.TeamPlayerStatsRaw
import fr.groupbees.midgard.filter
import fr.groupbees.midgard.map
import fr.groupbees.midgard.mapFn
import fr.groupbees.midgard.mapFnWithContext
import org.apache.beam.sdk.transforms.DoFn
import org.apache.beam.sdk.transforms.GroupByKey
import org.apache.beam.sdk.transforms.PTransform
import org.apache.beam.sdk.transforms.WithKeys
import org.apache.beam.sdk.values.PCollection
import org.apache.beam.sdk.values.PCollectionView
import org.apache.beam.sdk.values.TypeDescriptors
import org.slf4j.LoggerFactory

class TeamStatsTransform2(private val fifaRankingSideInput: PCollectionView<List<TeamFifaRanking>>) :
    PTransform<PCollection<TeamPlayerStatsRaw>, PCollection<TeamPlayerStats>>() {

    override fun expand(input: PCollection<TeamPlayerStatsRaw>): PCollection<TeamPlayerStats> {
        return input.map("Validate") { it.validateFields() }
            .filter("Players with kit sponsor") { it.nationalTeamKitSponsor != "" }
            .filter("Players with position") { it.position != "" }
            .apply("Add key on team name", WithKeys.of<String, TeamPlayerStatsRaw> { it.nationality }
                .withKeyType(TypeDescriptors.strings()))
            .apply("Group by team name", GroupByKey.create())
            .mapFn(
                name = "Compute team player stats",
                startBundleAction = { LOGGER.info("Start bundle action") },
                transform = { TeamPlayerStats.computeTeamPlayerStats(it.key, it.value) }
            )
            .mapFnWithContext(
                name = "Add Fifa ranking to team",
                setupAction = { LOGGER.info("Setup action") },
                sideInputs = listOf(fifaRankingSideInput),
                transform = { addFifaRankingToTeam(it, fifaRankingSideInput) }
            )
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TeamStatsTransform2::class.java)

        private fun addFifaRankingToTeam(
            context: DoFn<TeamPlayerStats, TeamPlayerStats>.ProcessContext,
            fifaRankingSideInput: PCollectionView<List<TeamFifaRanking>>
        ): TeamPlayerStats {
            val teamFifaRankingList: List<TeamFifaRanking> = context.sideInput(fifaRankingSideInput)
            val teamPlayerStats: TeamPlayerStats = context.element()

            return teamPlayerStats.addFifaRankingToTeamStats(teamFifaRankingList)
        }
    }
}