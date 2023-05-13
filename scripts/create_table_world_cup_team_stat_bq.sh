bq mk -t \
  --schema schema/world_cup_team_player_stat_schema.json \
  --time_partitioning_field ingestionDate \
  --time_partitioning_type DAY \
  gb-poc-373711:mazlum_test.world_cup_team_players_stat