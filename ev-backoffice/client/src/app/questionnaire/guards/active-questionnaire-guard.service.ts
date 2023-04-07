import { Injectable } from '@angular/core';
import { ActivatedRoute, ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, } from '@angular/router';
import { Observable, of } from 'rxjs';
import { catchError, filter, map, switchMap, take } from 'rxjs/operators';

import { QuestionnaireService } from '../services';
import { ModalService, UserService } from '../../core/services';
import { ConfirmButtonType } from '../../core/modals/confirm-modal/confirm-modal.component';
import { QuestionnaireModel, Section } from '../models/questionnaire.model';
import { Role } from '../../core/models/role.enum';


@Injectable()
export class ActiveQuestionnaireGuardService implements CanActivate {

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private questionnaireService: QuestionnaireService,
    private modalService: ModalService,
    private userService: UserService,
  ) {
  }


  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | boolean {
    return this.questionnaireService.questionnaireSections$.pipe(
      filter((data) => !!data),
      switchMap((data: QuestionnaireModel[]) => {
        const packageSections: QuestionnaireModel[] = data || [];
        const requestedApplicantId: number = parseInt(route.params.applicantId, 10);
        const packageSectionData: QuestionnaireModel = packageSections.find(
          applicantData => applicantData.applicantId === requestedApplicantId
        );
        if (!packageSectionData) {
          this.openPackageWarningModal('ACTIVE_QUESTIONNAIRE_APPLICANT_MODAL');
          return of(false);
        }
        const requestedSectionId: string = route.params.sectionId;
        const applicantPackageSections: Section[] = packageSectionData.sections || [];
        const requestedSection: Section = applicantPackageSections.find(sectionData => sectionData.id === requestedSectionId);
        if (!requestedSection) {
          this.openPackageWarningModal('ACTIVE_QUESTIONNAIRE_SECTION_MODAL');
          return of(false);
        }
        return of(true);
      }),
      take(1),
      catchError((err) => of(false))
    );
  }


  private openPackageWarningModal(warningModalName) {
    this.modalService.openConfirmModal({
      header: `TEMPLATE.${warningModalName}.TITLE`,
      body: `TEMPLATE.${warningModalName}.TEXT`,
      buttons: [
        {
          label: 'FORM.BUTTON.OK',
          type: ConfirmButtonType.Dismiss,
          className: 'btn btn-primary mr-2 min-w-100'
        },
      ],

      centered: true,
    }).pipe(
      catchError((err) => of(err)),
      switchMap((data) => this.userService.hasAccess([ Role.ROLE_USER ])),
      map((isUser) => {
        if (isUser) {
          return this.router.navigate([ 'dashboard', 'progress-status' ]);
        } else {
          return this.router.navigate([ 'dashboard', 'financial' ]);
        }
      }),
    ).subscribe();
  }
}
