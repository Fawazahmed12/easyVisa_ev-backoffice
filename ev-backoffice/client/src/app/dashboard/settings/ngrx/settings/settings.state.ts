import { createFeatureSelector } from '@ngrx/store';

import { RankingData } from '../../../models/ranking-data.model';
import { RepresentativesCount } from '../../../models/representatives-count.model';
import { Job } from '../../../models/site-jobs';

export const SETTINGS = 'Settings';

export enum JobTypes {
  enable = 'enable',
}

export interface SettingsState {
  rankingData: RankingData;
  representativesCount: RepresentativesCount;
  jobs: {
    [type: string]: Job;
  };
}

export const selectSettingsState = createFeatureSelector<SettingsState>(SETTINGS);

export const selectRankingsData = ({rankingData}: SettingsState) => rankingData;
export const selectRepresentativesCount = ({representativesCount}: SettingsState) => representativesCount;
export const selectBatchJob = ({jobs}: SettingsState) => jobs[JobTypes.enable];


