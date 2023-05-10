package fr.groupbees.application

import com.google.api.services.bigquery.model.TableRow
import fr.groupbees.domain.*
import fr.groupbees.domain_ptransform.JsonUtil
import fr.groupbees.midgard.map
import fr.groupbees.midgard.mapFn
import fr.groupbees.midgard.mapFnWithContext
import org.apache.beam.sdk.Pipeline
import org.apache.beam.sdk.io.TextIO
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO
import org.apache.beam.sdk.options.PipelineOptionsFactory
import org.apache.beam.sdk.transforms.DoFn
import org.apache.beam.sdk.transforms.GroupByKey
import org.apache.beam.sdk.transforms.View
import org.apache.beam.sdk.transforms.WithKeys
import org.apache.beam.sdk.values.PCollectionView
import org.apache.beam.sdk.values.TypeDescriptors
import org.joda.time.Instant
import org.slf4j.LoggerFactory

object WorldCupStatsApp {
    private val LOGGER = LoggerFactory.getLogger(WorldCupStatsApp::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        val options = PipelineOptionsFactory
            .fromArgs(*args)
            .withValidation()
            .`as`(WorldCupStatsOptions::class.java)

        val pipeline: Pipeline = Pipeline.create(options)

        val fifaRankingSideInput: PCollectionView<List<TeamFifaRanking>> = pipeline
            .apply("Read Fifa ranking side input", TextIO.read().from("gs://mazlum_dev/team_fifa_ranking.json"))
            .map("Deserialize") { deserializeToTeamRanking(it) }
            .apply("Create as collection view", View.asList());

        pipeline
            .apply("Read Json file", TextIO.read().from(options.inputJsonFile))
            .map("Deserialize") { deserializeToPlayerStatsRaw(it) }
            .map("Validate fields") { it.validateFields() }
            .apply(WithKeys.of<String, TeamPlayerStatsRaw> { x -> x.nationality }
                .withKeyType(TypeDescriptors.strings()))
            .apply(GroupByKey.create())
            .mapFn(
                name = "Compute team player stats",
                startBundleAction = { LOGGER.info("####################Start bundle compute stats") },
                transform = { TeamPlayerStats.computeTeamPlayerStats(it.key, it.value) })
            .mapFnWithContext(
                name = "DDD",
                startBundleAction = { LOGGER.info("###################Start bundle add Fifa ranking") },
                transform = { addFifaRankingToTeam(it, fifaRankingSideInput) }
            )
            .apply("Write To BigQuery", BigQueryIO.write<TeamPlayerStats>()
                .withMethod(BigQueryIO.Write.Method.STORAGE_WRITE_API)
                .to("${options.teamLeagueDataset}.${options.teamStatsTable}")
                .withFormatFunction { toTeamPlayerStatsTableRow(it) }
                .withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_NEVER)
                .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_APPEND))

        pipeline.run().waitUntilFinish()
        LOGGER.info("End of World cup Team stats JOB")
    }

    private fun addFifaRankingToTeam(
        context: DoFn<TeamPlayerStats, TeamPlayerStats>.ProcessContext,
        fifaRankingSideInput: PCollectionView<List<TeamFifaRanking>>
    ): TeamPlayerStats {
        val teamFifaRankingList: List<TeamFifaRanking> = context.sideInput(fifaRankingSideInput)
        val teamPlayerStats: TeamPlayerStats = context.element()

        return teamPlayerStats.addFifaRankingToTeamStats(teamFifaRankingList)
    }

    private fun deserializeToPlayerStatsRaw(playerStatsAsString: String): TeamPlayerStatsRaw {
        return JsonUtil.deserialize(playerStatsAsString, TeamPlayerStatsRaw::class.java)
    }

    private fun deserializeToTeamRanking(teamRankingAsString: String): TeamFifaRanking {
        return JsonUtil.deserialize(teamRankingAsString, TeamFifaRanking::class.java)
    }

    private fun toTeamPlayerStatsTableRow(teamPlayerStats: TeamPlayerStats): TableRow {
        val topScorers: TopScorersStats = teamPlayerStats.topScorers
        val bestPassers: BestPassersStats = teamPlayerStats.bestPassers
        val bestDribblers: BestDribblersStats = teamPlayerStats.bestDribblers
        val goalKeeper: GoalkeeperStats = teamPlayerStats.goalKeeper
        val playersMostAppearances: PlayersMostAppearancesStats = teamPlayerStats.playersMostAppearances
        val playersMostDuelsWon: PlayersMostDuelsWonStats = teamPlayerStats.playersMostDuelsWon
        val playersMostInterception: PlayersMostInterceptionStats = teamPlayerStats.playersMostInterception
        val playersMostSuccessfulTackles: PlayersMostSuccessfulTacklesStats =
            teamPlayerStats.playersMostSuccessfulTackles

        val topScorersRow = TableRow()
            .set("players", topScorers.players.map { toPlayerTableRow(it) })
            .set("goals", topScorers.goals)

        val bestPassersRow = TableRow()
            .set("players", bestPassers.players.map { toPlayerTableRow(it) })
            .set("goalAssists", bestPassers.goalAssists)

        val bestDribblersRow = TableRow()
            .set("players", bestDribblers.players.map { toPlayerTableRow(it) })
            .set("dribbles", bestDribblers.dribbles)

        val goalKeeperRow = TableRow()
            .set("playerName", goalKeeper.playerName)
            .set("appearances", goalKeeper.appearances)
            .set("savePercentage", goalKeeper.savePercentage)
            .set("cleanSheets", goalKeeper.cleanSheets)

        val playersMostAppearancesRow = TableRow()
            .set("players", playersMostAppearances.players.map { toPlayerTableRow(it) })
            .set("appearances", bestPassers.goalAssists)

        val playersMostDuelsWonRow = TableRow()
            .set("players", playersMostDuelsWon.players.map { toPlayerTableRow(it) })
            .set("duels", bestPassers.goalAssists)

        val playersMostInterceptionRow = TableRow()
            .set("players", playersMostInterception.players.map { toPlayerTableRow(it) })
            .set("interceptions", playersMostInterception.interceptions)

        val playersMostSuccessfulTacklesRow = TableRow()
            .set("players", playersMostSuccessfulTackles.players.map { toPlayerTableRow(it) })
            .set("successfulTackles", playersMostSuccessfulTackles.successfulTackles)

        return TableRow()
            .set("teamName", teamPlayerStats.teamName)
            .set("teamTotalGoals", teamPlayerStats.teamTotalGoals)
            .set("fifaRanking", teamPlayerStats.fifaRanking)
            .set("nationalTeamKitSponsor", teamPlayerStats.nationalTeamKitSponsor)
            .set("topScorers", topScorersRow)
            .set("bestPassers", bestPassersRow)
            .set("bestDribblers", bestDribblersRow)
            .set("goalKeeper", goalKeeperRow)
            .set("playersMostAppearances", playersMostAppearancesRow)
            .set("playersMostDuelsWon", playersMostDuelsWonRow)
            .set("playersMostInterception", playersMostInterceptionRow)
            .set("playersMostSuccessfulTackles", playersMostSuccessfulTacklesRow)
            .set("ingestionDate", Instant().toString())
    }

    private fun toPlayerTableRow(player: Player): TableRow {
        return TableRow()
            .set("playerName", player.playerName)
            .set("playerDob", player.playerDob)
            .set("position", player.position)
            .set("club", player.club)
            .set("brandSponsorAndUsed", player.brandSponsorAndUsed)
            .set("appearances", player.appearances)
    }
}