import {
  GetDocumentProgressSuccess,
  GetQuestionnaireProgressSuccess,
  ProgressStatusesActionsUnion,
  ProgressStatusesActionTypes
} from './progress-statuses.actions';
import { ProgressStatusesState } from './progress-statuses.state';

export const initialState: ProgressStatusesState = {
  questionnaireProgress: null,
  documentProgress: null,
};

export function reducer(state = initialState, action: ProgressStatusesActionsUnion) {
  switch (action.type) {

    case ProgressStatusesActionTypes.GetQuestionnaireProgressSuccess: {
      return {
        ...state,
        questionnaireProgress: (action as GetQuestionnaireProgressSuccess).payload,
      };
    }

    case ProgressStatusesActionTypes.GetQuestionnaireProgressFailure: {
      return {
        ...state,
        questionnaireProgress: null,
      };
    }

    case ProgressStatusesActionTypes.GetDocumentProgressSuccess: {
      return {
        ...state,
        documentProgress: (action as GetDocumentProgressSuccess).payload,
      };
    }

    case ProgressStatusesActionTypes.GetDocumentProgressFailure: {
      return {
        ...state,
        documentProgress: null,
      };
    }

    default: {
      return state;
    }
  }
}
