import { adapter, DispositionsState } from './dispositions.state';
import {
  DispositionsActionsUnion,
  DispositionsActionTypes,
  GetDispositionDataSuccess,
  GetDispositionsSuccess,
  PutDispositionSuccess, RemoveDisposition,
  SetActiveDisposition
} from './dispositions.actions';

import { DocumentFileType } from '../../../documents/models/documents.model';


export const initialState: DispositionsState = adapter.getInitialState({
  activeDispositionId: null,
  activeDispositionData: null,
  totalDispositions: null
});

export function reducer(state = initialState, action: DispositionsActionsUnion) {
  switch (action.type) {

    case DispositionsActionTypes.GetDispositionsSuccess: {

      const payload = (action as GetDispositionsSuccess).payload;

      return {
        ...adapter.setAll(payload.body, state),
        totalDispositions: payload.xTotalCount
      };
    }

    case DispositionsActionTypes.PutDispositionSuccess: {
      const payload = (action as PutDispositionSuccess).payload;
      const totalDispositions = payload.totalDispositions;
      return {
        ...adapter.upsertOne(payload, state),
        totalDispositions
      };
    }

    case DispositionsActionTypes.SetActiveDisposition: {
      return {
        ...state,
        activeDispositionId: (action as SetActiveDisposition).payload
      };
    }

    case DispositionsActionTypes.GetDispositionDataSuccess: {
      const payload = (action as GetDispositionDataSuccess).payload;
      const file = payload.file;
      const fileType = file.type;
      const fileBlob = new Blob([file], {type: DocumentFileType[fileType]});
      const fileUrl = URL.createObjectURL(fileBlob);
      const activeDispositionData = {
        ...payload,
        file: fileUrl,
        fileType: DocumentFileType[fileType],
      };
      return {
        ...state,
        activeDispositionData
      };
    }

    case DispositionsActionTypes.GetDispositionDataFailure:
    case DispositionsActionTypes.ResetActiveDisposition: {
      return {
        ...state,
        activeDispositionId: null,
        activeDispositionData: null,
      };
    }

    case DispositionsActionTypes.ResetDispositions: {
      return {
        ...adapter.removeAll(state),
        activeDispositionId: null,
        activeDispositionData: null,
      };
    }

    case DispositionsActionTypes.RemoveDisposition: {
      const payload = (action as RemoveDisposition).payload;
      const totalDispositions = payload.totalDispositions;
      return {
        ...adapter.removeOne(payload.id, state),
        totalDispositions
      };
    }

    default: {
      return state;
    }
  }
}
