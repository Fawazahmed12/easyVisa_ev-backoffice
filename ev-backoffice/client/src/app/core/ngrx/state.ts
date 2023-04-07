import * as fromUser from './user/user.state';
import * as fromConfigData from './config-data/config-data.state';
import * as fromEmailTemplates from './email-templates/email-templates.state';
import * as fromNotifications from './notifications/notifications.state';
import * as fromRepresentatives from './representatives/representatives.state';
import * as fromOrganizations from './organizations/organizations.state';
import * as fromPackages from './packages/packages.state';
import * as fromPayment from './payment/payment.state';
import * as fromFeeSchedule from './fee-schedule/fee-schedule.state';
import * as fromAuth from './auth/auth.state';
import * as fromAlerts from './alerts/alerts.state';
import * as fromUscisEditionDates from './uscis-edition-dates/uscis-edition-dates.state';
import * as fromTaxes from './taxes/taxes.state';
import * as fromI18n from '../i18n/i18n.state';
import * as fromUserRequest from './user-requests/state';
import * as fromAuthRequest from './auth-requests/state';
import * as fromConfigDataRequest from './config-data-requests/state';
import * as fromEmailTemplatesRequest from './email-templates-requests/state';
import * as fromNotificationsRequest from './notifications-requests/state';
import * as fromRepresentativesRequest from './representatives-requests/state';
import * as fromEmailsRequest from './emails-requests/state';
import * as fromPackagesRequest from './packages-requests/state';
import * as fromPaymentRequest from './payment-requests/state';
import * as fromOrganizationsRequest from './organizations-requests/state';
import * as fromFeeScheduleRequest from './fee-schedule-requests/state';
import * as fromAlertHandlingRequest from './alert-handling-requests/state';
import * as fromAlertsRequest from './alerts-requests/state';
import * as fromUscisEditionDatesRequest from './uscis-edition-dates-requests/state';
import * as fromTaxesRequest from './taxes-requests/state';

export interface State {
  [fromUser.USER]: fromUser.UserState;
  [fromConfigData.CONFIG_DATA]: fromConfigData.ConfigDataState;
  [fromEmailTemplates.EMAIL_TEMPLATES]: fromEmailTemplates.EmailTemplatesState;
  [fromNotifications.NOTIFICATIONS]: fromNotifications.NotificationsState;
  [fromRepresentatives.REPRESENTATIVES]: fromRepresentatives.RepresentativesState;
  [fromOrganizations.ORGANIZATIONS]: fromOrganizations.OrganizationsState;
  [fromPackages.PACKAGES]: fromPackages.PackagesState;
  [fromPayment.PAYMENT]: fromPayment.PaymentState;
  [fromFeeSchedule.FEE_SCHEDULE]: fromFeeSchedule.FeeScheduleState;
  [fromAuth.AUTH]: fromAuth.AuthState;
  [fromAlerts.ALERTS]: fromAlerts.AlertsState;
  [fromUscisEditionDates.USCIS_EDITION_DATES]: fromUscisEditionDates.UscisEditionDatesState;
  [fromTaxes.TAXES]: fromTaxes.TaxesState;
  [fromI18n.I18N]?: fromI18n.I18nState;
  [fromUserRequest.USER_REQUEST]: fromUserRequest.UserRequestState;
  [fromAuthRequest.AUTH_REQUEST]: fromAuthRequest.AuthRequestState;
  [fromConfigDataRequest.CONFIG_DATA_REQUEST]: fromConfigDataRequest.ConfigDataRequestState;
  [fromEmailTemplatesRequest.EMAIL_TEMPLATES_REQUEST]: fromEmailTemplatesRequest.EmailTemplatesRequestState;
  [fromNotificationsRequest.NOTIFICATIONS_REQUEST]: fromNotificationsRequest.NotificationsRequestState;
  [fromRepresentativesRequest.REPRESENTATIVES_REQUEST]: fromRepresentativesRequest.RepresentativesRequestState;
  [fromEmailsRequest.EMAILS_REQUESTS]: fromEmailsRequest.EmailsRequestState;
  [fromPackagesRequest.PACKAGES_REQUEST]: fromPackagesRequest.PackagesRequestState;
  [fromPaymentRequest.PAYMENT_REQUEST]: fromPaymentRequest.PaymentRequestState;
  [fromOrganizationsRequest.ORGANIZATIONS_REQUEST]: fromOrganizationsRequest.OrganizationsRequestState;
  [fromFeeScheduleRequest.FEE_SCHEDULE_REQUEST]: fromFeeScheduleRequest.FeeScheduleRequestState;
  [fromAlertHandlingRequest.ALERT_HANDLING_REQUEST]: fromAlertHandlingRequest.AlertHandlingRequestState;
  [fromAlertsRequest.ALERTS_REQUEST]: fromAlertsRequest.AlertsRequestState;
  [fromUscisEditionDatesRequest.USCIS_EDITION_DATES_REQUEST]: fromUscisEditionDatesRequest.UscisEditionDatesRequestState;
  [fromTaxesRequest.TAXES_REQUEST]: fromTaxesRequest.TaxesRequestState;
}
