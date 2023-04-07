import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { QuestionnaireSyncStatus } from '../../models/package/questionnaire-sync-status.enum';
import { ModalService, PackagesService, UserService } from '../../services';
import { EMPTY, Subject, timer } from 'rxjs';
import { catchError, filter, retry, switchMap, takeUntil } from 'rxjs/operators';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { DestroySubscribers } from 'ngx-destroy-subscribers';
import { Role } from '../../models/role.enum';

@Component({
  selector: 'package-questionnaire-sync-modal',
  templateUrl: './package-questionnaire-sync-modal.component.html'
})
@DestroySubscribers()
export class PackageQuestionnaireSyncModalComponent implements OnInit, OnDestroy {

  @Input() activePackageId: number;
  activePackage: any;
  retryCount = 0;
  maxRetryCount = 1;

  public questionnaireSyncStatus = QuestionnaireSyncStatus;
  private stopPolling$: Subject<boolean> = new Subject<boolean>();
  private retryQuestionnaireSync$: Subject<boolean> = new Subject<boolean>();
  private navigateFromQuestionnaireSyncFailure$: Subject<boolean> = new Subject<boolean>();
  private subscribers: any = {};

  constructor(public activeModal: NgbActiveModal,
              private router: Router,
              private http: HttpClient,
              private packagesService: PackagesService,
              private userService: UserService,
              private modalService: ModalService) {
  }

  ngOnInit() {
    this.initiateProgressPolling();

    this.subscribers.retryQuestionnaireSyncSubscription = this.retryQuestionnaireSync$
      .pipe(filter((data) => !!data),
        switchMap(() => {
          const syncQuestionnaireAnswersUrl = `/questionnaire/sync-answers`;
          return this.http.post(syncQuestionnaireAnswersUrl, { packageId: this.activePackageId });
        }))
      .subscribe(
        (data => this.initiateProgressPolling()),
        (error => this.showErrorModalWithResponse())
      );

    this.subscribers.navigateFromQuestionnaireSyncFailureSubscription = this.navigateFromQuestionnaireSyncFailure$
      .pipe(filter((data) => !!data),
        switchMap(() => this.userService.hasAccess([Role.ROLE_USER])))
      .subscribe((isUser) => {
        this.activeModal.dismiss();
        if (isUser) {
          return this.router.navigate(['dashboard', 'progress-status']);
        } else {
          return this.router.navigate(['dashboard', 'financial']);
        }
      });
  }

  private initiateProgressPolling() {
    timer(1, 5000).pipe(
      switchMap(() => this.packagesService.getPackage(this.activePackageId)),
      retry(),
      takeUntil(this.stopPolling$)
    ).subscribe((data: any) => {
      if (data.questionnaireSyncStatus == QuestionnaireSyncStatus.COMPLETED) {
        this.gotoQuestionnairePage();
      } else if (data.questionnaireSyncStatus == QuestionnaireSyncStatus.FAILED) {
        this.retryQuestionnaireSync(data);
      } else {
        this.activePackage = data;
      }
    });
  }

  gotoQuestionnairePage() {
    this.modalDismiss();
    this.router.navigate(['questionnaire', 'package', this.activePackageId]);
  }

  retryQuestionnaireSync(data) {
    this.retryCount++;
    this.stopPolling$.next(true);
    this.activePackage = data;
    if (this.retryCount <= this.maxRetryCount) {
      this.retryQuestionnaireSync$.next(true);
    }
  }

  modalDismiss() {
    this.stopPolling$.next(true);
    this.activeModal.dismiss();
  }

  modalDismissFromFailure() {
    this.stopPolling$.next(true);
    this.navigateFromQuestionnaireSyncFailure$.next(true);
  }

  showErrorModalWithResponse() {
    return (observable) => observable.pipe(
      catchError((error: HttpErrorResponse) => {
          if (error.status !== 401) {
            this.modalService.showErrorModal(error.error.errors || [error.error]);
          }
          return EMPTY;
        }
      ),
    );
  }

  ngOnDestroy() {
    if (this.subscribers.retryQuestionnaireSyncSubscription) {
      this.subscribers.retryQuestionnaireSyncSubscription.unsubscribe();
      this.subscribers.retryQuestionnaireSyncSubscription = null;
    }

    if (this.subscribers.navigateFromQuestionnaireSyncFailureSubscription) {
      this.subscribers.navigateFromQuestionnaireSyncFailureSubscription.unsubscribe();
      this.subscribers.navigateFromQuestionnaireSyncFailureSubscription = null;
    }
  }
}
