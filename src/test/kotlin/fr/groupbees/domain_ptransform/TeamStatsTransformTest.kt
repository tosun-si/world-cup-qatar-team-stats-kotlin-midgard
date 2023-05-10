package fr.groupbees.domain_ptransform

import fr.groupbees.domain.TeamFifaRanking
import fr.groupbees.domain.TeamPlayerStatsRaw
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.apache.beam.sdk.Pipeline
import org.apache.beam.sdk.testing.TestPipeline
import org.apache.beam.sdk.testing.ValidatesRunner
import org.apache.beam.sdk.transforms.Create
import org.apache.beam.sdk.transforms.MapElements
import org.apache.beam.sdk.transforms.SerializableFunction
import org.apache.beam.sdk.transforms.View
import org.apache.beam.sdk.values.PCollection
import org.apache.beam.sdk.values.PCollectionView
import org.apache.beam.sdk.values.TypeDescriptors.strings
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import java.io.Serializable

private inline fun <reified T> deserializeFromResourcePath(resourcePath: String): List<T> {
    val elementAsString = ResourceUtil.toStringContent(resourcePath)
    return Json.decodeFromString(elementAsString)
}

class TeamStatsTransformTest : Serializable {

    @Transient
    private val pipeline: TestPipeline = TestPipeline.create()

    @Rule
    fun pipeline(): TestPipeline = pipeline

    @Test
    @Category(ValidatesRunner::class)
    fun givenInputTeamsStatsRawWithoutErrorWhenTransformToStatsDomainThenExpectedOutputInResult() {
        // Given.
        val inputTeamsStatsRaw = deserializeFromResourcePath<TeamPlayerStatsRaw>(
            "files/input/domain/ptransform/input_team_player_stats_raw_without_error.json"
        )

        val input = pipeline.apply("Read team stats Raw", Create.of(inputTeamsStatsRaw))

        // When.
        val resultTransform = input
            .apply("Transform to team stats", TeamStatsTransform(getTeamFifaRankingSideInput(pipeline)))

        val output: PCollection<String> = resultTransform
            .apply(
                "Map to Json String",
                MapElements.into(strings()).via(SerializableFunction { JsonUtil.serialize(it) })
            )
            .apply(
                "Log Output team stats",
                MapElements.into(strings()).via(SerializableFunction { logStringElement(it) })
            )

//        val expectedTeamsStats = deserializeFromResourcePath<TeamPlayerStats>(
//            "files/expected/domain/ptransform/expected_teams_stats_without_error.json"
//        ).map { Json.encodeToString(it) }
//
//        // Then.
//        PAssert.that(output).containsInAnyOrder(expectedTeamsStats)
        pipeline.run().waitUntilFinish()
    }

    private fun getTeamFifaRankingSideInput(pipeline: Pipeline): PCollectionView<List<TeamFifaRanking>> {
        val teamRankingList = deserializeFromResourcePath<TeamFifaRanking>(
            "files/input/domain/ptransform/input_team_fifa_ranking.json"
        )

        return pipeline
            .apply("String side input", Create.of(teamRankingList))
            .apply("Create as collection view", View.asList())
    }

    private fun logStringElement(element: String): String {
        println(element)
        return element
    }

    companion object {
        private const val SLOGANS = "{\"PSG\": \"Paris est magique\",\"Real\": \"Hala Madrid\"}"
    }
}