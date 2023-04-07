import { CannotConvertPackageModalModule } from './cannot-convert-package-modal/cannot-convert-package-modal.module';
import { ConfirmModalModule } from './confirm-modal/confirm-modal.module';
import { EmailTemplateVariablesModalModule } from './email-template-variables-modal/email-template-variables-modal.module';
import { LoginModalModule } from './login-modal/login-modal.module';
import { PackageCannotBeOpenModalModule } from './package-cannot-be-open-modal/package-cannot-be-open-modal.module';
import { BenefitCategoryConflictModalModule } from './benefit-category-conflict-modal/benefit-category-conflict-modal.module';
import { MembersOfBlockedOrOpenPackageModalModule } from './members-of-blocked-or-open-package-modal/members-of-blocked-or-open-package-modal.module';
import { ReminderInvitationRegisterModalModule } from './reminder-invitation-register-modal/reminder-invitation-register-modal.module';
import { ReminderApplicantPermissionModalModule } from './reminder-applicant-permission-modal/reminder-applicant-permission-modal.module';
import { PaymentFailedModalModule } from './payment-failed-modal/payment-failed-modal.module';
import { NoResultsModalModule } from './no-results-modal/no-results-modal.module';
import { NoPackageSelectModalModule } from './no-package-select-modal/no-package-select-modal.module';
import { PackageQuestionnaireSyncModalModule } from './package-questionnaire-sync-modal/package-questionnaire-sync-modal.module';


export const MODALS = [
  CannotConvertPackageModalModule,
  ConfirmModalModule,
  EmailTemplateVariablesModalModule,
  LoginModalModule,
  PackageCannotBeOpenModalModule,
  BenefitCategoryConflictModalModule,
  MembersOfBlockedOrOpenPackageModalModule,
  ReminderInvitationRegisterModalModule,
  ReminderApplicantPermissionModalModule,
  PaymentFailedModalModule,
  NoResultsModalModule,
  NoPackageSelectModalModule,
  PackageQuestionnaireSyncModalModule
];

export * from './cannot-convert-package-modal/cannot-convert-package-modal.module';
export * from './confirm-modal/confirm-modal.module';
export * from './email-template-variables-modal/email-template-variables-modal.module';
export * from './login-modal/login-modal.module';
export * from './package-cannot-be-open-modal/package-cannot-be-open-modal.module';
export * from './benefit-category-conflict-modal/benefit-category-conflict-modal.module';
export * from './reminder-invitation-register-modal/reminder-invitation-register-modal.module';
export * from './reminder-applicant-permission-modal/reminder-applicant-permission-modal.module';
export * from './payment-failed-modal/payment-failed-modal.module';
export * from './no-results-modal/no-results-modal.module';
export * from './no-package-select-modal/no-package-select-modal.module';
export * from './package-questionnaire-sync-modal/package-questionnaire-sync-modal.module';
