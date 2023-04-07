import { ProfileGetRequestEffects } from './profile-get/state';
import { ProfilePutRequestEffects } from './profile-put/state';
import { ProfilePicturePostRequestEffects } from './profile-picture-post/state';
import { OrganizationGetRequestEffects } from './organization-get/state';
import { OrganizationPutRequestEffects } from './organization-put/state';
import { OrganizationPicturePostRequestEffects } from './organization-picture-post/state';
import { LeaveOrganizationPostRequestEffects } from './leave-organization-post/state';
import { InviteAttorneyPutRequestEffects } from './invite-attorney-put/state';
import { IsAdminPostRequestEffects } from './is-admin-post/state';
import { CreateReviewPostRequestEffects } from './create-review-post/state';
import { ReviewGetRequestEffects } from './review-get/state';
import { ReviewPutRequestEffects } from './review-put/state';
import { ReviewsGetRequestEffects } from './reviews-get/state';
import { RatingsGetRequestEffects } from './ratings-get/state';
import { ReviewPatchRequestEffects } from './review-patch/state';
import { InviteDeleteRequestEffects } from './invite-delete/state';
import { RequestJoinPutRequestEffects } from './request-join-put/state';
import { NonRegisteredApplicantDeleteRequestEffects } from './non-registered-applicant-delete/state';
import { ProfileEmailPutRequestEffects } from './profile-email-put/state';
import { NotificationsConfigGetRequestEffects } from './notifications-config-get/state';
import { NotificationsConfigPutRequestEffects } from './notifications-config-put/state';
import { RemindersGetRequestEffects } from './reminders-get/state';
import { RemindersPatchRequestEffects } from './reminders-patch/state';
import { NotificationTypesGetRequestEffects } from './notyfication-types-get/state';
import { RequestJoinDeleteRequestEffects } from './request-join-delete/state';

export const AccountModuleRequestEffects = [
  ProfileGetRequestEffects,
  ProfilePutRequestEffects,
  ProfilePicturePostRequestEffects,
  OrganizationGetRequestEffects,
  OrganizationPutRequestEffects,
  OrganizationPicturePostRequestEffects,
  LeaveOrganizationPostRequestEffects,
  CreateReviewPostRequestEffects,
  ReviewGetRequestEffects,
  ReviewsGetRequestEffects,
  RatingsGetRequestEffects,
  ReviewPutRequestEffects,
  ReviewPatchRequestEffects,
  InviteAttorneyPutRequestEffects,
  InviteDeleteRequestEffects,
  RequestJoinPutRequestEffects,
  RequestJoinDeleteRequestEffects,
  IsAdminPostRequestEffects,
  NonRegisteredApplicantDeleteRequestEffects,
  ProfileEmailPutRequestEffects,
  NotificationsConfigGetRequestEffects,
  NotificationsConfigPutRequestEffects,
  RemindersGetRequestEffects,
  RemindersPatchRequestEffects,
  NotificationTypesGetRequestEffects,
];
