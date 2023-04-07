import { HttpErrorResponse, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Action } from '@ngrx/store';

import { Package } from '../../models/package/package.model';
import { PackageStatus } from '../../models/package/package-status.enum';
import { PACKAGES } from './packages.state';
import { PackageModalType } from '../../../task-queue/models/package-modal-type.enum';

export interface PackagesRequestParamsModel {
  [param: string]: string | string[];

  alias?: string;
  order?: string;
  limit?: string;
  page?: string;
  search?: string;
}

export const PackagesActionTypes = {
  SelectPackageId: `[${PACKAGES}] Select PackageId`,
  GetPackage: `[${PACKAGES}] Get Package`,
  GetPackageSuccess: `[${PACKAGES}] Get Package Success`,
  GetPackageFailure: `[${PACKAGES}] Get Package Failure`,
  PostPackage: `[${PACKAGES}] Post Package`,
  PostPackageSuccess: `[${PACKAGES}] Post Package Success`,
  PostPackageFailure: `[${PACKAGES}] Post Package Failure`,
  PatchPackage: `[${PACKAGES}] Patch Package`,
  PatchPackageWithoutReminder: `[${PACKAGES}] Patch Package Without Reminder`,
  PatchPackageSuccess: `[${PACKAGES}] Patch Package Success`,
  PatchPackageFailure: `[${PACKAGES}] Patch Package Failure`,
  RemovePackage: `[${PACKAGES}] Remove Package`,
  GetPackages: `[${PACKAGES}] Get Packages`,
  GetPackagesSuccess: `[${PACKAGES}] Get Packages Success`,
  GetPackagesFailure: `[${PACKAGES}] Get Packages Failure`,
  RemovePackages: `[${PACKAGES}] Remove Packages`,
  ChangePackageStatus: `[${PACKAGES}] Change Package Status`,
  UpdatePackage: `[${PACKAGES}] Update Package`,
  GetActivePackage: `[${PACKAGES}] Get Active Package`,
  GetActivePackageSuccess: `[${PACKAGES}] Get Active Package Success`,
  GetActivePackageFailure: `[${PACKAGES}] Get Active Package Failure`,
  ClearActivePackage: `[${PACKAGES}] Clear Active Package`,
  SetActivePackageId: `[${PACKAGES}] Set Active Package Id`,
  OpenConflictBenefitCategoryModal: `[${PACKAGES}] Open Conflict Benefit Category Modal`,
  OpenRequestTransferSentModal: `[${PACKAGES}] Open Request Transfer Sent Modal`,
  GetApplicants: `[${PACKAGES}] Get Applicants`,
  OpenPackagesFailModals: `[${PACKAGES}] Open Packages Fail Modals`,
  DeletePackagesSuccess: `[${PACKAGES}] Delete Packages Success`,
  ChangePackageOwed:`[${PACKAGES}] Change Packages Owed`
};

export class SelectPackageId implements Action {
  readonly type = PackagesActionTypes.SelectPackageId;

  constructor(public payload: string) {
  }
}

export class GetPackage implements Action {
  readonly type = PackagesActionTypes.GetPackage;

  constructor(public payload: string) {
  }
}

export class GetPackageSuccess implements Action {
  readonly type = PackagesActionTypes.GetPackageSuccess;

  constructor(public payload: Package) {
  }
}

export class GetPackageFailure implements Action {
  readonly type = PackagesActionTypes.GetPackageFailure;

  constructor(public payload: any) {
  }
}

export class PostPackage implements Action {
  readonly type = PackagesActionTypes.PostPackage;

  constructor(public payload: Package) {
  }
}

export class PostPackageSuccess implements Action {
  readonly type = PackagesActionTypes.PostPackageSuccess;

  constructor(public payload: Package) {
  }
}

export class PostPackageFailure implements Action {
  readonly type = PackagesActionTypes.PostPackageFailure;

  constructor(public payload: any) {
  }
}

export class PatchPackage implements Action {
  readonly type = PackagesActionTypes.PatchPackage;

  constructor(public payload: Package) {
  }
}

export class PatchPackageWithoutReminder implements Action {
  readonly type = PackagesActionTypes.PatchPackageWithoutReminder;

