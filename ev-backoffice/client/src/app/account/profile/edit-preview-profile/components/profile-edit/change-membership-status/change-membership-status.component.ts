import { Component, OnDestroy, OnInit } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

import { EMPTY, Observable, Subject } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { catchError, map, switchMap, withLatestFrom } from 'rxjs/operators';
import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';
import { fromPromise } from 'rxjs/internal-compatibility';

import { ModalService, UserService } from '../../../../../../core/services';
import { CancelMembership, ReActivateMembership } from '../../../../../../core/ngrx/user/user.actions';

import { MembershipModalComponent } from './membership-modal/membership-modal.component';


@Component({
  selector: 'app-change-membership-status',
  templateUrl: './change-membership-status.component.html',
})
@DestroySubscribers()
export class ChangeMembershipStatusComponent implements OnInit, OnDestroy, AddSubscribers {
  openModalSubject$: Subject<boolean> = new Subject<boolean>();
  activeMembership$: Observable<boolean>;
  content$: Observable<string>;
  btnLabel$: Observable<string>;

  private subscribers: any = {};

  constructor(
    private modalService: ModalService,
    private userService: UserService,
    private ngbModal: NgbModal,
  ) {
  }

  ngOnInit() {
    this.activeMembership$ = this.userService.activeMembership$;
    this.content$ = this.activeMembership$.pipe(
      map((activeMembership) => activeMembership ?
        'TEMPLATE.ACCOUNT.PROFILE.EASY_VISA_MEMBERSHIP_STATUS.CANCEL_MEMBERSHIP_P1'
        : 'TEMPLATE.ACCOUNT.PROFILE.EASY_VISA_MEMBERSHIP_STATUS.RE_ACTIVATE_MEMBERSHIP_P1')
    );

    this.btnLabel$ = this.activeMembership$.pipe(
      map((activeMembership) => activeMembership ?
        'TEMPLATE.ACCOUNT.PROFILE.EASY_VISA_MEMBERSHIP_STATUS.CANCEL_MEMBERSHIP_BTN'
        : 'TEMPLATE.ACCOUNT.PROFILE.EASY_VISA_MEMBERSHIP_STATUS.RE_ACTIVATE_MEMBERSHIP_BTN')
    );
  }

  addSubscribers() {
    this.subscribers.openModalSubjectSubscription = this.openModalSubject$.pipe(
      switchMap(() => this.openMembershipModal().pipe(
        catchError(() => EMPTY)
      )),
      withLatestFrom(this.activeMembership$),
      switchMap(([permanentlyDelete, activeMembership]) => {
          if (permanentlyDelete) {
            this.userService.deleteUser();
            return EMPTY;
          }
          const action = activeMembership ? new CancelMembership() : new ReActivateMembership();
          return this.userService.changeMembership(action).pipe(
            catchError((error: HttpErrorResponse) => {
              if (error.status !== 401) {
                this.modalService.showErrorModal(error.error.errors || [error.error]);
              }
              return EMPTY;
            }),
          );
        }
      )).subscribe();
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  openMembershipModal() {
    const modalRef = this.ngbModal.open(MembershipModalComponent, {
      centered: true,
      windowClass: 'custom-modal-lg',
    });
    return fromPromise(modalRef.result);
  }

  openModal() {
    this.openModalSubject$.next(true);
  }
}
