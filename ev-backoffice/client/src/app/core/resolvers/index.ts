import { FeeDetailsResolverService } from './fee-details-resolver.service';
import { EmailTemplatesResolverService } from './email-templates-resolver.service';
import { MyPaymentMethodResolverService } from './my-payment-method-resolver.service';
import { MyBalanceResolverService } from './my-balance-resolver.service';
import { MyAccountTransactionsResolverService } from './my-account-transactions-resolver.service';
import { FeeScheduleSettingsResolverService } from './fee-schedule-settings-resolver.service';
import { GovernmentFeeResolverService } from './government-fee-resolver.service';
import { FeeScheduleResolverService } from './fee-schedule-resolver.service';
import { AlertsResolverService } from './alerts-resolver.service';
import { AffiliatedOrganizationsResolverService } from './affiliated-organizations-resolver.service';
import { BenefitsResolverService } from './benefits-resolver.service';
import { ClientNotificationsResolverService } from './client-notifications-resolver.service';

export const RESOLVERS = [
  FeeDetailsResolverService,
  EmailTemplatesResolverService,
  MyPaymentMethodResolverService,
  MyBalanceResolverService,
  MyAccountTransactionsResolverService,
  FeeScheduleSettingsResolverService,
  GovernmentFeeResolverService,
  FeeScheduleResolverService,
  AlertsResolverService,
  AffiliatedOrganizationsResolverService,
  BenefitsResolverService,
  ClientNotificationsResolverService
];
