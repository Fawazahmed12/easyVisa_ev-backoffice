import { ActionReducerMap } from '@ngrx/store';

import { State } from './state';

import * as fromQuestionnaire from './questionnaire/questionnaire.reducers';
import * as fromQuestionnaireRequest from './questionnaire-requests/reducer';

export const reducers: ActionReducerMap<State> = {
  Questionnaire: fromQuestionnaire.reducer,
  QuestionnaireRequests: fromQuestionnaireRequest.reducer
};
