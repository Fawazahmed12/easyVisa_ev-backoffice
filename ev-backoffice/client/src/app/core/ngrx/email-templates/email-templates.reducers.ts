import { adapter, EmailTemplatesState } from './email-templates.state';

import {
  EmailTemplatesActionsUnion,
  EmailTemplatesActionTypes,
  GetEmailTemplatesSuccess,
  GetEmailTemplateSuccess, GetEmailTemplateVariablesSuccess,
  PutEmailTemplateSuccess,
} from './email-templates.actions';

export const initialState: EmailTemplatesState = adapter.getInitialState({
  emailTemplateVariables: null
});

export function reducer(state = initialState, action: EmailTemplatesActionsUnion) {
  switch (action.type) {

    case EmailTemplatesActionTypes.GetEmailTemplatesSuccess: {
      return {
        ...adapter.setAll((action as GetEmailTemplatesSuccess).payload, state),
      };
    }

    case EmailTemplatesActionTypes.GetEmailTemplateSuccess: {
      return {
        ...adapter.upsertOne((action as GetEmailTemplateSuccess).payload, state),
      };
    }

    case EmailTemplatesActionTypes.PutEmailTemplateSuccess: {
      return {
        ...adapter.upsertOne((action as PutEmailTemplateSuccess).payload, state),
      };
    }

    case EmailTemplatesActionTypes.GetEmailTemplateVariablesSuccess: {
      return {
        ...state,
        emailTemplateVariables: (action as GetEmailTemplateVariablesSuccess).payload,
      };
    }

    default: {
      return state;
    }
  }
}
