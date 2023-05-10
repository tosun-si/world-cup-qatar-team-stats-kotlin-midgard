# teams-league-kotlin-migard-beam-summit

## Run job with Dataflow runner :

```
mvn compile exec:java \
  -Dexec.mainClass=fr.groupbees.application.TeamLeagueApp \
  -Dexec.args=" \
  --project=gb-poc-373711 \
  --runner=DataflowRunner \
  --jobName=team-league-kotlin-midgard-job-$(date +'%Y-%m-%d-%H-%M-%S') \
  --region=europe-west1 \
  --streaming=false \
  --zone=europe-west1-d \
  --tempLocation=gs://mazlum_dev/dataflow/temp \
  --gcpTempLocation=gs://mazlum_dev/dataflow/temp \
  --stagingLocation=gs://mazlum_dev/dataflow/staging \
  --serviceAccount=sa-dataflow-dev@gb-poc-373711.iam.gserviceaccount.com \
  --inputJsonFile=gs://mazlum_dev/team_league/input/json/input_teams_stats_raw.json \
  --inputFileSlogans=gs://mazlum_dev/team_league/input/json/input_team_slogans.json \
  --teamLeagueDataset=mazlum_test \
  --teamStatsTable=team_stat \
  " \
  -Pdataflow-runner
```