import { FeeScheduleDataActionTypes, FeeScheduleDataActionUnion, GetFeeScheduleSettingsSuccess } from './fee-schedule.actions';


export const initialState = {
  feeScheduleSettings: null,
};

export function reducer(state = initialState, action: FeeScheduleDataActionUnion) {
  switch (action.type) {

    case FeeScheduleDataActionTypes.GetFeeScheduleSettingsSuccess:
    case FeeScheduleDataActionTypes.PostFeeScheduleSettingsSuccess: {
      return {
        ...state,
        feeScheduleSettings: (action as GetFeeScheduleSettingsSuccess).payload,
      };
    }

    case FeeScheduleDataActionTypes.GetFeeScheduleSettingsFailure: {
      return {
        ...state,
        feeScheduleSettings: null,
      };
    }

    default: {
      return state;
    }
  }
}
