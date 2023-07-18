# world-cup-qatar-team-stats-kotlin-midgard

## Run job with Dataflow runner :

```
mvn compile exec:java \
  -Dexec.mainClass=fr.groupbees.application.WorldCupStatsApp \
  -Dexec.args=" \
  --project=gb-poc-373711 \
  --runner=DataflowRunner \
  --jobName=world-cup-team-stats-kotlin-midgard-job-$(date +'%Y-%m-%d-%H-%M-%S') \
  --dataflowServiceOptions=enable_prime \
  --region=europe-west1 \
  --zone=europe-west1-d \
  --tempLocation=gs://mazlum_dev/dataflow/temp \
  --gcpTempLocation=gs://mazlum_dev/dataflow/temp \
  --stagingLocation=gs://mazlum_dev/dataflow/staging \
  --serviceAccount=sa-dataflow-dev@gb-poc-373711.iam.gserviceaccount.com \
  --inputJsonFile=gs://mazlum_dev/world_cup_team_stats/input/world_cup_team_players_stats_raw_ndjson.json \
  --inputFileTeamFifaRanking=gs://mazlum_dev/world_cup_team_stats/input/team_fifa_ranking.json \
  --worldCupStatsDataset=mazlum_test \
  --worldCupTeamPlayerStatsTable=world_cup_team_players_stat_demo \
  " \
  -Pdataflow-runner
```