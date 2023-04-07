import { Component, OnDestroy, OnInit } from '@angular/core';

import { EMPTY, Observable, Subject } from 'rxjs';
import { fromPromise } from 'rxjs/internal-compatibility';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { catchError, switchMap } from 'rxjs/operators';
import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { UserService } from '../../../../../../core/services';
import { RequestState } from '../../../../../../core/ngrx/utils';

import { PermanentlyDeleteModalComponent } from './permanently-delete-modal/permanently-delete-modal.component';


@Component({
  selector: 'app-delete-my-data',
  templateUrl: './delete-my-data.component.html',
})

@DestroySubscribers()
export class DeleteMyDataComponent implements OnInit, OnDestroy, AddSubscribers {
  userDeleteRequest$: Observable<RequestState<number>>;
  openModalSubject$: Subject<boolean> = new Subject<boolean>();

  private subscribers: any = {};

  constructor(
    private userService: UserService,
    private ngbModal: NgbModal,
  ) {
  }

  ngOnInit() {
    this.userDeleteRequest$ = this.userService.userDeleteRequest$;
  }

  addSubscribers() {
    this.subscribers.openModalSubjectSubscription = this.openModalSubject$.pipe(
      switchMap(() => this.openPermanentlyDeleteModal().pipe(
        catchError(() => EMPTY)
      ))
    ).subscribe(() => this.userService.deleteUser());
  }

  openPermanentlyDeleteModal() {
    const modalRef = this.ngbModal.open(PermanentlyDeleteModalComponent, {
      centered: true,
      windowClass: 'custom-modal-lg',
    });
    return fromPromise(modalRef.result);
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  openModal() {
    this.openModalSubject$.next(true);
  }
}
