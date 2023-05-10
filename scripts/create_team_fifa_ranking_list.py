import json
from itertools import groupby
from team_league_elt.root import ROOT_DIR
from typing import List, Dict


def build_team_fifa_ranking_list():
    with open(f'{ROOT_DIR}/world_cup_team_players_stats_raw.json') as json_file:
        team_stats_as_dicts = json.load(json_file)

    team_fifa_ranking: List[Dict] = []
    for k, g in groupby(team_stats_as_dicts, lambda t: t['nationality']):
        group_values = list(g)

        any_value = group_values[0]
        fifa_ranking = any_value['fifaRanking']

        team_fifa_ranking.append(
            {
                'teamName': k,
                'fifaRanking': fifa_ranking
            }
        )

    return team_fifa_ranking


if __name__ == '__main__':
    build_team_fifa_ranking_list()
