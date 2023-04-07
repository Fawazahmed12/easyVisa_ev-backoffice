import { JobTypes, SettingsState } from './settings.state';
import {
  GetBatchJobsConfig,
  GetRankingDataSuccess,
  GetRepresentativesCountSuccess,
  SettingsActionsUnion,
  SettingsActionTypes
} from './settings.actions';


export const initialState: SettingsState = {
  rankingData: null,
  representativesCount: null,
  jobs: {},
};

export function reducer(state = initialState, action: SettingsActionsUnion) {
  switch (action.type) {

    case SettingsActionTypes.GetRankingDataSuccess:
    case SettingsActionTypes.PutRankingDataSuccess: {
      return {
        ...state,
        rankingData: (action as GetRankingDataSuccess).payload
      };
    }

    case SettingsActionTypes.GetRepresentativesCountSuccess: {
      return {
        ...state,
        representativesCount: (action as GetRepresentativesCountSuccess).payload
      };
    }

    case SettingsActionTypes.PatchBatchJobsConfigSuccess:
    case SettingsActionTypes.GetBatchJobsConfigSuccess: {
      return {
        ...state,
        jobs: {
          [JobTypes.enable]: (action as GetBatchJobsConfig).payload,
        }
      };
    }

    default: {
      return state;
    }
  }
}
