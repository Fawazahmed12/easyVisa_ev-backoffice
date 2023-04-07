import { Injectable } from '@angular/core';
import {
  Router,
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
} from '@angular/router';

import { Observable } from 'rxjs';
import { catchError, filter, switchMap, take, tap } from 'rxjs/operators';
import { of } from 'rxjs';

import { ModalService, OrganizationService } from '../services';
import { ConfirmButtonType } from '../modals/confirm-modal/confirm-modal.component';


@Injectable()
export class RepresentativeSelectedGuardService implements CanActivate {

  constructor(
    private router: Router,
    private modalService: ModalService,
    private organizationService: OrganizationService,
  ) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | boolean {
    return this.organizationService.currentRepresentativeId$.pipe(
      filter(val => val !== undefined),
      switchMap((id) => {
        if (!id) {
          return this.modalService.openConfirmModal({
            header: 'TEMPLATE.REPRESENTATIVE_SELECTED_MODAL.TITLE',
            body: 'TEMPLATE.REPRESENTATIVE_SELECTED_MODAL.TEXT',
            centered: true,
            buttons: [
              {
                label: 'FORM.BUTTON.OK',
                type: ConfirmButtonType.Dismiss,
                className: 'btn btn-primary mr-2 min-w-100',
              },
            ],
          }).pipe(
            catchError(() => of(true)),
          );
        } else {
          return of(true);
        }
      }),
      take(1),
    );
  }
}

