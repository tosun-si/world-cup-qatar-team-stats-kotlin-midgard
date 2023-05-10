bq --location=EU load \
--autodetect \
--source_format=NEWLINE_DELIMITED_JSON \
gb-poc-373711:mazlum_test.team_stat_raw \
./input_teams_stats_raw_pubsub.json