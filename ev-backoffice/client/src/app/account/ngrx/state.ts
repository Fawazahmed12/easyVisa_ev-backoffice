import { createFeatureSelector, createSelector } from '@ngrx/store';

import {
  ACCOUNT_MODULE_REQUESTS,
  AccountModuleRequestState,
  selectAccountModuleRequestsState,
  selectCreateReviewPostState,
  selectInviteAttorneyPutState,
  selectInviteDeleteState,
  selectIsAdminPostState,
  selectLeaveOrganizationPostState,
  selectNonRegisteredApplicantDeleteState,
  selectNotificationsConfigGetState,
  selectNotificationsConfigPutState, selectNotificationTypesGetState,
  selectOrganizationGetState,
  selectOrganizationPicturePostState,
  selectOrganizationPutState,
  selectProfileEmailPutState,
  selectProfileGetState,
  selectProfilePicturePostState,
  selectProfilePutState,
  selectRatingsGetState, selectRemindersGetState, selectRemindersPatchState, selectRequestJoinDeleteState,
  selectRequestJoinPutState,
  selectReviewGetState,
  selectReviewPatchState,
  selectReviewPutState,
  selectReviewsGetState
} from './requests/state';
import { PROFILE, ProfileState, selectProfile, selectProfileState } from './profile/profile.state';
import { ORGANIZATION, OrganizationState, selectOrganization, selectOrganizationState } from './organization/organization.state';
import {
  INVITE_REQUEST,
  InviteRequestState,
  selectInvite,
  selectInviteRequestState,
  selectRequest
} from './invite-request/invite-request.state';
import {
  REVIEWS,
  ReviewsState, selectActiveReview,
  selectActiveReviewId,
  selectReviews,
  selectReviewsEntities, selectReviewsRatings,
  selectReviewsState, selectReviewsTotal, selectReviewsTotalFiltered,
} from './reviews/reviews.state';
import {
  NON_REGISTERED_APPLICANTS,
  NonRegisteredApplicantsState,
  selectNonRegisteredApplicants,
  selectNonRegisteredApplicantState
} from './non-registered-applicants/non-registered-applicants.state';
import {
  ATTORNEY_NOTIFICATIONS,
  AttorneyNotificationsState,
  selectAttorneyNotificationsState, selectClientProgressSettings, selectNotificationTypes, selectTaskQueueSettings
} from './attorney-notifications/attorney-notifications.state';
import {
  REMINDERS,
  RemindersState,
   selectActiveItem, selectActiveItemType,
  selectReminders,
  selectRemindersEntities,
  selectRemindersState
} from './reminders/reminders.state';

export const ACCOUNT_MODULE_STATE = 'AccountModuleState';

export interface State {
  [PROFILE]: ProfileState;
  [ORGANIZATION]: OrganizationState;
  [REVIEWS]: ReviewsState;
  [INVITE_REQUEST]: InviteRequestState;
  [NON_REGISTERED_APPLICANTS]: NonRegisteredApplicantsState;
  [ATTORNEY_NOTIFICATIONS]: AttorneyNotificationsState;
  [REMINDERS]: RemindersState;
  [ACCOUNT_MODULE_REQUESTS]: AccountModuleRequestState;
}

export const selectAccountModuleState = createFeatureSelector<State>(ACCOUNT_MODULE_STATE);

export const getAccountModuleRequestsState = createSelector(
  selectAccountModuleState,
  selectAccountModuleRequestsState,
);

export const getProfileGetRequestState = createSelector(
  getAccountModuleRequestsState,
  selectProfileGetState,
);

export const getProfileState = createSelector(
  selectAccountModuleState,
  selectProfileState,
);

export const getProfile = createSelector(
  getProfileState,
  selectProfile,
);
export const getProfilePutRequestState = createSelector(
  getAccountModuleRequestsState,
  selectProfilePutState,
);
export const getProfilePicturePostRequestState = createSelector(
  getAccountModuleRequestsState,
  selectProfilePicturePostState,
);
export const getOrganizationState = createSelector(
  selectAccountModuleState,
  selectOrganizationState,
);
export const getOrganizationGetRequestState = createSelector(
  getAccountModuleRequestsState,
  selectOrganizationGetState,
);
export const getOrganizationPutRequestState = createSelector(
  getAccountModuleRequestsState,
  selectOrganizationPutState,
);
export const getOrganization = createSelector(
  getOrganizationState,
  selectOrganization,
);

export const getOrganizationPicturePostRequestState = createSelector(
  getAccountModuleRequestsState,
  selectOrganizationPicturePostState,
);

export const getLeaveOrganizationPostRequestState = createSelector(
  getAccountModuleRequestsState,
  selectLeaveOrganizationPostState,
);

export const getRequestJoinPutRequestState = createSelector(
  getAccountModuleRequestsState,
  selectRequestJoinPutState,
);

export const getIsAdminPostRequestState = createSelector(
  getAccountModuleRequestsState,
  selectIsAdminPostState,
);

export const getCreateReviewPostRequestState = createSelector(
  getAccountModuleRequestsState,
  selectCreateReviewPostState,
);

