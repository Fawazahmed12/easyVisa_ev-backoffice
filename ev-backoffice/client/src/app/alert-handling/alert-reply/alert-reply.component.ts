import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { catchError, map, mapTo, switchMap, withLatestFrom } from 'rxjs/operators';
import { combineLatest, EMPTY, Observable, of, ReplaySubject } from 'rxjs';

import { ModalService, UserService } from '../../core/services';
import { AlertHandlingService } from '../../core/services/alert-handling.service';
import { Role } from '../../core/models/role.enum';
import { OkButtonLg } from '../../core/modals/confirm-modal/confirm-modal.component';


@Component({
  selector: 'app-alert-reply',
  templateUrl: './alert-reply.component.html',
})
@DestroySubscribers()
export class AlertReplyComponent implements AddSubscribers, OnDestroy, OnInit {
  private accept$: Observable<boolean>;
  alertId$: Observable<number>;
  private subscribers: any = {};

  @ViewChild('alertReply', { static: true }) alertReply;
  messageSubject$: ReplaySubject<string> = new ReplaySubject<string>(1);

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private modalService: ModalService,
    private alertHandlingService: AlertHandlingService,
    private userService: UserService,
  ) {
  }

  ngOnInit() {
    this.accept$ = this.activatedRoute.queryParams.pipe(
      map((params) => params[ 'accept' ]),
    );

    this.alertId$ = this.activatedRoute.params.pipe(
      map((params) => params.alertId),
    );
  }

  addSubscribers() {
    this.subscribers.verifyAttorneySubscription = combineLatest([
      this.accept$,
      this.alertId$
    ]).pipe(
      withLatestFrom(this.userService.hasAccess([ Role.ROLE_USER ])),
      switchMap(([ [ accept, alertId ], isUser ]) =>
        this.alertHandlingService.alertReply({ id: alertId, accept }).pipe(
          catchError((error: HttpErrorResponse) => {

              if (error.status === 422 && error.error.errors[0].type === 'INVITATION_ALREADY_ACCEPTED') {
                this.messageSubject$.next(error.error.errors[0].message);
                return this.modalService.openConfirmModal({
                  header: 'TEMPLATE.MODAL.INVITATION_ALREADY_ACCEPTED_TITLE',
                  body: this.alertReply,
                  centered: true,
                  buttons: [ OkButtonLg ]
                }).pipe(catchError((err) => of(err)));
              } else if (error.status !== 401) {
                return this.modalService.showErrorModal(error.error.errors).pipe(
                  catchError((err) => of(err)));
              } else {
                return EMPTY;
              }
            }
          ),
          mapTo(isUser)
        )
      ),
    )
      .subscribe(isUser => this.router.navigate(!isUser ? [ 'task-queue', 'alerts' ] : [ 'dashboard', 'alerts' ]));
  }

  ngOnDestroy() {
    this.messageSubject$.next(null);
    console.log(`${this.constructor.name} Destroys`);
  }
}
