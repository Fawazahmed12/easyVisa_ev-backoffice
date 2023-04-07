import { Action } from '@ngrx/store';

import { SETTINGS } from './settings.state';

import { RankingData } from '../../../models/ranking-data.model';
import { RepresentativesCount } from '../../../models/representatives-count.model';
import { Job } from '../../../models/site-jobs';


export const SettingsActionTypes = {
  GetRankingData: `[${SETTINGS}] Get Ranking Data`,
  GetRankingDataSuccess: `[${SETTINGS}] Get Ranking Data Success`,
  GetRankingDataFailure: `[${SETTINGS}] Get Ranking Data Failure`,
  PutRankingData: `[${SETTINGS}] Put Ranking Data`,
  PutRankingDataSuccess: `[${SETTINGS}] Put Ranking Data Success`,
  PutRankingDataFailure: `[${SETTINGS}] Put Ranking Data Failure`,
  GetRepresentativesCount: `[${SETTINGS}] Get Representatives Count`,
  GetRepresentativesCountSuccess: `[${SETTINGS}] Get Representatives Count Success`,
  GetRepresentativesCountFailure: `[${SETTINGS}] Get Representatives Count Failure`,
  GetBatchJobsConfig: `[${SETTINGS}] Get Batch Jobs Config`,
  GetBatchJobsConfigSuccess: `[${SETTINGS}] Get Batch Jobs Config Success`,
  GetBatchJobsConfigFailure: `[${SETTINGS}] Get Batch Jobs Config Failure`,
  PatchBatchJobsConfig: `[${SETTINGS}] Patch Batch Jobs Config`,
  PatchBatchJobsConfigSuccess: `[${SETTINGS}] Patch Batch Jobs Config Success`,
  PatchBatchJobsConfigFailure: `[${SETTINGS}] Patch Batch Jobs Config Failure`,
};

export class GetRankingData implements Action {
  readonly type = SettingsActionTypes.GetRankingData;

  constructor(public payload?: any) {
  }
}

export class GetRankingDataSuccess implements Action {
  readonly type = SettingsActionTypes.GetRankingDataSuccess;

  constructor(public payload: RankingData) {
  }
}

export class GetRankingDataFailure implements Action {
  readonly type = SettingsActionTypes.GetRankingDataFailure;

  constructor(public payload?: any) {
  }
}

export class PutRankingData implements Action {
  readonly type = SettingsActionTypes.PutRankingData;

  constructor(public payload?: any) {
  }
}

export class PutRankingDataSuccess implements Action {
  readonly type = SettingsActionTypes.PutRankingDataSuccess;

  constructor(public payload: RankingData) {
  }
}

export class PutRankingDataFailure implements Action {
  readonly type = SettingsActionTypes.PutRankingDataFailure;

  constructor(public payload?: any) {
  }
}

export class GetRepresentativesCount implements Action {
  readonly type = SettingsActionTypes.GetRepresentativesCount;

  constructor(public payload?: any) {
  }
}

export class GetRepresentativesCountSuccess implements Action {
  readonly type = SettingsActionTypes.GetRepresentativesCountSuccess;

  constructor(public payload: RepresentativesCount) {
  }
}

export class GetRepresentativesCountFailure implements Action {
  readonly type = SettingsActionTypes.GetRepresentativesCountFailure;

  constructor(public payload?: any) {
  }
}

export class GetBatchJobsConfig implements Action {
  readonly type = SettingsActionTypes.GetBatchJobsConfig;

  constructor(public payload?: any) {
  }
}

export class GetBatchJobsConfigSuccess implements Action {
  readonly type = SettingsActionTypes.GetBatchJobsConfigSuccess;

  constructor(public payload: Job) {
  }
}

export class GetBatchJobsConfigFailure implements Action {
  readonly type = SettingsActionTypes.GetBatchJobsConfigFailure;

  constructor(public payload?: any) {
  }
}

export class PatchBatchJobsConfig implements Action {
  readonly type = SettingsActionTypes.PatchBatchJobsConfig;

  constructor(public payload?: any) {
  }
}

export class PatchBatchJobsConfigSuccess implements Action {
  readonly type = SettingsActionTypes.PatchBatchJobsConfigSuccess;

  constructor(public payload: Job) {
  }
}

export class PatchBatchJobsConfigFailure implements Action {
  readonly type = SettingsActionTypes.PatchBatchJobsConfigFailure;

  constructor(public payload?: any) {
  }
}


export type SettingsActionsUnion =
  | GetRankingData
  | GetRankingDataSuccess
  | GetRankingDataFailure
  | PutRankingData
  | PutRankingDataSuccess
  | PutRankingDataFailure
  | GetRepresentativesCount
  | GetRepresentativesCountSuccess
  | GetRepresentativesCountFailure
  | GetBatchJobsConfig
  | GetBatchJobsConfigSuccess
  | GetBatchJobsConfigFailure
  | PatchBatchJobsConfig
  | PatchBatchJobsConfigSuccess
  | PatchBatchJobsConfigFailure;
