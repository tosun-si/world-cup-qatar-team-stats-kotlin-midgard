while read team_stat_message; do
  echo "Publishing trophy message $team_stat_message"

  gcloud pubsub topics publish team_stats \
    --message="$team_stat_message"

done <input_teams_stats_raw_pubsub.json

