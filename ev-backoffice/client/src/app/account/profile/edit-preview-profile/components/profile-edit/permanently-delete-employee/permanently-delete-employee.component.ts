import { Component, OnDestroy, OnInit } from '@angular/core';

import { EMPTY, Subject } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { catchError, switchMap } from 'rxjs/operators';
import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';
import { fromPromise } from 'rxjs/internal-compatibility';

import { ModalService, UserService } from '../../../../../../core/services';

import { DeleteEmployeeModalComponent } from './delete-employee-modal/delete-employee-modal.component';


@Component({
  selector: 'app-permanently-delete-employee',
  templateUrl: './permanently-delete-employee.component.html',
})
@DestroySubscribers()
export class PermanentlyDeleteEmployeeComponent implements OnInit, OnDestroy, AddSubscribers {
  openModalSubject$: Subject<boolean> = new Subject<boolean>();

  private subscribers: any = {};

  constructor(
    private modalService: ModalService,
    private userService: UserService,
    private ngbModal: NgbModal,
  ) {
  }

  ngOnInit() {
    console.log(`${this.constructor.name} Init`);

  }

  addSubscribers() {
    this.subscribers.openModalSubjectSubscription = this.openModalSubject$.pipe(
      switchMap(() => this.openMembershipModal().pipe(
        catchError(() => EMPTY)
      ))
    ).subscribe((permanentlyDelete) => {
        if (permanentlyDelete) {
          this.userService.deleteUser();
        }
      });
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  openMembershipModal() {
    const modalRef = this.ngbModal.open(DeleteEmployeeModalComponent, {
      centered: true,
      size: 'lg'
    });
    return fromPromise(modalRef.result);
  }

  openModal() {
    this.openModalSubject$.next(true);
  }
}
