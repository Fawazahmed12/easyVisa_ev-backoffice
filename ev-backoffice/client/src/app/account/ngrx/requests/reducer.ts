import { AccountModuleRequestState } from './state';
import { profileGetRequestReducer } from './profile-get/state';
import { profilePutRequestReducer } from './profile-put/state';
import { profilePicturePostRequestReducer } from './profile-picture-post/state';
import { organizationGetRequestReducer } from './organization-get/state';
import { organizationPutRequestReducer } from './organization-put/state';
import { organizationPicturePostRequestReducer } from './organization-picture-post/state';
import { leaveOrganizationPostRequestReducer } from './leave-organization-post/state';
import { inviteAttorneyPutRequestReducer } from './invite-attorney-put/state';
import { requestJoinPutRequestReducer } from './request-join-put/state';
import { isAdminPostRequestReducer } from './is-admin-post/state';
import { createReviewPostRequestReducer } from './create-review-post/state';
import { reviewGetRequestReducer } from './review-get/state';
import { reviewPutRequestReducer } from './review-put/state';
import { reviewsGetRequestReducer } from './reviews-get/state';
import { ratingsGetRequestReducer } from './ratings-get/state';
import { reviewPatchRequestReducer } from './review-patch/state';
import { inviteDeleteRequestReducer } from './invite-delete/state';
import { nonRegisteredApplicantDeleteRequestReducer } from './non-registered-applicant-delete/state';
import { requestJoinDeleteRequestReducer } from './request-join-delete/state';
import { notificationsConfigGetRequestReducer } from './notifications-config-get/state';
import { notificationsConfigPutRequestReducer } from './notifications-config-put/state';
import { profileEmailPutRequestReducer } from './profile-email-put/state';
import { remindersGetRequestReducer } from './reminders-get/state';
import { remindersPatchRequestReducer } from './reminders-patch/state';
import { notificationTypesGetRequestReducer } from './notyfication-types-get/state';

export function reducer(state: AccountModuleRequestState = {}, action): AccountModuleRequestState {
  return {
    profileGet: profileGetRequestReducer(state.profileGet, action),
    profilePut: profilePutRequestReducer(state.profilePut, action),
    profilePicturePost: profilePicturePostRequestReducer(state.profilePicturePost, action),
    organizationGet: organizationGetRequestReducer(state.organizationGet, action),
    organizationPut: organizationPutRequestReducer(state.organizationPut, action),
    organizationPicturePost: organizationPicturePostRequestReducer(state.organizationPicturePost, action),
    leaveOrganizationPost: leaveOrganizationPostRequestReducer(state.leaveOrganizationPost, action),
    createReviewPost: createReviewPostRequestReducer(state.createReviewPost, action),
    reviewGet: reviewGetRequestReducer(state.reviewGet, action),
    reviewPut: reviewPutRequestReducer(state.reviewPut, action),
    reviewPatch: reviewPatchRequestReducer(state.reviewPatch, action),
    reviewsGet: reviewsGetRequestReducer(state.reviewsGet, action),
    ratingsGet: ratingsGetRequestReducer(state.ratingsGet, action),
    inviteAttorneyPut: inviteAttorneyPutRequestReducer(state.inviteAttorneyPut, action),
    inviteDelete: inviteDeleteRequestReducer(state.inviteDelete, action),
    requestJoinPut: requestJoinPutRequestReducer(state.requestJoinPut, action),
    requestJoinDelete: requestJoinDeleteRequestReducer(state.requestJoinDelete, action),
    isAdminPost: isAdminPostRequestReducer(state.isAdminPost, action),
    nonRegisteredApplicantDelete: nonRegisteredApplicantDeleteRequestReducer(state.nonRegisteredApplicantDelete, action),
    profileEmailPut: profileEmailPutRequestReducer(state.profileEmailPut, action),
    notificationsConfigGet: notificationsConfigGetRequestReducer(state.notificationsConfigGet, action),
    notificationsConfigPut: notificationsConfigPutRequestReducer(state.notificationsConfigPut, action),
    remindersGet: remindersGetRequestReducer(state.remindersGet, action),
    remindersPatch: remindersPatchRequestReducer(state.remindersPatch, action),
    notificationTypesGet: notificationTypesGetRequestReducer(state.notificationTypesGet, action),
  };
}
