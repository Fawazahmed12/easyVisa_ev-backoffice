import { ClientSearchModalModule } from './client-search-modal/client-search-modal.module';
import { ChangeCaseStatusModalModule } from './change-case-status-modal/change-case-status-modal.module';
import { UpdatePackageStatusModalModule } from './update-package-status-modal/update-package-status-modal.module';
import { CannotTransferModalModule } from './cannot-transfer-modal/cannot-transfer-modal.module';
import { DeleteOldLeadsModalModule } from './delete-old-leads-modal/delete-old-leads-modal.module';
import { TransferCasesModalModule } from './transfer-cases-modal/transfer-cases-modal.module';
import { CannotDeletePackagesModalModule } from './cannot-delete-packages-modal/cannot-delete-packages-modal.module';
import { ConfirmDeletePackagesModalModule } from './confirm-delete-packages-modal/confirm-delete-packages-modal.module';



export const MODALS = [
  ClientSearchModalModule,
  CannotTransferModalModule,
  ChangeCaseStatusModalModule,
  DeleteOldLeadsModalModule,
  TransferCasesModalModule,
  UpdatePackageStatusModalModule,
  CannotDeletePackagesModalModule,
  ConfirmDeletePackagesModalModule,
];

export * from './cannot-transfer-modal/cannot-transfer-modal.module';
export * from './client-search-modal/client-search-modal.module';
export * from './change-case-status-modal/change-case-status-modal.module';
export * from './update-package-status-modal/update-package-status-modal.module';
export * from './delete-old-leads-modal/delete-old-leads-modal.module';
export * from './transfer-cases-modal/transfer-cases-modal.module';
export * from './cannot-delete-packages-modal/cannot-delete-packages-modal.module';
export * from './confirm-delete-packages-modal/confirm-delete-packages-modal.module';
