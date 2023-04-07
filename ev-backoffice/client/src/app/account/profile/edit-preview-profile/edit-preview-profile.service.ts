import { Injectable } from '@angular/core';
import { select, Store } from '@ngrx/store';

import { filter, share } from 'rxjs/operators';
import { Observable } from 'rxjs';

import { State } from '../../../core/ngrx/state';
import { throwIfRequestFailError } from '../../../core/ngrx/utils/rxjs-utils';
import { RequestState } from '../../../core/ngrx/utils';
import { Profile } from '../../../core/models/profile.model';

import { OrganizationType } from '../../../core/models/organization-type.enum';
import { PackageApplicant } from '../../../core/models/package/package-applicant.model';
import { GetApplicants } from '../../../core/ngrx/packages/packages.actions';

import { GetProfile, PostProfilePicture, PutProfile, PutProfileEmail } from '../../ngrx/profile/profile.actions';
import {
  getNonRegisteredApplicantDeleteRequestState,
  getNonRegisteredApplicants,
  getOrganization,
  getOrganizationGetRequestState, getOrganizationPicturePostRequestState, getOrganizationPutRequestState,
  getProfile, getProfileEmailPutRequestState,
  getProfileGetRequestState,
  getProfilePicturePostRequestState,
  getProfilePutRequestState
} from '../../ngrx/state';
import { GetOrganization, PostOrganizationPicture, PutOrganization } from '../../ngrx/organization/organization.actions';

import {
  DeleteNonRegisteredApplicant,
  SetNonRegisteredApplicants
} from '../../ngrx/non-registered-applicants/non-registered-applicants.action';

import { OrganizationProfile } from './models/organization-profile.model';

@Injectable()
export class EditPreviewProfileService {
  nonRegisteredApplicants$: Observable<PackageApplicant[]>;
  nonRegisteredApplicantDeleteRequestState$: Observable<RequestState<number>>;
  organizationGetRequestState$: Observable<RequestState<OrganizationProfile>>;
  organizationPutRequest$: Observable<RequestState<OrganizationProfile>>;
  organizationPicturePostRequest$: Observable<RequestState<{ url: string }>>;
  organization$: Observable<OrganizationProfile>;
  profileGetRequest$: Observable<RequestState<Profile>>;
  profilePutRequest$: Observable<RequestState<Profile>>;
  profilePicturePostRequest$: Observable<RequestState<{ url: string }>>;
  profileEmailPutRequest$: Observable<RequestState<{ email: string }>>;
  profile$: Observable<Profile>;

  constructor(
    private store: Store<State>,
  ) {
    this.nonRegisteredApplicants$ = this.store.pipe(select(getNonRegisteredApplicants));
    this.nonRegisteredApplicantDeleteRequestState$ = this.store.pipe(select(getNonRegisteredApplicantDeleteRequestState));
    this.organizationPutRequest$ = this.store.pipe(select(getOrganizationPutRequestState));
    this.organizationGetRequestState$ = this.store.pipe(select(getOrganizationGetRequestState));
    this.organizationPicturePostRequest$ = this.store.pipe(select(getOrganizationPicturePostRequestState));
    this.organization$ = this.store.pipe(select(getOrganization));
    this.profileGetRequest$ = this.store.pipe(select(getProfileGetRequestState));
    this.profilePutRequest$ = this.store.pipe(select(getProfilePutRequestState));
    this.profilePicturePostRequest$ = this.store.pipe(select(getProfilePicturePostRequestState));
    this.profileEmailPutRequest$ = this.store.pipe(select(getProfileEmailPutRequestState));
    this.profile$ = this.store.pipe(select(getProfile));
  }

  getProfile() {
    this.store.dispatch(new GetProfile());
    return this.profileGetRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  updateProfile(profile) {
    Object.keys(profile).forEach((key) => (profile[key] == null) && delete profile[key]);
    this.store.dispatch(new PutProfile(profile));
    return this.profilePutRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  uploadProfilePicture(data) {
    this.store.dispatch(new PostProfilePicture(data));
    return this.profilePicturePostRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  getOrganization(id) {
    this.store.dispatch(new GetOrganization(id));
    return this.organizationGetRequestState$.pipe(
      filter((state) => !state.loading),
      throwIfRequestFailError(),
      share(),
    );
  }

  updateOrganization(profile) {
    Object.keys(profile).forEach((key) => (profile[key] == null) && delete profile[key]);
    this.store.dispatch(new PutOrganization(profile));
    return this.organizationPutRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  uploadOrganizationPicture(data) {
    this.store.dispatch(new PostOrganizationPicture(data));
    return this.organizationPicturePostRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  getProfilePreviewTitle(isOrganizationProfile: boolean, activeOrganizationType: OrganizationType) {
    if (isOrganizationProfile) {
      switch (activeOrganizationType) {
        case OrganizationType.LAW_FIRM: {
          return 'TEMPLATE.ACCOUNT.PROFILE.PROFILE_PREVIEW_TITLES.LAW_FIRM';
        }
        case OrganizationType.RECOGNIZED_ORGANIZATION: {
          return 'TEMPLATE.ACCOUNT.PROFILE.PROFILE_PREVIEW_TITLES.RECOGNIZED_ORGANIZATION';
        }
        default: {
          return '';
        }
      }
    } else {
      switch (activeOrganizationType) {
        case OrganizationType.SOLO_PRACTICE: {
          return 'TEMPLATE.ACCOUNT.PROFILE.PROFILE_PREVIEW_TITLES.SOLO_PRACTITIONER';
        }
        case OrganizationType.LAW_FIRM: {
          return 'TEMPLATE.ACCOUNT.PROFILE.PROFILE_PREVIEW_TITLES.ATTORNEY';
        }
        case OrganizationType.RECOGNIZED_ORGANIZATION: {
          return 'TEMPLATE.ACCOUNT.PROFILE.PROFILE_PREVIEW_TITLES.ACCREDITED_REPRESENTATIVE';
        }
        default: {
          return '';
        }
      }
    }
  }

  getNonRegisteredApplicants(packageId) {
    this.store.dispatch(new GetApplicants(packageId));
  }

  resetNonRegisteredApplicants() {
    this.store.dispatch(new SetNonRegisteredApplicants(null));
  }

  deleteNonRegisteredApplicant(applicantId) {
    this.store.dispatch(new DeleteNonRegisteredApplicant(applicantId));
  }

  updateProfileEmail(data) {
    this.store.dispatch(new PutProfileEmail(data));
  }
}
