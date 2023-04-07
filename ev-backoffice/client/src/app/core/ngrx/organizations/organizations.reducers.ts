import { adapter, OrganizationsState } from './organizations.state';

import {
  GetAffiliatedOrganizationsSuccess,
  GetMenuOrganizationsSuccess,
  OrganizationsActionsUnion,
  OrganizationsActionTypes,
  SetActiveOrganization,
  UpdateActiveOrganization,
  UpdateOrganization
} from './organizations.actions';
import { uniqBy } from 'lodash-es';


export const initialState: OrganizationsState = adapter.getInitialState({
  activeOrganizationId: null,
  affiliatedOrganizations: null,
});

export function reducer(state = initialState, action: OrganizationsActionsUnion) {
  switch (action.type) {

    case OrganizationsActionTypes.SetActiveOrganization:
    case OrganizationsActionTypes.ChangeActiveOrganization: {
      return {
        ...state,
        activeOrganizationId: (action as SetActiveOrganization).payload,
      };
    }

    case OrganizationsActionTypes.GetMenuOrganizationsSuccess: {

      const organizations = (action as GetMenuOrganizationsSuccess).payload;

      return {
        ...adapter.setAll(organizations, state),
      };
    }

    case OrganizationsActionTypes.GetAffiliatedOrganizationsSuccess: {

      const affiliatedOrganizations = (action as GetAffiliatedOrganizationsSuccess).payload;

      return {
        ...state,
        affiliatedOrganizations: uniqBy(affiliatedOrganizations, 'id'),
      };
    }

    case OrganizationsActionTypes.UpdateActiveOrganization: {
      return {
        ...state,
        activeOrganizationId: (action as UpdateActiveOrganization).payload,
      };
    }

    case OrganizationsActionTypes.UpdateOrganization: {
      return {
        ...adapter.upsertOne((action as UpdateOrganization).payload, state),
      };
    }

    default: {
      return state;
    }
  }
}


