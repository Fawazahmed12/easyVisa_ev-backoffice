import { FormsSheetsState } from './forms-sheets.state';
import {
  FormsSheetsActionsUnion,
  FormsSheetsActionTypes,
  GetBlanksSuccess,
  GetFormsSheetsSuccess,
  SelectApplicants
} from './forms-sheets.actions';
import { PackagesActionTypes } from '../../../core/ngrx/packages/packages.actions';


export const initialState: FormsSheetsState = {
  packageApplicants: null,
  packageForms: null,
  packageContinuationSheets: null,
  selectedApplicantsIds: null,
  blanks: null,
};


export function reducer(state = initialState, action: FormsSheetsActionsUnion) {
  switch (action.type) {

    case FormsSheetsActionTypes.GetFormsSheetsSuccess: {
      const payload = (action as GetFormsSheetsSuccess).payload;
      return {
        ...state,
        packageApplicants: payload.packageApplicants,
        packageForms: payload.packageForms,
        packageContinuationSheets: payload.packageContinuationSheets,
      };
    }

    case FormsSheetsActionTypes.SelectApplicants: {
      return {
        ...state,
        selectedApplicantsIds: (action as SelectApplicants).payload,
      };
    }

    case FormsSheetsActionTypes.GetBlanksSuccess: {
      return {
        ...state,
        blanks: (action as GetBlanksSuccess).payload
      };
    }

    case PackagesActionTypes.ClearActivePackage: {
      return{
        ...state,
        packageApplicants: null,
        packageForms: null,
        packageContinuationSheets: null,
        selectedApplicantsIds: null,
        blanks: null,
      };
    }

    default: {
      return state;
    }
  }
}
