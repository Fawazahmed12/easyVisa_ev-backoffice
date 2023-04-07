import { CookieService } from 'ngx-cookie-service';

import { UserService } from './user.service';
import { AuthService } from './auth.service';
import { ModalService } from './modal.service';
import { ConfigDataService } from './config-data.service';
import { EmailsService } from './emails.service';
import { EmailTemplatesService } from './email-templates.service';
import { OrganizationService } from './organization.service';
import { PackagesService } from './packages.service';
import { NotificationsService } from './notifications.service';
import { FattService } from './fatt.service';
import { PaymentService } from './payment.service';
import { FeeScheduleService } from './fee-schedule.service';
import { UscisEditionDatesService } from './uscis-edition-dates.service';
import { TaxesService } from './taxes.service';
import { XsrfAppLoadService } from './xsrf-app-load.service';
import { PaginationService } from './pagination.service';

export const PROVIDERS = [
  CookieService,
  PaymentService,
  UserService,
  AuthService,
  ModalService,
  ConfigDataService,
  EmailsService,
  EmailTemplatesService,
  NotificationsService,
  OrganizationService,
  PackagesService,
  FattService,
  FeeScheduleService,
  UscisEditionDatesService,
  TaxesService,
  XsrfAppLoadService,
  PaginationService
];

export * from './user.service';
export * from './auth.service';
export * from './config-data.service';
export * from './emails.service';
export * from './email-templates.service';
export * from './notifications.service';
export * from './organization.service';
export * from './packages.service';
export * from './payment.service';
export * from './modal.service';
export * from './fatt.service';
export * from './fee-schedule.service';
export * from './uscis-edition-dates.service';
export * from './taxes.service';
export * from './xsrf-app-load.service';
export * from './pagination.service';
