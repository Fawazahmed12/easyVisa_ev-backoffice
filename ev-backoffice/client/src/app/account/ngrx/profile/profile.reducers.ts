import { ProfileState } from './profile.state';
import {
  GetProfileSuccess, PostProfilePictureSuccess,
  ProfileActionsUnion,
  ProfileActionTypes, PutProfileEmailSuccess,
  PutProfileSuccess
} from './profile.actions';

import { sortBy } from 'lodash-es';

export const initialState: ProfileState = {
  profile: null,
};

export function reducer(state = initialState, action: ProfileActionsUnion) {
  switch (action.type) {

    case ProfileActionTypes.GetProfileSuccess: {
      const currentProfile = (action as GetProfileSuccess).payload;
      const currentEducation = currentProfile.education;
      const sortedEducation = sortBy(currentEducation, 'year');
      const profileResult = {
        ...currentProfile,
        education: sortedEducation
      };

      return {
        ...state,
        profile: profileResult,
      };
    }

    case ProfileActionTypes.PutProfileSuccess: {
      const currentProfile = (action as PutProfileSuccess).payload;
      const currentEducation = currentProfile.education;
      const sortedEducation = sortBy(currentEducation, 'year');
      const profileResult = {
        ...currentProfile,
        education: sortedEducation
      };
      return {
        ...state,
        profile: profileResult,
      };
    }

    case ProfileActionTypes.PostProfilePictureSuccess: {
      return {
        ...state,
        profile: {
          ...state.profile,
          profilePhoto: (action as PostProfilePictureSuccess).payload
        }
      };
    }

    case ProfileActionTypes.PutProfileEmailSuccess: {
      return {
        ...state,
        profile: {
          ...state.profile,
          email: (action as PutProfileEmailSuccess).payload
        }
      };
    }

    default: {
      return state;
    }
  }
}
