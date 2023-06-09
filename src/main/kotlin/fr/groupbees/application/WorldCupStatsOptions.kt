package fr.groupbees.application

import org.apache.beam.sdk.options.Description
import org.apache.beam.sdk.options.PipelineOptions

interface WorldCupStatsOptions : PipelineOptions {
    @get:Description("Path of the input Json file to read from")
    var inputJsonFile: String

    @get:Description("Path of the Fifa ranking file to read from")
    var inputFileTeamFifaRanking: String

    @get:Description("Output Dataset")
    var worldCupStatsDataset: String

    @get:Description("Output table")
    var worldCupTeamPlayerStatsTable: String
}