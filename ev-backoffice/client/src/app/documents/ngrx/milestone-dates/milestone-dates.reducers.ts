import { adapter, MilestoneDatesState } from './milestone-dates.state';
import {
  GetMilestoneDatesSuccess,
  MilestoneDatesActionsUnion,
  MilestoneDatesActionTypes,
  PostMilestoneDateSuccess
} from './milestone-dates.actions';


export const initialState: MilestoneDatesState = adapter.getInitialState( );

export function reducer(state = initialState, action: MilestoneDatesActionsUnion) {
  switch (action.type) {

    case MilestoneDatesActionTypes.GetMilestoneDatesSuccess: {
      return {
        ...adapter.setAll((action as GetMilestoneDatesSuccess).payload, state),
      };
    }

    case MilestoneDatesActionTypes.PostMilestoneDateSuccess: {
      return {
        ...adapter.upsertOne((action as PostMilestoneDateSuccess).payload, state),
      };
    }

    default: {
      return state;
    }
  }
}
