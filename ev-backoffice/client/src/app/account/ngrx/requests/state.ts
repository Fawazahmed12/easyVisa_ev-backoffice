import { RequestState } from '../../../core/ngrx/utils';
import { Profile } from '../../../core/models/profile.model';

import { AttorneyProfile } from '../../profile/edit-preview-profile/models/attorney-profile.model';
import { EmployeeProfile } from '../../profile/edit-preview-profile/models/employee-profile.model';
import { OrganizationProfile } from '../../profile/edit-preview-profile/models/organization-profile.model';
import { Review } from '../../models/review.model';
import { Invite } from '../../models/invite.model';
import { NotificationSettings } from '../../models/notification-settings.model';
import { remindersPatchRequestReducer } from './reminders-patch/state';
import { Reminder } from '../../models/reminder.model';
import { NotificationTypes } from '../../models/notyfication-types.model';

export const ACCOUNT_MODULE_REQUESTS = 'AccountModuleRequests';

export interface AccountModuleRequestState {
  profileGet?: RequestState<AttorneyProfile & EmployeeProfile & Profile>;
  profilePut?: RequestState<AttorneyProfile & EmployeeProfile & Profile>;
  profilePicturePost?: RequestState<{url: string}>;
  organizationGet?: RequestState<OrganizationProfile>;
  organizationPut?: RequestState<OrganizationProfile>;
  organizationPicturePost?: RequestState<{url: string}>;
  leaveOrganizationPost?: RequestState<any>;
  createReviewPost?: RequestState<any>;
  reviewGet?: RequestState<Review>;
  reviewPut?: RequestState<Review>;
  reviewPatch?: RequestState<Review>;
  reviewsGet?: RequestState<Review[]>;
  ratingsGet?: RequestState<any>;
  inviteAttorneyPut?: RequestState<Invite>;
  inviteDelete?: RequestState<any>;
  requestJoinPut?: RequestState<any>;
  requestJoinDelete?: RequestState<any>;
  isAdminPost?: RequestState<any>;
  nonRegisteredApplicantDelete?: RequestState<number>;
  profileEmailPut?: RequestState<{email: string}>;
  notificationsConfigGet?: RequestState<NotificationSettings>;
  notificationsConfigPut?: RequestState<NotificationSettings>;
  remindersGet?: RequestState<Reminder[]>;
  remindersPatch?: RequestState<Reminder[]>;
  notificationTypesGet?: RequestState<NotificationTypes>;
}

export const selectAccountModuleRequestsState = (state) => state[ACCOUNT_MODULE_REQUESTS];

export const selectProfileGetState = (state: AccountModuleRequestState) => state.profileGet;
export const selectProfilePutState = (state: AccountModuleRequestState) => state.profilePut;
export const selectProfilePicturePostState = (state: AccountModuleRequestState) => state.profilePicturePost;
export const selectOrganizationGetState = (state: AccountModuleRequestState) => state.organizationGet;
export const selectOrganizationPutState = (state: AccountModuleRequestState) => state.organizationPut;
export const selectOrganizationPicturePostState = (state: AccountModuleRequestState) => state.organizationPicturePost;
export const selectLeaveOrganizationPostState = (state: AccountModuleRequestState) => state.leaveOrganizationPost;
export const selectInviteAttorneyPutState = (state: AccountModuleRequestState) => state.inviteAttorneyPut;
export const selectInviteDeleteState = (state: AccountModuleRequestState) => state.inviteDelete;
export const selectRequestJoinPutState = (state: AccountModuleRequestState) => state.requestJoinPut;
export const selectRequestJoinDeleteState = (state: AccountModuleRequestState) => state.requestJoinDelete;
export const selectIsAdminPostState = (state: AccountModuleRequestState) => state.isAdminPost;
export const selectCreateReviewPostState = (state: AccountModuleRequestState) => state.createReviewPost;
export const selectReviewGetState = (state: AccountModuleRequestState) => state.reviewGet;
export const selectReviewPutState = (state: AccountModuleRequestState) => state.reviewPut;
export const selectReviewPatchState = (state: AccountModuleRequestState) => state.reviewPatch;
export const selectReviewsGetState = (state: AccountModuleRequestState) => state.reviewsGet;
export const selectRatingsGetState = (state: AccountModuleRequestState) => state.ratingsGet;
export const selectNonRegisteredApplicantDeleteState = (state: AccountModuleRequestState) => state.nonRegisteredApplicantDelete;
export const selectProfileEmailPutState = (state: AccountModuleRequestState) => state.profileEmailPut;
export const selectNotificationsConfigGetState = (state: AccountModuleRequestState) => state.notificationsConfigGet;
export const selectNotificationsConfigPutState = (state: AccountModuleRequestState) => state.notificationsConfigPut;
export const selectRemindersGetState = (state: AccountModuleRequestState) => state.remindersGet;
export const selectRemindersPatchState = (state: AccountModuleRequestState) => state.remindersPatch;
export const selectNotificationTypesGetState = (state: AccountModuleRequestState) => state.notificationTypesGet;

export { profileGetRequestHandler } from './profile-get/state';
export { profilePutRequestHandler } from './profile-put/state';
export { profilePicturePostRequestHandler } from './profile-picture-post/state';
export { organizationGetRequestHandler } from './organization-get/state';
export { organizationPutRequestHandler } from './organization-put/state';
export { organizationPicturePostRequestHandler } from './organization-picture-post/state';
export { leaveOrganizationPostRequestHandler } from './leave-organization-post/state';
export { inviteAttorneyPutRequestHandler } from './invite-attorney-put/state';
export { requestJoinPutRequestHandler } from './request-join-put/state';
export { requestJoinDeleteRequestHandler } from './request-join-delete/state';
export { inviteDeleteRequestHandler } from './invite-delete/state';
export { createReviewPostRequestHandler } from './create-review-post/state';
export { reviewGetRequestHandler } from './review-get/state';
export { reviewPutRequestHandler } from './review-put/state';
export { reviewPatchRequestHandler } from './review-patch/state';
export { reviewsGetRequestHandler } from './reviews-get/state';
export { ratingsGetRequestHandler } from './ratings-get/state';
export { nonRegisteredApplicantDeleteRequestHandler } from './non-registered-applicant-delete/state';
export { profileEmailPutRequestHandler } from './profile-email-put/state';
export { notificationsConfigGetRequestHandler } from './notifications-config-get/state';
export { notificationsConfigPutRequestHandler } from './notifications-config-put/state';
export { remindersGetRequestHandler } from './reminders-get/state';
export { remindersPatchRequestHandler } from './reminders-patch/state';
export { notificationTypesGetRequestHandler } from './notyfication-types-get/state';

