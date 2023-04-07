export interface RankingData {
  recentContributor: RankingDataDetails;
  topContributor: RankingDataDetails;
  allReps: RankingDataDetails;
}

export interface RankingDataDetails {
  points: number;
  articlesInMonth: number;
  articlesInQuarter: number;
  articlesInHalf: number;
}

