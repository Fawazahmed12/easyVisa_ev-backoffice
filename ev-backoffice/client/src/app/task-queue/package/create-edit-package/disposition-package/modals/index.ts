import { ConfirmNoConflictModalComponent } from './confirm-no-conflict-modal/confirm-no-conflict-modal.component';
import { DeleteNewPackageModalComponent } from './delete-new-package-modal/delete-new-package-modal.component';
import { InformationMissingModalComponent } from './information-missing-modal/information-missing-modal.component';
import { PackagePaymentFailedModalComponent } from './package-payment-failed-modal/package-payment-failed-modal.component';

export const MODALS = [
  ConfirmNoConflictModalComponent,
  DeleteNewPackageModalComponent,
  InformationMissingModalComponent,
  PackagePaymentFailedModalComponent,
];

export * from './confirm-no-conflict-modal/confirm-no-conflict-modal.component';
export * from './delete-new-package-modal/delete-new-package-modal.component';
export * from './information-missing-modal/information-missing-modal.component';
export * from './package-payment-failed-modal/package-payment-failed-modal.component';
