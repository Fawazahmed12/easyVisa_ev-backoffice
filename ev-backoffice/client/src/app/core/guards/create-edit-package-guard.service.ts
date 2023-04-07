import { Injectable } from '@angular/core';
import {
  Router,
  ActivatedRouteSnapshot,
  RouterStateSnapshot, CanDeactivate,
} from '@angular/router';
import { FormGroup } from '@angular/forms';

import { Observable } from 'rxjs';
import { catchError, take } from 'rxjs/operators';
import { of } from 'rxjs';

import { CreateApplicantFormGroupService } from '../../task-queue/package/services';
import { CreateEditPackageComponent } from '../../task-queue/package/create-edit-package/create-edit-package.component';

import { ModalService, UserService } from '../services';
import { ConfirmButtonType } from '../modals/confirm-modal/confirm-modal.component';


@Injectable()
export class CreateEditPackageGuardService implements CanDeactivate<CreateEditPackageComponent> {
  formGroup: FormGroup;

  constructor(
    private router: Router,
    private modalService: ModalService,
    private userService: UserService,
    private createApplicantFormGroupService: CreateApplicantFormGroupService,
  ) {
  }

  canDeactivate(
    component: CreateEditPackageComponent,
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> | Promise<boolean> | boolean {
    let isChanged = false;
    if (!!this.createApplicantFormGroupService.formGroup && this.createApplicantFormGroupService.formGroup.getRawValue()) {
      const formValue = this.createApplicantFormGroupService.formGroup.getRawValue();
      const {representativeId, ...valueToCheck} = formValue;
      isChanged = this.createApplicantFormGroupService.checkFormGroupChanges(valueToCheck);
    }

      if (this.createApplicantFormGroupService.canOut) {
        return true;
      } else if (isChanged) {
        return this.openPackageWarningModal()
        .pipe(
          take(1),
          catchError(() => of(true))
        );
      } else {
        return true;
      }
  }

  private openPackageWarningModal() {
    const buttons = [
      {
        label: 'FORM.BUTTON.CANCEL',
        type: ConfirmButtonType.Close,
        className: 'btn btn-primary mr-2 min-w-100',
        value: false
      },
      {
        label: 'FORM.BUTTON.OK',
        type: ConfirmButtonType.Dismiss,
        className: 'btn btn-primary mr-2 min-w-100',
        value: true
      },
    ];

    return this.modalService.openConfirmModal({
      header: 'TEMPLATE.TASK_QUEUE.PACKAGE.PACKAGE_CHANGES_DETECTED.HEADER',
      body: 'TEMPLATE.TASK_QUEUE.PACKAGE.PACKAGE_CHANGES_DETECTED.WARNING',
      buttons,
      centered: true,
      showCloseIcon: false,
      backdrop: 'static'
    });
  }
}

