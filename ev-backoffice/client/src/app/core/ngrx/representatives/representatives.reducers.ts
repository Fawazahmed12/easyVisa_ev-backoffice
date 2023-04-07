import { adapter, RepresentativesState } from './representatives.state';
import {
  GetFeeScheduleSuccess,
  GetRepresentativesMenuSuccess,
  GetRepresentativesSuccess,
  RepresentativesActionsUnion,
  RepresentativesActionTypes,
  SetCurrentRepresentativeId,
  UpdateCurrentRepresentativeId,
  UpdateRepresentative,
} from './representatives.actions';
import { FeeSchedule } from '../../models/fee-schedule.model';

export const initialState: RepresentativesState = adapter.getInitialState({
  currentRepresentativeId: undefined,
  currentRepresentativeFeeSchedule: null,
  representativesMenu: null,
  feeScheduleIds: [],
  feeScheduleEntities: {},
});

export function reducer(state = initialState, action: RepresentativesActionsUnion) {
  switch (action.type) {

    case RepresentativesActionTypes.GetRepresentativesSuccess: {
      return {
        ...adapter.setAll((action as GetRepresentativesSuccess).payload, state),
      };
    }

    case RepresentativesActionTypes.GetRepresentativesMenuSuccess: {
      return {
        ...state,
        representativesMenu: (action as GetRepresentativesMenuSuccess).payload,
      };
    }

    case RepresentativesActionTypes.GetFeeScheduleSuccess: {
      const {response, id} = (action as GetFeeScheduleSuccess).payload;

      const {feeScheduleEntities, feeScheduleIds} = createFeeScheduleEntities(response, id);
      return {
        ...state,
        currentRepresentativeFeeSchedule: (action as GetFeeScheduleSuccess).payload.response,
        feeScheduleEntities,
        feeScheduleIds
      };
    }

    case RepresentativesActionTypes.SetCurrentRepresentativeId: {
      return {
        ...state,
        currentRepresentativeId: (action as SetCurrentRepresentativeId).payload,
      };
    }

    case RepresentativesActionTypes.UpdateCurrentRepresentativeId: {
      return {
        ...state,
        currentRepresentativeId: (action as UpdateCurrentRepresentativeId).payload,
      };
    }

    case RepresentativesActionTypes.UpdateRepresentative: {
      return {
        ...state,
        ...adapter.upsertOne((action as UpdateRepresentative).payload, state),
      };
    }

    default: {
      return state;
    }
  }


  function createFeeScheduleEntities(response: FeeSchedule[], representativeId: number) {
    const {feeScheduleEntities, feeScheduleIds} = state;

    const updatedEntities = {...feeScheduleEntities, [representativeId]: response};

    return {
      ...state,
      feeScheduleEntities: updatedEntities,
      feeScheduleIds: [...new Set([...feeScheduleIds, representativeId])],
    };
  }
}
