import { OrganizationState } from './organization.state';
import {
  GetOrganizationSuccess,
  PostOrganizationPictureSuccess,
  OrganizationActionsUnion,
  OrganizationActionTypes,
  PutOrganizationSuccess,
} from './organization.actions';

import { DayOfWeek } from '../../profile/edit-preview-profile/models/day-of-week.enum';

export const initialState: OrganizationState = {
  organization: null,
};

export function reducer(state = initialState, action: OrganizationActionsUnion) {
  switch (action.type) {

    case OrganizationActionTypes.GetOrganizationSuccess: {
      const organizationProfile = (action as GetOrganizationSuccess).payload;
      const updatedWorkingHours = sortByDays(organizationProfile.workingHours);

      return {
        ...state,
        organization: {
          ...organizationProfile,
          workingHours: updatedWorkingHours
        },
      };
    }

    case OrganizationActionTypes.PutOrganizationSuccess: {
      return {
        ...state,
        organization: (action as PutOrganizationSuccess).payload,
      };
    }

    case OrganizationActionTypes.PostOrganizationPictureSuccess: {
      return {
        ...state,
        organization: {
          ...state.organization,
          profilePhoto: (action as PostOrganizationPictureSuccess).payload
        }
      };
    }

    default: {
      return state;
    }
  }
}

function sortByDays(workingHours) {
  const weekdayOrder = Object.values(DayOfWeek);

  return workingHours.slice().sort(
    (a, b) => weekdayOrder.indexOf(a.dayOfWeek) - weekdayOrder.indexOf(b.dayOfWeek)
  );
}
