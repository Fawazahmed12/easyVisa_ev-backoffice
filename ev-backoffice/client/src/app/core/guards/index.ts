import { RegistrationFinishGuardService } from './registration-finish-guard.service';
import { RoleGuardService } from './role-guard.service';
import { ActivePackageGuardService } from './active-package-guard.service';
import { RoleAndAdminGuardService } from './role-and-admin-guard.service';
import { ActiveMembershipGuardService } from './active-membership-guard.service';
import { UnpaidGuardService } from './unpaid-guard.service';
import { PositionGuardService } from './position-guard.service';
import { CreateEditPackageGuardService } from './create-edit-package-guard.service';
import { SubmitArticleGuardService } from './submit-article-guard.service';
import { DashboardGuardService } from './dashboard-guard.service';
import { RepresentativeSelectedGuardService } from './representative-selected-guard.service';
import { ConvertToAttorneyGuardService } from './convert-to-attorney-guard.service';
import { TaskQueueGuardService } from './task-queue-guard.service';
import { FinancialPositionGuardService } from './financial-position-guard.service';
import { StoreUrlGuardService } from './store-url-guard.service';

export const GUARD_PROVIDERS = [
  RegistrationFinishGuardService,
  RoleGuardService,
  ActivePackageGuardService,
  RoleAndAdminGuardService,
  ActiveMembershipGuardService,
  UnpaidGuardService,
  PositionGuardService,
  CreateEditPackageGuardService,
  SubmitArticleGuardService,
  DashboardGuardService,
  RepresentativeSelectedGuardService,
  ConvertToAttorneyGuardService,
  TaskQueueGuardService,
  FinancialPositionGuardService,
  StoreUrlGuardService,
];

export * from './registration-finish-guard.service';
