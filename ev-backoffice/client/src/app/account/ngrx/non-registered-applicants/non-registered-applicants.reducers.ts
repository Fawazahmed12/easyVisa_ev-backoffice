import {
  DeleteNonRegisteredApplicantSuccess,
  NonRegisteredApplicantsActionsUnion,
  NonRegisteredApplicantsActionTypes, SetNonRegisteredApplicants
} from './non-registered-applicants.action';
import { NonRegisteredApplicantsState } from './non-registered-applicants.state';

export const initialState: NonRegisteredApplicantsState = {
  nonRegisteredApplicants: null,
};

export function reducer(state = initialState, action: NonRegisteredApplicantsActionsUnion) {
  switch (action.type) {

    case NonRegisteredApplicantsActionTypes.SetNonRegisteredApplicants: {
      return {
        ...state,
        nonRegisteredApplicants: (action as SetNonRegisteredApplicants).payload,
      };
    }

    case NonRegisteredApplicantsActionTypes.DeleteNonRegisteredApplicantSuccess: {
      const id = (action as DeleteNonRegisteredApplicantSuccess).payload;
      const updatedApplicants = state.nonRegisteredApplicants.filter((applicant) => applicant.profile.id !== id.id);
      return {
        ...state,
        nonRegisteredApplicants: updatedApplicants,
      };
    }

    default: {
      return state;
    }
  }
}
