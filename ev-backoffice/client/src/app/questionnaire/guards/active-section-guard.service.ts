import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, } from '@angular/router';

import { combineLatest, EMPTY, Observable, of } from 'rxjs';
import { catchError, delay, filter, switchMap, take } from 'rxjs/operators';
import { fromPromise } from 'rxjs/internal-compatibility';
import { HttpErrorResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { QuestionnaireService } from '../services';
import { QuestionnaireAccessState } from '../models/questionnaire.model';
import { ModalService, PackagesService, UserService } from '../../core/services';
import { ConfirmButtonType } from '../../core/modals/confirm-modal/confirm-modal.component';
import { Role } from '../../core/models/role.enum';
import { PackageStatus } from '../../core/models/package/package-status.enum';
import { NoPackageSelectModalComponent } from '../../core/modals/no-package-select-modal/no-package-select-modal.component';
import { Package } from '../../core/models/package/package.model';
import { QuestionnaireSyncStatus } from '../../core/models/package/questionnaire-sync-status.enum';
import { PackageQuestionnaireSyncModalComponent } from '../../core/modals/package-questionnaire-sync-modal/package-questionnaire-sync-modal.component';

@Injectable()
export class ActiveSectionGuardService implements CanActivate {

  constructor(
    private router: Router,
    private userService: UserService,
    private modalService: ModalService,
    private packagesService: PackagesService,
    private questionnaireService: QuestionnaireService,
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
    return this.questionnaireService.getQuestionnaireAccessRequest(packageId).pipe(
      catchError((error: HttpErrorResponse) =>
        this.modalService.showErrorModal(error.error.errors || [error.error]).pipe(
          catchError(() => EMPTY),
          switchMap(() => fromPromise(this.router.navigate(['home'])))
        )
      ),
      switchMap((questionnaireAccessState) => {
        this.packagesService.clearActivePackage();
        this.packagesService.setActivePackage(packageId);
        return combineLatest([
          of(questionnaireAccessState),
          this.packagesService.activePackage$,
          this.userService.hasAccess([Role.ROLE_USER])
        ]).pipe(
          filter(([questionnaireAccessStateData, packageData, isUser]) => !!packageData && !!questionnaireAccessStateData),
          take(1),
          switchMap(([accessData, packageData, isUser]: [QuestionnaireAccessState, Package, boolean]) => {
            const activePackage: Package = packageData as Package;
            this.showPackageStatusWarning(activePackage, isUser);
            if (!accessData.access || activePackage.questionnaireSyncStatus != QuestionnaireSyncStatus.COMPLETED) {
              return of(false);
            }
            return this.questionnaireService.getSections(activePackage.id)
              .pipe(switchMap((sectionsData) => of(accessData.access)), delay(100));
          }),
          take(1)
        );
      })
    );
  }

  private showPackageStatusWarning(activePackage, isUser: Boolean) {
    switch (activePackage.status) {
      case PackageStatus.LEAD: {
        return this.openQuestionnaireInactiveModal('TEMPLATE.QUESTIONNAIRE_DOCUMENTS_INACTIVE.LEAD')
          .subscribe();
      }
      case PackageStatus.CLOSED: {
        return this.openQuestionnaireInactiveModal('TEMPLATE.QUESTIONNAIRE_DOCUMENTS_INACTIVE.CLOSED')
          .subscribe();
      }
      case PackageStatus.BLOCKED: {
        if (isUser) {
          return this.openQuestionnaireInactiveModal('TEMPLATE.QUESTIONNAIRE_DOCUMENTS_INACTIVE.BLOCKED')
            .subscribe();
        }
      }
    }

    if (activePackage.questionnaireSyncStatus != QuestionnaireSyncStatus.COMPLETED) {
      this.openQuestionnaireIncompleteSyncModal(activePackage);
    }
  }

  private openPackageWarningModal() {
    this.ngbModal.open(NoPackageSelectModalComponent, { centered: true });
    return of(true);
  }

  private openQuestionnaireInactiveModal(bodyText) {
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

  private openQuestionnaireIncompleteSyncModal(activePackage: Package) {
    const modalRef = this.ngbModal.open(PackageQuestionnaireSyncModalComponent, {
      centered: true,
    });
    modalRef.componentInstance.activePackageId = activePackage.id;
  }
}
