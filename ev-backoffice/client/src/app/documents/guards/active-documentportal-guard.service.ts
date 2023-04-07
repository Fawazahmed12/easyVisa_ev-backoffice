import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, } from '@angular/router';

import { Observable } from 'rxjs';
import { catchError, filter, switchMap, take, tap } from 'rxjs/operators';
import { of } from 'rxjs';
import { combineLatest, EMPTY } from 'rxjs';
import { fromPromise } from 'rxjs/internal-compatibility';
import { HttpErrorResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { DocumentsService } from '../services';
import { DocumentPortalAccessState } from '../models/documents.model';
import { ModalService, PackagesService, UserService } from '../../core/services';
import { ConfirmButtonType } from '../../core/modals/confirm-modal/confirm-modal.component';
import { Role } from '../../core/models/role.enum';
import { PackageStatus } from '../../core/models/package/package-status.enum';
import { NoPackageSelectModalComponent } from '../../core/modals/no-package-select-modal/no-package-select-modal.component';
import { QuestionnaireSyncStatus } from '../../core/models/package/questionnaire-sync-status.enum';
import { Package } from '../../core/models/package/package.model';
import { QuestionnaireAccessState } from '../../questionnaire/models/questionnaire.model';

@Injectable()
export class ActiveDocumentPortalGuardService implements CanActivate {

  constructor(
    private router: Router,
    private userService: UserService,
    private modalService: ModalService,
    private packagesService: PackagesService,
    private documentService: DocumentsService,
    private ngbModal: NgbModal
  ) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | boolean {
    const packageId = +route.params[ 'packageId' ];
    if (!packageId) {
      return this.openPackageWarningModal().pipe(
        switchMap(() => of(false))
      );
    }
    return this.documentService.getDocumentPortalAccessRequest(packageId).pipe(
      catchError((error: HttpErrorResponse) =>
        this.modalService.showErrorModal(error.error.errors || [ error.error ]).pipe(
          catchError(() => EMPTY),
          switchMap(() => fromPromise(this.router.navigate([ 'home' ])))
        )
      ),
      switchMap((data: DocumentPortalAccessState) => {
        this.packagesService.clearActivePackage();
        this.packagesService.setActivePackage(packageId);
        return combineLatest([
          of(data),
          this.packagesService.activePackage$,
          this.userService.hasAccess([ Role.ROLE_USER ])
        ]).pipe(
          filter(([ documentPortalAccessStateData, packageData, isUser ]) => !!packageData && !!documentPortalAccessStateData),
          switchMap(([ documentPortalAccessStateData, packageData, isUser ]: [DocumentPortalAccessState, Package, boolean]) => {
            const activePackage: Package = packageData as Package;
            this.showPackageStatusWarning(activePackage, isUser);
            return of(documentPortalAccessStateData.access);
          }),
          take(1)
        );
      })
    );
  }

  private showPackageStatusWarning(activePackage, isUser: Boolean) {
    switch (activePackage.status) {
      case PackageStatus.LEAD: {
        this.openDocumentPortalInactiveModal('TEMPLATE.QUESTIONNAIRE_DOCUMENTS_INACTIVE.LEAD')
          .subscribe();
        break;
      }
      case PackageStatus.CLOSED: {
        this.openDocumentPortalInactiveModal('TEMPLATE.QUESTIONNAIRE_DOCUMENTS_INACTIVE.CLOSED')
          .subscribe();
        break;
      }
      case PackageStatus.BLOCKED: {
        if (isUser) {
          this.openDocumentPortalInactiveModal('TEMPLATE.QUESTIONNAIRE_DOCUMENTS_INACTIVE.BLOCKED')
            .subscribe();
        }
        break;
      }
    }
  }

  private openPackageWarningModal() {
    this.ngbModal.open(NoPackageSelectModalComponent, { centered: true });
    return of(true);
  }

  private openDocumentPortalInactiveModal(bodyText) {
    return this.modalService.openConfirmModal({
      header: 'TEMPLATE.QUESTIONNAIRE_DOCUMENTS_INACTIVE.TITLE',
      body: bodyText,
      buttons: [
        {
          label: 'FORM.BUTTON.OK',
          type: ConfirmButtonType.Dismiss,
          className: 'btn btn-primary mr-2 min-w-100',
        },
      ],
      centered: true,
    }).pipe(
      catchError((err) => of(err))
    );
  }
}

