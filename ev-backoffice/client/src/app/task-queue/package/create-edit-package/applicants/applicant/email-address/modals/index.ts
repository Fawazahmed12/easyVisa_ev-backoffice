import { VerifyWithClientModalComponent } from './verify-with-client-modal/verify-with-client-modal.component';
import { GoodNewsModalComponent } from './good-news-modal/good-news-modal.component';
import { DifferentEmailRequiredModalComponent } from './different-email-required-modal/different-email-required-modal.component';
import { EmailFormatInvalidModalComponent } from './email-format-invalid-modal/email-format-invalid-modal.component';
import {
  ApplicantMemberOfBlockedPackageModalComponent
} from './applicant-member-of-blocked-package-modal/applicant-member-of-blocked-package-modal.component';
import {
  BeneficiaryMemberOfOpenPackageModalComponent
} from './beneficiary-member-of-open-package-modal/beneficiary-member-of-open-package-modal.component';

export const MODALS = [
  VerifyWithClientModalComponent,
  GoodNewsModalComponent,
  DifferentEmailRequiredModalComponent,
  EmailFormatInvalidModalComponent,
  ApplicantMemberOfBlockedPackageModalComponent,
  BeneficiaryMemberOfOpenPackageModalComponent,
];

export * from './verify-with-client-modal/verify-with-client-modal.component';
export * from './good-news-modal/good-news-modal.component';
export * from './different-email-required-modal/different-email-required-modal.component';
export * from './email-format-invalid-modal/email-format-invalid-modal.component';
export * from './applicant-member-of-blocked-package-modal/applicant-member-of-blocked-package-modal.component';
export * from './beneficiary-member-of-open-package-modal/beneficiary-member-of-open-package-modal.component';
