import { Action } from '@ngrx/store';

import { Profile } from '../../../core/models/profile.model';
import { AttorneyProfile } from '../../profile/edit-preview-profile/models/attorney-profile.model';

import { PROFILE } from './profile.state';
import { EmployeeProfile } from '../../profile/edit-preview-profile/models/employee-profile.model';

export const ProfileActionTypes = {
  GetProfile: `[${PROFILE}] Get Profile`,
  GetProfileSuccess: `[${PROFILE}] Get Profile Success`,
  GetProfileFailure: `[${PROFILE}] Get Profile Failure`,
  PutProfile: `[${PROFILE}] Put Profile`,
  OpenProfileSuccessChangingModal: `[${PROFILE}] Open Profile Success Changing Modal`,
  PutProfileSuccess: `[${PROFILE}] Put Profile Success`,
  PutProfileFailure: `[${PROFILE}] Put Profile Failure`,
  PostProfilePicture: `[${PROFILE}] Post Profile Picture`,
  PostProfilePictureSuccess: `[${PROFILE}] Post Profile Picture Success`,
  PostProfilePictureFailure: `[${PROFILE}] Post Profile Picture Failure`,
  PutProfileEmail: `[${PROFILE}] Put Profile Email`,
  PutProfileEmailSuccess: `[${PROFILE}] Put Profile Email Success`,
  PutProfileEmailFailure: `[${PROFILE}] Put Profile Email Failure`,
};


export class GetProfile implements Action {
  readonly type = ProfileActionTypes.GetProfile;
}

export class GetProfileSuccess implements Action {
  readonly type = ProfileActionTypes.GetProfileSuccess;

  constructor(public payload: AttorneyProfile & EmployeeProfile & Profile) {
  }
}

export class GetProfileFailure implements Action {
  readonly type = ProfileActionTypes.GetProfileFailure;

  constructor(public payload?: any) {
  }
}

export class PutProfile implements Action {
  readonly type = ProfileActionTypes.PutProfile;

  constructor(public payload: AttorneyProfile & EmployeeProfile & Profile) {
  }
}

export class PutProfileSuccess implements Action {
  readonly type = ProfileActionTypes.PutProfileSuccess;

  constructor(public payload: AttorneyProfile & EmployeeProfile & Profile) {
  }
}

export class PutProfileFailure implements Action {
  readonly type = ProfileActionTypes.PutProfileFailure;

  constructor(public payload?: any) {
  }
}

export class PostProfilePicture implements Action {
  readonly type = ProfileActionTypes.PostProfilePicture;

  constructor(public payload: {id: string; profilePhoto: FormData}) {
  }
}

export class PostProfilePictureSuccess implements Action {
  readonly type = ProfileActionTypes.PostProfilePictureSuccess;

  constructor(public payload: {url: string}) {
  }
}

export class PostProfilePictureFailure implements Action {
  readonly type = ProfileActionTypes.PostProfilePictureFailure;

  constructor(public payload?: any) {
  }
}

export class OpenProfileSuccessChangingModal implements Action {
  readonly type = ProfileActionTypes.OpenProfileSuccessChangingModal;
}

export class PutProfileEmail implements Action {
  readonly type = ProfileActionTypes.PutProfileEmail;

  constructor(public payload: {email: string}) {
  }
}

export class PutProfileEmailSuccess implements Action {
  readonly type = ProfileActionTypes.PutProfileEmailSuccess;

  constructor(public payload: string) {
  }
}

export class PutProfileEmailFailure implements Action {
  readonly type = ProfileActionTypes.PutProfileEmailFailure;

  constructor(public payload?: any) {
  }
}

export type ProfileActionsUnion =
  | GetProfile
  | GetProfileSuccess
  | GetProfileFailure
  | PutProfile
  | PutProfileSuccess
  | PutProfileFailure
  | PostProfilePicture
  | PostProfilePictureSuccess
  | PostProfilePictureFailure
  | OpenProfileSuccessChangingModal
  | PutProfileEmail
  | PutProfileEmailSuccess
  | PutProfileEmailFailure;
