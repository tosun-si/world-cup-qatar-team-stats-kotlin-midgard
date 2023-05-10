bq mk -t \
  --schema team_stat_table_schema.json \
  --time_partitioning_field ingestionDate \
  --time_partitioning_type DAY \
  gb-poc-373711:mazlum_test.team_stat