export const getReviewGetRequestState = createSelector(
  getAccountModuleRequestsState,
  selectReviewGetState,
);

export const getReviewsGetRequestState = createSelector(
  getAccountModuleRequestsState,
  selectReviewsGetState,
);

export const getRatingsGetRequestState = createSelector(
  getAccountModuleRequestsState,
  selectRatingsGetState,
);

export const getReviewPutRequestState = createSelector(
  getAccountModuleRequestsState,
  selectReviewPutState,
);

export const getReviewPatchRequestState = createSelector(
  getAccountModuleRequestsState,
  selectReviewPatchState,
);

export const getReviewsState = createSelector(
  selectAccountModuleState,
  selectReviewsState,
);

export const getReviews = createSelector(
  getReviewsState,
  selectReviews,
);

export const getActiveReview = createSelector(
  getReviewsState,
  selectActiveReview,
);

export const getActiveReviewId = createSelector(
  getReviewsState,
  selectActiveReviewId,
);

export const getReviewsEntities = createSelector(
  getReviewsState,
  selectReviewsEntities,
);

export const getReviewsTotal = createSelector(
  getReviewsState,
  selectReviewsTotal,
);

export const getReviewsTotalFiltered = createSelector(
  getReviewsState,
  selectReviewsTotalFiltered,
);

export const getReviewsRatings = createSelector(
  getReviewsState,
  selectReviewsRatings,
);

export const getInvitePutRequestState = createSelector(
  getAccountModuleRequestsState,
  selectInviteAttorneyPutState,
);

export const getRequestPutRequestState = createSelector(
  getAccountModuleRequestsState,
  selectRequestJoinPutState,
);

export const getInviteDeleteRequestState = createSelector(
  getAccountModuleRequestsState,
  selectInviteDeleteState,
);

export const getRequestJoinDeleteRequestState = createSelector(
  getAccountModuleRequestsState,
  selectRequestJoinDeleteState,
);

export const getInviteRequestState = createSelector(
  selectAccountModuleState,
  selectInviteRequestState,
);

export const getInvite = createSelector(
  getInviteRequestState,
  selectInvite,
);

export const getRequestJoin = createSelector(
  getInviteRequestState,
  selectRequest,
);

export const getNonRegisteredApplicantsState = createSelector(
  selectAccountModuleState,
  selectNonRegisteredApplicantState,
);

export const getNonRegisteredApplicants = createSelector(
  getNonRegisteredApplicantsState,
  selectNonRegisteredApplicants
);

export const getNonRegisteredApplicantDeleteRequestState = createSelector(
  getAccountModuleRequestsState,
  selectNonRegisteredApplicantDeleteState,
);

export const getProfileEmailPutRequestState = createSelector(
  getAccountModuleRequestsState,
  selectProfileEmailPutState,
);

export const getAttorneyNotificationsState = createSelector(
  selectAccountModuleState,
  selectAttorneyNotificationsState
);

export const getTaskQueueSettings = createSelector(
  getAttorneyNotificationsState,
  selectTaskQueueSettings
);

export const getClientProgressSettings = createSelector(
  getAttorneyNotificationsState,
  selectClientProgressSettings
);

export const getNotificationsConfigGetRequestState = createSelector(
  getAccountModuleRequestsState,
  selectNotificationsConfigGetState
);

export const getNotificationsConfigPutRequestState = createSelector(
  getAccountModuleRequestsState,
  selectNotificationsConfigPutState
);

export const getRemindersState = createSelector(
  selectAccountModuleState,
  selectRemindersState
);

export const getRemindersEntities = createSelector(
  getRemindersState,
  selectRemindersEntities,
);

export const getReminders = createSelector(
  getRemindersState,
  selectReminders,
);

export const getActiveItem = createSelector(
  getRemindersState,
  selectActiveItem,
);

export const getRemindersGetRequestState = createSelector(
  getAccountModuleRequestsState,
  selectRemindersGetState
);

export const getRemindersPatchRequestState = createSelector(
  getAccountModuleRequestsState,
  selectRemindersPatchState
);

export const getFullNotificationTypes = createSelector(
  getAttorneyNotificationsState,
  selectNotificationTypes
);

export const getActiveCategory = createSelector(
  getRemindersState,
  selectActiveItemType
);

export const getActiveNotification = createSelector(
  getFullNotificationTypes,
  getActiveCategory,
  (notifications, activeCategory) => !!notifications && [...notifications.deadline, ...notifications.importantDocuments, ...notifications.blocked].find(
      notification => notification.value === activeCategory) || null
);

export const getIsActiveNotificationImportantDocument = createSelector(
  getFullNotificationTypes,
  getActiveCategory,
  (notifications, ImportantDocumentsReminder) =>
    !!notifications && !!notifications.importantDocuments.find(
      notification => notification.value === ImportantDocumentsReminder) || false
);

export const getNotificationsTypesGetRequestState = createSelector(
  getAccountModuleRequestsState,
  selectNotificationTypesGetState
);
