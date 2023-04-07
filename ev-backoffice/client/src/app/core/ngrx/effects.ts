import { UserEffects } from './user/user.effects';
import { UserRequestEffects } from './user-requests/effects';
import { AuthRequestEffects } from './auth-requests/effects';
import { ConfigDataRequestEffects } from './config-data-requests/effects';
import { ConfigDataEffects } from './config-data/config-data.effects';
import { EmailTemplatesEffects } from './email-templates/email-templates.effects';
import { EmailTemplatesRequestEffects } from './email-templates-requests/effects';
import { NotificationsEffects } from './notifications/notifications.effects';
import { NotificationsRequestEffects } from './notifications-requests/effects';
import { RepresentativesRequestEffects } from './representatives-requests/effects';
import { RepresentativesEffects } from './representatives/representatives.effects';
import { OrganizationsEffects } from './organizations/organizations.effects';
import { EmailsRequestEffects } from './emails-requests/effects';
import { PackagesEffects } from './packages/packages.effects';
import { PackagesRequestEffects } from './packages-requests/effects';
import { PaymentRequestEffects } from './payment-requests/effects';
import { PaymentEffects } from './payment/payment.effects';
import { OrganizationsRequestEffects } from './organizations-requests/effects';
import { FeeScheduleEffects } from './fee-schedule/fee-schedule.effects';
import { FeeScheduleRequestEffects } from './fee-schedule-requests/effects';
import { AlertHandlingRequestEffects } from './alert-handling-requests/effects';
import { AuthEffects } from './auth/auth.effect';
import { AlertsEffects } from './alerts/alerts.effects';
import { AlertsRequestEffects } from './alerts-requests/effects';
import { UscisEditionDatesEffects } from './uscis-edition-dates/uscis-edition-dates.effects';
import { UscisEditionDatesRequestEffects } from './uscis-edition-dates-requests/effects';
import { TaxesEffects } from './taxes/taxes.effect';
import { TaxesRequestEffects } from './taxes-requests/effects';

export const effects = [
  UserEffects,
  ConfigDataEffects,
  EmailTemplatesEffects,
  NotificationsEffects,
  RepresentativesEffects,
  OrganizationsEffects,
  PackagesEffects,
  PaymentEffects,
  FeeScheduleEffects,
  AuthEffects,
  AlertsEffects,
  UscisEditionDatesEffects,
  TaxesEffects,
  ...UserRequestEffects,
  ...AuthRequestEffects,
  ...ConfigDataRequestEffects,
  ...EmailTemplatesRequestEffects,
  ...NotificationsRequestEffects,
  ...RepresentativesRequestEffects,
  ...EmailsRequestEffects,
  ...PackagesRequestEffects,
  ...PaymentRequestEffects,
  ...OrganizationsRequestEffects,
  ...FeeScheduleRequestEffects,
  ...AlertHandlingRequestEffects,
  ...AlertsRequestEffects,
  ...UscisEditionDatesRequestEffects,
  ...TaxesRequestEffects,
];
