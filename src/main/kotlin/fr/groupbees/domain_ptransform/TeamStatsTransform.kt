package fr.groupbees.domain_ptransform

import fr.groupbees.domain.TeamFifaRanking
import fr.groupbees.domain.TeamPlayerStats
import fr.groupbees.domain.TeamPlayerStatsRaw
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

class TeamStatsTransform(private val fifaRankingSideInput: PCollectionView<List<TeamFifaRanking>>) :
    PTransform<PCollection<TeamPlayerStatsRaw>, PCollection<TeamPlayerStats>>() {

    override fun expand(input: PCollection<TeamPlayerStatsRaw>): PCollection<TeamPlayerStats> {
        return input
            .map("Validate fields") { it.validateFields() }
            .apply(WithKeys.of<String, TeamPlayerStatsRaw> { x -> x.nationality }
                .withKeyType(TypeDescriptors.strings()))
            .apply(GroupByKey.create())
            .mapFn(
                name = "Compute Team Player Stats",
                startBundleAction = { LOGGER.info("####################Start bundle compute stats") },
                transform = { TeamPlayerStats.computeTeamPlayerStats(it.key, it.value) }
            )
            .mapFnWithContext(
                name = "Add team Fifa ranking",
                setupAction = { LOGGER.info("####################Start add Fifa ranking") },
                sideInputs = listOf(fifaRankingSideInput),
                transform = { addFifaRankingToTeam(it) }
            )
    }

    private fun addFifaRankingToTeam(
        context: DoFn<TeamPlayerStats, TeamPlayerStats>.ProcessContext
    ): TeamPlayerStats {
        val teamFifaRankingList: List<TeamFifaRanking> = context.sideInput(fifaRankingSideInput)
        val teamPlayerStats: TeamPlayerStats = context.element()

        return teamPlayerStats.addFifaRankingToTeamStats(teamFifaRankingList)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TeamStatsTransform::class.java)
    }
}