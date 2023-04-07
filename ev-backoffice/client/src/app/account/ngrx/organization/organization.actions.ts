import { Action } from '@ngrx/store';

import { ORGANIZATION } from './organization.state';

import { OrganizationProfile } from '../../profile/edit-preview-profile/models/organization-profile.model';

export const OrganizationActionTypes = {
  GetOrganization: `[${ORGANIZATION}] Get Organization`,
  GetOrganizationSuccess: `[${ORGANIZATION}] Get Organization Success`,
  GetOrganizationFailure: `[${ORGANIZATION}] Get Organization Failure`,
  PutOrganization: `[${ORGANIZATION}] Put Organization`,
  PutOrganizationSuccess: `[${ORGANIZATION}] Put Organization Success`,
  PutOrganizationFailure: `[${ORGANIZATION}] Put Organization Failure`,
  PostOrganizationPicture: `[${ORGANIZATION}] Post Organization Picture`,
  PostOrganizationPictureSuccess: `[${ORGANIZATION}] Post Organization Picture Success`,
  PostOrganizationPictureFailure: `[${ORGANIZATION}] Post Organization Picture Failure`,
  OpenOrganizationSuccessChangingModal: `[${ORGANIZATION}] Open Profile Success Changing Modal`
};


export class GetOrganization implements Action {
  readonly type = OrganizationActionTypes.GetOrganization;

  constructor(public payload: number) {
  }
}

export class GetOrganizationSuccess implements Action {
  readonly type = OrganizationActionTypes.GetOrganizationSuccess;

  constructor(public payload: OrganizationProfile) {
  }
}

export class GetOrganizationFailure implements Action {
  readonly type = OrganizationActionTypes.GetOrganizationFailure;

  constructor(public payload?: any) {
  }
}

export class PutOrganization implements Action {
  readonly type = OrganizationActionTypes.PutOrganization;

  constructor(public payload: OrganizationProfile) {
  }
}

export class PutOrganizationSuccess implements Action {
  readonly type = OrganizationActionTypes.PutOrganizationSuccess;

  constructor(public payload: OrganizationProfile) {
  }
}

export class PutOrganizationFailure implements Action {
  readonly type = OrganizationActionTypes.PutOrganizationFailure;

  constructor(public payload?: any) {
  }
}

export class PostOrganizationPicture implements Action {
  readonly type = OrganizationActionTypes.PostOrganizationPicture;

  constructor(public payload: {id: string; profilePhoto: FormData}) {
  }
}

export class PostOrganizationPictureSuccess implements Action {
  readonly type = OrganizationActionTypes.PostOrganizationPictureSuccess;

  constructor(public payload: {url: string}) {
  }
}

export class PostOrganizationPictureFailure implements Action {
  readonly type = OrganizationActionTypes.PostOrganizationPictureFailure;

  constructor(public payload?: any) {
  }
}

export class OpenOrganizationSuccessChangingModal implements Action {
  readonly type = OrganizationActionTypes.OpenOrganizationSuccessChangingModal;
}

export type OrganizationActionsUnion =
  | GetOrganization
  | GetOrganizationSuccess
  | GetOrganizationFailure
  | PutOrganization
  | PutOrganizationSuccess
  | PutOrganizationFailure
  | PostOrganizationPicture
  | PostOrganizationPictureSuccess
  | PostOrganizationPictureFailure
  | OpenOrganizationSuccessChangingModal;
