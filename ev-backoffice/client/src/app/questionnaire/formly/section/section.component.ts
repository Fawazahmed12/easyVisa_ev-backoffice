import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, ActivatedRouteSnapshot, Params, Router } from '@angular/router';

import { FormlyFieldConfig, FormlyFormOptions } from '@ngx-formly/core';

import { combineLatest, concat, EMPTY, Observable, of, Subject, zip } from 'rxjs';
import {
  catchError,
  concatMap,
  delay,
  distinctUntilChanged,
  filter,
  map,
  publishReplay,
  refCount,
  switchMap,
  tap
} from 'rxjs/operators';
import { DestroySubscribers } from 'ngx-destroy-subscribers';
import { isEqual } from 'lodash-es';

import { QuestionnaireService } from '../../services';
import {
  Answer,
  QuestionnaireStateModel,
  RepeatGroupModel,
  Section,
  SectionWarningModel
} from '../../models/questionnaire.model';
import { PackagesService } from '../../../core/services';
import { ModalService } from '../../../core/services/modal.service';
import { FocusManagerService } from '../../services/focusmanager.service';


@Component({
  selector: 'app-section',
  templateUrl: './section.component.html',
  styles: [
    `
      .applicant-name {
        word-break: break-word;
      }
    `
  ]
})
@DestroySubscribers()
export class SectionComponent implements OnInit, OnDestroy {
  form = new FormGroup({});
  answer$: Observable<Answer>;
  fields$: Observable<FormlyFieldConfig[]>;
  options: FormlyFormOptions = {
    formState: {}
  };
  hasQuestionAnswerLoaded = false;
  requestDelay = 50;

  activePackageId$: Observable<number>;
  selectedSection$: Observable<Section>;
  selectedQuestionnaire$: Observable<QuestionnaireStateModel>;

  private routeParamsTransformed$: Observable<{ applicantId: number; sectionId: string }>;
  private navigationSubject$: Subject<boolean> = new Subject<boolean>();
  private subscribers: any = {};