  constructor(public payload: Package) {
  }
}

export class PatchPackageSuccess implements Action {
  readonly type = PackagesActionTypes.PatchPackageSuccess;

  constructor(public payload: Package) {
  }
}

export class PatchPackageFailure implements Action {
  readonly type = PackagesActionTypes.PatchPackageFailure;

  constructor(public payload: any) {
  }
}

export class RemovePackage implements Action {
  readonly type = PackagesActionTypes.RemovePackage;

  constructor() {
  }
}

export class GetPackages implements Action {
  readonly type = PackagesActionTypes.GetPackages;

  constructor(public payload: { params: PackagesRequestParamsModel; isShowModal?: boolean ; isEditPackagePage?: boolean}) {
  }
}

export class GetPackagesSuccess implements Action {
  readonly type = PackagesActionTypes.GetPackagesSuccess;

  constructor(public payload: HttpResponse<any>) {
  }
}

export class GetPackagesFailure implements Action {
  readonly type = PackagesActionTypes.GetPackagesFailure;

  constructor(public payload: any) {
  }
}

export class RemovePackages implements Action {
  readonly type = PackagesActionTypes.RemovePackages;
}

export class ChangePackageStatus implements Action {
  readonly type = PackagesActionTypes.ChangePackageStatus;

  constructor(public payload: { id: number; newStatus: PackageStatus }) {
  }
}
export class ChangePackageOwed implements Action {
  readonly type = PackagesActionTypes.ChangePackageOwed;
  constructor(public payload: { id: number; owed: number }) {
  }
}

export class UpdatePackage implements Action {
  readonly type = PackagesActionTypes.UpdatePackage;

  constructor(public payload: any) {
  }
}

export class GetActivePackage implements Action {
  readonly type = PackagesActionTypes.GetActivePackage;

  constructor(public payload: string | number) {
  }
}

export class GetActivePackageSuccess implements Action {
  readonly type = PackagesActionTypes.GetActivePackageSuccess;

  constructor(public payload: Package) {
  }
}

export class GetActivePackageFailure implements Action {
  readonly type = PackagesActionTypes.GetActivePackageFailure;

  constructor(public payload: any) {
  }
}

export class ClearActivePackage implements Action {
  readonly type = PackagesActionTypes.ClearActivePackage;
}

export class SetActivePackageId implements Action {
  readonly type = PackagesActionTypes.SetActivePackageId;

  constructor(public payload: number) {
  }
}

export class OpenConflictBenefitCategoryModal implements Action {
  readonly type = PackagesActionTypes.OpenConflictBenefitCategoryModal;

  constructor(public payload: any) {
  }
}

export class OpenRequestTransferSentModal implements Action {
  readonly type = PackagesActionTypes.OpenRequestTransferSentModal;

  constructor(public payload: any) {
  }
}

export class GetApplicants implements Action {
  readonly type = PackagesActionTypes.GetApplicants;

  constructor(public payload: number) {
  }
}

export class OpenPackagesFailModals implements Action {
  readonly type = PackagesActionTypes.OpenPackagesFailModals;

  constructor(public payload: {
      message?: string;
      text?: string;
      type?: PackageModalType;
  }) {
  }
}

export class DeletePackagesSuccess implements Action {
  readonly type = PackagesActionTypes.DeletePackagesSuccess;

  constructor(public payload: { deletedPackageIds: number[] }) {
  }
}

export type PackagesActionsUnion =
  | SelectPackageId
  | GetPackages
  | GetPackagesSuccess
  | GetPackagesFailure
  | RemovePackages
  | GetPackage
  | GetPackageSuccess
  | GetPackageFailure
  | PostPackage
  | PostPackageSuccess
  | PostPackageFailure
  | PatchPackage
  | PatchPackageSuccess
  | PatchPackageFailure
  | RemovePackage
  | ChangePackageStatus
  | ChangePackageOwed
  | UpdatePackage
  | GetActivePackage
  | GetActivePackageSuccess
  | GetActivePackageFailure
  | ClearActivePackage
  | SetActivePackageId
  | OpenConflictBenefitCategoryModal
  | OpenRequestTransferSentModal
  | GetApplicants
  | OpenPackagesFailModals
  | DeletePackagesSuccess;
