import { AuthServiceMock } from './auth-service.mock';
import { ConfigDataServiceMock } from './config-data-service.mock';
import { FattServiceMock } from './fatt-service.mock';
import { I18nServiceMock } from './i18n-service.mock';
import { ModalServiceMock } from './modal-service.mock';
import { NotificationsServiceMock } from './notifications-service.mock';
import { OrganizationServiceMock } from './organization-service.mock';
import { PackagesServiceMock } from './packages-service.mock';
import { PaymentServiceMock } from './payment-service.mock';
import { SignUpServiceMock } from './sign-up-service.mock';
import { TaxesServiceMock } from './taxes-service.mock';
import { UserServiceMock } from './user-service.mock';
import { XsrfAppLoadServiceMock } from './xsrf-app-load-service.mock';
import { RetrieveCredentialServiceMock } from './retrieve-credential-service.mock';
import { FeeScheduleSettingsServiceMock } from './fee-schedule-settings-service.mock';


export const SERVICE_MOCKS = [
  AuthServiceMock,
  ConfigDataServiceMock,
  FattServiceMock,
  I18nServiceMock,
  ModalServiceMock,
  NotificationsServiceMock,
  OrganizationServiceMock,
  PackagesServiceMock,
  PaymentServiceMock,
  SignUpServiceMock,
  TaxesServiceMock,
  UserServiceMock,
  XsrfAppLoadServiceMock,
  RetrieveCredentialServiceMock,
  FeeScheduleSettingsServiceMock
];

export * from './auth-service.mock';
export * from './config-data-service.mock';
export * from './fatt-service.mock';
export * from './i18n-service.mock';
export * from './modal-service.mock';
export * from './notifications-service.mock';
export * from './organization-service.mock';
export * from './packages-service.mock';
export * from './payment-service.mock';
export * from './sign-up-service.mock';
export * from './taxes-service.mock';
export * from './user-service.mock';
export * from './xsrf-app-load-service.mock';
export * from './retrieve-credential-service.mock';
export * from './fee-schedule-settings-service.mock';
