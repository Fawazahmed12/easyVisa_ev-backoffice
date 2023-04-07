import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, ActivatedRouteSnapshot, Params, Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

import { combineLatest, EMPTY, Observable, of, Subject } from 'rxjs';
import { catchError, filter, switchMap } from 'rxjs/operators';
import { find } from 'lodash-es';

import { QuestionnaireModel } from './models/questionnaire.model';
import { QuestionnaireService } from './services';
import { ActivePackageComponent } from '../components/active-package/active-package.component';
import { ModalService, NotificationsService, PackagesService } from '../core/services';
import { DestroySubscribers } from 'ngx-destroy-subscribers';

const BENEFICIARY = 'Beneficiary'; // ApplicantType enum has different value from server. So Here added a const for Beneficiary

@Component({
  selector: 'app-questionnaire',
  templateUrl: './questionnaire.component.html',
  styleUrls: ['./questionnaire.component.scss']
})
@DestroySubscribers()
export class QuestionnaireComponent implements OnInit, OnDestroy {

  packageSections$: Observable<QuestionnaireModel[]>;
  packageId: Observable<String>;
  sectionsGetRequest$;

  private navigationSubject$: Subject<boolean> = new Subject<boolean>();

  private subscribers: any = {};

  constructor(private activeRoute: ActivatedRoute,
              private router: Router, private modalService: ModalService,
              private questionnaireService: QuestionnaireService,
              private notificationsService: NotificationsService,
              private packagesService: PackagesService) {
  }

  ngOnInit() {
    this.sectionsGetRequest$ = this.questionnaireService.sectionsGetRequest$;
    this.notificationsService.showComponent$.next(ActivePackageComponent);
    this.packageSections$ = combineLatest([
      this.questionnaireService.questionnaireSections$,
      this.packagesService.activePackageId$
    ]).pipe(
      filter(([packageSections, activePackageId]) => !!packageSections && !!activePackageId),
      switchMap(([packageSections, activePackageId]) => {
        this.loadDefaultSection(packageSections, activePackageId);
        return of(packageSections as QuestionnaireModel[]);
      }),
      catchError((error: HttpErrorResponse) => {
          this.modalService.showErrorModal(error.error.errors || [error.error]);
          return EMPTY;
        }
      )
    );


    this.subscribers.navigationSubscription = combineLatest([this.navigationSubject$,
      this.questionnaireService.hasPendingAnswerSaveRequest$])
      .pipe(
        filter(([navigation, hasPendingAnswerSaveRequest]) => !!navigation && !hasPendingAnswerSaveRequest)
      )
      .subscribe(([navigation, hasPendingAnswerSaveRequest]) => {
        const snapShot: ActivatedRouteSnapshot = this.activeRoute.snapshot;
        const navigationData: any = this.questionnaireService.getNavigationData();
        const routeParams: Params = snapShot.params;
        this.questionnaireService.fetchSections(routeParams.packageId);
        this.navigationSubject$.next(false)
        this.router.navigate(['questionnaire', 'package', routeParams.packageId, 'applicants',
          navigationData.applicantId, 'sections', navigationData.sectionId]);
      });
  }

  /* To load the default(first) questionnaire section data (questions and answer)*/
  loadDefaultSection(packageSections: QuestionnaireModel[], activePackageId) {
    const matchedPackageSection = find(packageSections, function(packageSection) {
      return packageSection.sections && packageSection.sections.length;
    });
    if (this.activeRoute.children.length === 0 && matchedPackageSection) {
      const section = matchedPackageSection.sections[ 0 ];
      this.router.navigate(
        ['questionnaire', 'package', activePackageId, 'applicants', matchedPackageSection.applicantId, 'sections', section.id]
      );
    }
  }

  hasDerivedBeneficiary(packageSection) {
    return packageSection.applicantType === BENEFICIARY && !packageSection.direct;
  }

  onSectionSelect(applicantId: number, sectionId: string): void {
    const navigationData = { applicantId, sectionId };
    this.questionnaireService.setNavigationData(navigationData);
    this.navigationSubject$.next(true);
  }

  isActive(applicantId: number, sectionId: string): boolean {
    const snapShot: ActivatedRouteSnapshot = this.activeRoute.snapshot;
    const routeParams: Params = snapShot.firstChild?.params;
    return routeParams?.applicantId == applicantId && routeParams?.sectionId == sectionId;
  }

  ngOnDestroy() {
    this.notificationsService.showComponent$.next(null);
    this.cleanupSubscriptions();
    console.log(`${this.constructor.name} Destroys`);
  }

  cleanupSubscriptions() {
    if (this.subscribers.navigationSubscription) {
      this.subscribers.navigationSubscription.unsubscribe();
      this.subscribers.navigationSubscription = null;
    }
  }
}
