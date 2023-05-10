package fr.groupbees.application

import org.apache.beam.sdk.options.Description
import org.apache.beam.sdk.options.PipelineOptions

interface WorldCupStatsOptions : PipelineOptions {
    @get:Description("Path of the input Json file to read from")
    var inputJsonFile: String

    @get:Description("Path of the slogans file to read from")
    var inputFileSlogans: String

    @get:Description("Path of the file to write to")
    var teamLeagueDataset: String

    @get:Description("Team stats table")
    var teamStatsTable: String
}