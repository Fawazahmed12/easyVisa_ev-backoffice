import { AccountModuleRequestEffects } from './requests/effects';
import { ProfileEffects } from './profile/profile.effects';
import { OrganizationEffects } from './organization/organization.effects';
import { ReviewsEffects } from './reviews/reviews.effects';
import { InviteRequestEffects } from './invite-request/invite-request.effects';
import { NonRegisteredApplicantsEffects } from './non-registered-applicants/non-registered-applicants.effects';
import { AttorneyNotificationsEffects } from './attorney-notifications/attorney-notifications.effects';
import { RemindersEffects } from './reminders/reminders.effects';

export const effects = [
  ProfileEffects,
  OrganizationEffects,
  ReviewsEffects,
  InviteRequestEffects,
  NonRegisteredApplicantsEffects,
  AttorneyNotificationsEffects,
  RemindersEffects,
  ...AccountModuleRequestEffects,
];
