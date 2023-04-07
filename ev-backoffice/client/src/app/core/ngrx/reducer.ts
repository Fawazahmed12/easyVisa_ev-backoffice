import { Action, ActionReducer, ActionReducerMap, MetaReducer } from '@ngrx/store';

import { State } from './state';

import * as fromUser from './user/user.reducers';
import * as fromConfigData from './config-data/config-data.reducers';
import * as fromEmailTemplates from './email-templates/email-templates.reducers';
import * as fromNotifications from './notifications/notifications.reducers';
import * as fromRepresentatives from './representatives/representatives.reducers';
import * as fromOrganizations from './organizations/organizations.reducers';
import * as fromPackages from './packages/packages.reducers';
import * as fromPayment from './payment/payment.reducers';
import * as fromFeeSchedule from './fee-schedule/fee-schedule.reducers';
import * as fromAuth from './auth/auth.reducers';
import * as fromAlerts from './alerts/alerts.reducers';
import * as fromTaxes from './taxes/taxes.reducers';
import * as fromUscisEditionDates from './uscis-edition-dates/uscis-edition-dates.reducers';
import * as fromUserRequest from './user-requests/reducer';
import * as fromAuthRequest from './auth-requests/reducer';
import * as fromConfigDataRequest from './config-data-requests/reducer';
import * as fromEmailTemplatesRequest from './email-templates-requests/reducer';
import * as fromNotificationsRequest from './notifications-requests/reducer';
import * as fromRepresentativesRequest from './representatives-requests/reducer';
import * as fromEmailsRequest from './emails-requests/reducer';
import * as fromPackagesRequest from './packages-requests/reducer';
import * as fromPaymentRequest from './payment-requests/reducer';
import * as fromOrganizationsRequest from './organizations-requests/reducer';
import * as fromFeeScheduleRequest from './fee-schedule-requests/reducer';
import * as fromAlertHandlingRequest from './alert-handling-requests/reducer';
import * as fromAlertsRequest from './alerts-requests/reducer';
import * as fromUscisEditionDatesRequest from './uscis-edition-dates-requests/reducer';
import * as fromTaxesRequest from './taxes-requests/reducer';

import { UserActionTypes } from './user/user.actions';

export const reducers: ActionReducerMap<State> = {
  User: fromUser.reducer,
  ConfigData: fromConfigData.reducer,
  EmailTemplates: fromEmailTemplates.reducer,
  Notifications: fromNotifications.reducer,
  Representatives: fromRepresentatives.reducer,
  Organizations: fromOrganizations.reducer,
  Packages: fromPackages.reducer,
  Payment: fromPayment.reducer,
  FeeSchedule: fromFeeSchedule.reducer,
  Auth: fromAuth.reducer,
  Alerts: fromAlerts.reducer,
  UscisEditionDates: fromUscisEditionDates.reducer,
  Taxes: fromTaxes.reducer,
  UserRequest: fromUserRequest.reducer,
  AuthRequest: fromAuthRequest.reducer,
  ConfigDataRequest: fromConfigDataRequest.reducer,
  EmailTemplatesRequest: fromEmailTemplatesRequest.reducer,
  NotificationsRequest: fromNotificationsRequest.reducer,
  RepresentativesRequest: fromRepresentativesRequest.reducer,
  EmailsRequests: fromEmailsRequest.reducer,
  PackagesRequest: fromPackagesRequest.reducer,
  PaymentRequest: fromPaymentRequest.reducer,
  OrganizationsRequest: fromOrganizationsRequest.reducer,
  FeeScheduleRequest: fromFeeScheduleRequest.reducer,
  AlertHandlingRequest: fromAlertHandlingRequest.reducer,
  AlertsRequest: fromAlertsRequest.reducer,
  UscisEditionDatesRequest: fromUscisEditionDatesRequest.reducer,
  TaxesRequest: fromTaxesRequest.reducer,
};

export function clearState(reducer: ActionReducer<State>): ActionReducer<State> {
  return function(state: State, action: Action): State {

    if (
      action.type === UserActionTypes.LogoutSuccess
      || action.type === UserActionTypes.LogoutFailure
      || action.type === UserActionTypes.DeleteUserSuccess
    ) {
      const I18n = {...state.I18n};
      state = {
        ...reducer(undefined, action),
        I18n,
      };
    }

    if (action.type === UserActionTypes.CancelMembershipSuccess) {
      const I18n = {...state.I18n};
      const User = {...state.User};
      const ConfigData = {...state.ConfigData};
      state = {
        ...reducer(undefined, action),
        User,
        ConfigData,
        I18n
      };
    }

    return reducer(state, action);
  };
}
export const metaReducers: MetaReducer<State>[] = [clearState];