  private questionnaireSections: any = [];

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private questionnaireService: QuestionnaireService,
    private packagesService: PackagesService,
    private modalService: ModalService,
    private focusManagerService: FocusManagerService
  ) {
  }

  ngOnInit() {
    // One variable is used due to simultaneous emit for applicantId and sectionId
    this.routeParamsTransformed$ = this.activatedRoute.params.pipe(
      map((params) => this.transformRouteParams(params)),
      filter((params) => !Number.isNaN(params.applicantId)),
      distinctUntilChanged(isEqual),
    );

    this.selectedSection$ = this.routeParamsTransformed$.pipe(
      tap((params) => {
        this.hasQuestionAnswerLoaded = false;
      }),
      map((params) => params.sectionId),
      switchMap((sectionId) => this.questionnaireService.getSection(sectionId))
    );

    this.activePackageId$ = this.packagesService.activePackageId$;

    this.selectedQuestionnaire$ = this.routeParamsTransformed$.pipe(
      map((params) => params.applicantId),
      switchMap((applicantId) => this.questionnaireService.getQuestionnaireItem(applicantId))
    );

    this.fields$ = combineLatest([
      this.routeParamsTransformed$,
      this.activePackageId$,
    ]).pipe(
      filter(([routeParams, packageId]) => !!packageId),
      switchMap(([routeParams, packageId]) => {
        const { applicantId, sectionId } = routeParams;
        this.questionnaireService.fetchQuestions(packageId, applicantId, sectionId);
        return this.questionnaireService.formlyQuestionnaire$;
      }));

    this.answer$ = combineLatest([
      this.routeParamsTransformed$,
      this.activePackageId$,
    ]).pipe(
      filter(([routeParams, packageId]) => !!packageId),
      switchMap(([routeParams, packageId]) => {
        const { applicantId, sectionId } = routeParams;
        this.questionnaireService.fetchAnswers(packageId, applicantId, sectionId);
        this.focusManagerService.setPreviousAnswerState({});
        return this.questionnaireService.formlyAnswerSubject.asObservable();
      }));


    // hasQuestionAnswerLoaded need to be set to true after both fields and answers are received.
    this.subscribers.questionAnswerRequestSubscription = zip(
      this.questionnaireService.questionsGetRequest(),
      this.questionnaireService.answersGetRequest()
    ).pipe(this.catchAndShare())
      .subscribe((data) => {
        this.hasQuestionAnswerLoaded = true;
      });


    this.subscribers.answerRequestItemSubscription = combineLatest([
      this.routeParamsTransformed$,
      this.activePackageId$,
      this.questionnaireService.answerSaveRequestItem$,
    ]).pipe(
      filter(([routeParams, packageId, answerSaveRequestItem]) => !!answerSaveRequestItem),
      map(([routeParams, packageId, answerSaveRequestItem]) => ({ packageId, ...routeParams, ...answerSaveRequestItem })),
      concatMap(args => concat(
        of(args),                           // emit first item right away
        EMPTY.pipe(delay(this.requestDelay)),   // delay next item
      )),
    ).subscribe((answerModel) => {
      this.questionnaireService.saveAnswer(answerModel);
    });


    this.subscribers.answerValidationRequestSubscription = combineLatest([
      this.routeParamsTransformed$,
      this.activePackageId$,
      this.questionnaireService.answerValidationRequestItem$,
    ]).pipe(
      filter(([routeParams, packageId, answerValidationRequestItem]) => !!answerValidationRequestItem),
      map(([routeParams, packageId, answerValidationRequestItem]) => ({ packageId, ...routeParams, ...answerValidationRequestItem })),
    ).subscribe((answerModel) => {
      this.questionnaireService.validateAnswer(answerModel);
    });


    this.subscribers.addRepeatGroupSubscription = combineLatest([
      this.routeParamsTransformed$,
      this.activePackageId$,
      this.questionnaireService.addRepeatGroupRequestItem$,
    ]).pipe(
      filter(([routeParams, packageId, addRepeatGroupRequestItem]) => !!addRepeatGroupRequestItem),
      map(([routeParams, packageId, addRepeatGroupRequestItem]) => {
        this.addRepeatGroup(routeParams.sectionId, routeParams.applicantId, packageId, addRepeatGroupRequestItem);
      }),
    ).subscribe((data) => {
      console.log(' ');
    });


    this.subscribers.removeRepeatGroupSubscription = combineLatest([
      this.routeParamsTransformed$,
      this.activePackageId$,
      this.questionnaireService.removeRepeatGroupRequestItem$,
    ]).pipe(
      filter(([routeParams, packageId, removeRepeatGroupRequestItem]) => !!removeRepeatGroupRequestItem),
      map(([routeParams, packageId, removeRepeatGroupRequestItem]) => {
        this.removeRepeatGroup(routeParams.sectionId, routeParams.applicantId, packageId, removeRepeatGroupRequestItem);
      }),
    ).subscribe((data) => {
      console.log(' ');
    });

    this.subscribers.questionnaireItemsSubscription = this.questionnaireService.questionnaireItems$
      .subscribe((questionnaireItemsList: QuestionnaireStateModel[]) => {
        this.questionnaireSections = [];
        const questionnaireStateModelList: QuestionnaireStateModel[] = questionnaireItemsList || [];
        questionnaireStateModelList.forEach((questionnaireItem: QuestionnaireStateModel) => {
          const { applicantId, applicantName, applicantType } = questionnaireItem;
          const questionnaireModel = { applicantId, applicantName, applicantType };
          const questionnaireItemSections = questionnaireItem.sections.map((sectionId) => ({ ...questionnaireModel, sectionId }));
          this.questionnaireSections.push(...questionnaireItemSections);
        });
      });


    this.subscribers.answerValidationSubscription = combineLatest([
      this.routeParamsTransformed$,
      this.activePackageId$,
      this.questionnaireService.answerValidationData$,
    ]).pipe(
      filter(([routeParams, packageId, answerValidationData]) => !!answerValidationData),
      distinctUntilChanged((prev, curr) => {
        const prevAnswerValidationModel = (prev != null && prev.length) ? prev[ 2 ] : null;
        const currentAnswerValidationModel = (curr != null && curr.length) ? curr[ 2 ] : null;
        return prevAnswerValidationModel === currentAnswerValidationModel;
      }),
    ).subscribe(([routeParams, packageId, answerValidationModel]) => {
      if (answerValidationModel.hasValidAnswer) {
        const answerModel = { packageId, ...routeParams, ...answerValidationModel };
        this.questionnaireService.saveAnswer(answerModel);
      } else {
        this.modalService.showErrorModal(answerValidationModel.errorMessage);
      }
    });


    this.subscribers.navigationSubscription = combineLatest([this.navigationSubject$,
      this.questionnaireService.hasPendingAnswerSaveRequest$])
      .pipe(
        filter(([navigation, hasPendingAnswerSaveRequest]) => !!navigation && !hasPendingAnswerSaveRequest)
      )
      .subscribe(([navigation, hasPendingAnswerSaveRequest]) => {
        const snapShot: ActivatedRouteSnapshot = this.activatedRoute.snapshot;
        const navigationData: any = this.questionnaireService.getNavigationData();
        const routeParams: Params = snapShot.parent.params;
        this.navigationSubject$.next(false);
        this.questionnaireService.fetchSections(routeParams.packageId);
        this.router.navigate(['questionnaire', 'package', routeParams.packageId, 'applicants',
          navigationData.applicantId, 'sections', navigationData.sectionId]);
      });
  }

  addRepeatGroup(sectionId, applicantId, packageId, addRepeatGroupRequestItem) {
    const repeatGroupModel: RepeatGroupModel = {
      ...addRepeatGroupRequestItem,
      sectionId,
      applicantId,
      packageId,
    };
    this.questionnaireService.addRepeatGroup(repeatGroupModel);
  }


  removeRepeatGroup(sectionId, applicantId, packageId, removeRepeatGroupRequestItem) {
    const repeatGroupModel: RepeatGroupModel = {
      ...removeRepeatGroupRequestItem,
      sectionId,
      applicantId,
      packageId,
    };
    this.questionnaireService.removeRepeatGroup(repeatGroupModel);
  }

  catchAndShare<T>(navigateTo: string[ ] = ['questionnaire']) {
    return (obs: Observable<T>) => obs.pipe(
      catchError((error) => {
        this.router.navigate(navigateTo);
        this.modalService.showErrorModal(error.error.errors || [error.error]);
        return EMPTY;
      }),
      publishReplay(1),
      refCount(),
    );
  }

  transformRouteParams(params: any) {
    return {
      applicantId: Number.parseInt(params[ 'applicantId' ], 10),
      sectionId: params[ 'sectionId' ],
    };
  }

  canDisableNextButton() {
    const currentQuestionnaireSectionIndex = this.getCurrentQuestionnaireSectionIndex();
    return ((currentQuestionnaireSectionIndex + 1) === this.questionnaireSections.length);
  }


  canDisablePreviousButton() {
    const currentQuestionnaireSectionIndex = this.getCurrentQuestionnaireSectionIndex();
    return (currentQuestionnaireSectionIndex === 0);
  }


  onNextSection() {
    const currentQuestionnaireSectionIndex = this.getCurrentQuestionnaireSectionIndex();
    const nextQuestionnaireSection = this.questionnaireSections[ currentQuestionnaireSectionIndex + 1 ];
    this.navigateToSelectedSection(nextQuestionnaireSection);
  }


  onPreviousSection() {
    const currentQuestionnaireSectionIndex = this.getCurrentQuestionnaireSectionIndex();
    const previousQuestionnaireSection = this.questionnaireSections[ currentQuestionnaireSectionIndex - 1 ];
    this.navigateToSelectedSection(previousQuestionnaireSection);
  }


  getCurrentQuestionnaireSectionIndex() {
    const snapShot: ActivatedRouteSnapshot = this.activatedRoute.snapshot;
    const routeParams: Params = snapShot.params;
    const currentQuestionnaireSectionIndex = this.questionnaireSections.findIndex((data) => (data.sectionId === routeParams.sectionId && data.applicantId === parseInt(routeParams.applicantId, 10)));
    return currentQuestionnaireSectionIndex;
  }

  navigateToSelectedSection(questionnaireSection) {
    if (questionnaireSection) {
      const navigationData = {
        applicantId: questionnaireSection.applicantId,
        sectionId: questionnaireSection.sectionId
      };
      this.questionnaireService.setNavigationData(navigationData);
      this.navigationSubject$.next(true);
    }
  }

  public getSectionWarningData(): Observable<SectionWarningModel> {
    return this.activePackageId$.pipe(switchMap(packageId => {
        const currentQuestionnaireSectionIndex = this.getCurrentQuestionnaireSectionIndex();
        const currentQuestionnaireSection = this.questionnaireSections[ currentQuestionnaireSectionIndex ];
        return this.questionnaireService.sectionWarningGetRequest(
          packageId,
          currentQuestionnaireSection.applicantId, currentQuestionnaireSection.sectionId
        );
      })
    );
  }


  cleanupSubscriptions() {
    if (this.subscribers.answerRequestItemSubscription) {
      this.subscribers.answerRequestItemSubscription.unsubscribe();
      this.subscribers.answerRequestItemSubscription = null;
    }

    if (this.subscribers.answerValidationRequestSubscription) {
      this.subscribers.answerValidationRequestSubscription.unsubscribe();
      this.subscribers.answerValidationRequestSubscription = null;
    }

    if (this.subscribers.addRepeatGroupSubscription) {
      this.subscribers.addRepeatGroupSubscription.unsubscribe();
      this.subscribers.addRepeatGroupSubscription = null;
    }

    if (this.subscribers.removeRepeatGroupSubscription) {
      this.subscribers.removeRepeatGroupSubscription.unsubscribe();
      this.subscribers.removeRepeatGroupSubscription = null;
    }

    if (this.subscribers.questionnaireItemsSubscription) {
      this.subscribers.questionnaireItemsSubscription.unsubscribe();
      this.subscribers.questionnaireItemsSubscription = null;
    }

    if (this.subscribers.questionAnswerRequestSubscription) {
      this.subscribers.questionAnswerRequestSubscription.unsubscribe();
      this.subscribers.questionAnswerRequestSubscription = null;
    }

    if (this.subscribers.answerValidationSubscription) {
      this.subscribers.answerValidationSubscription.unsubscribe();
      this.subscribers.answerValidationSubscription = null;
    }

    if (this.subscribers.navigationSubscription) {
      this.subscribers.navigationSubscription.unsubscribe();
      this.subscribers.navigationSubscription = null;
    }
  }


  ngOnDestroy() {
    this.cleanupSubscriptions();
    console.log(`${this.constructor.name} Destroys`);
  }

}
