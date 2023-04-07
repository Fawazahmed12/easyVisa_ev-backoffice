import { ActionReducerMap } from '@ngrx/store';

import { State } from './state';

import * as fromProfile from './profile/profile.reducers';
import * as fromOrganization from './organization/organization.reducers';
import * as fromReviews from './reviews/reviews.reducers';
import * as fromInviteRequest from './invite-request/invite-request.reducers';
import * as fromNonRegisteredApplicantsRequest from './non-registered-applicants/non-registered-applicants.reducers';
import * as fromAttorneyNotifications from './attorney-notifications/attorney-notifications.reducers';
import * as fromReminders from './reminders/reminders.reducers';
import * as fromAccountModuleRequest from './requests/reducer';

export const reducers: ActionReducerMap<State> = {
  Profile: fromProfile.reducer,
  Organization: fromOrganization.reducer,
  Reviews: fromReviews.reducer,
  InviteRequest: fromInviteRequest.reducer,
  NonRegisteredApplicants: fromNonRegisteredApplicantsRequest.reducer,
  AttorneyNotifications: fromAttorneyNotifications.reducer,
  Reminders: fromReminders.reducer,
  AccountModuleRequests: fromAccountModuleRequest.reducer,
};
