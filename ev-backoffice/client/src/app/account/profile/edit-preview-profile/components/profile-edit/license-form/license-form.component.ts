import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormGroup } from '@angular/forms';

import { catchError, switchMap, tap } from 'rxjs/operators';
import { EMPTY, Subject } from 'rxjs';
import { fromPromise } from 'rxjs/internal-compatibility';

import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { states } from '../../../../../../core/models/states';

import { AddBarAdmissionModalComponent } from './add-bar-admission-modal/add-bar-admission-modal.component';
import { ProfileEditService } from '../profile-edit.service';

@Component({
  selector: 'app-license-form',
  templateUrl: './license-form.component.html',
  styleUrls: ['./license-form.component.scss'],
})

@DestroySubscribers()
export class LicenseFormComponent implements OnInit, OnDestroy, AddSubscribers {
  @ViewChild('addBarAdmissionModal', { static: false }) addBarAdmissionModal;

  addBarAdmissionSubject$: Subject<boolean> = new Subject();

  states = states;
  licensedRegionFormGroup: FormGroup;
  currentDate = new Date();


  private subscribers: any = {};

  get licensedRegionsFormArray() {
    return this.profileEditService.licensedRegionsFormArray;
  }

  get maxDate() {
    return {
      year: this.currentDate.getFullYear(),
      month: this.currentDate.getMonth() + 1,
      day: this.currentDate.getDate(),
    };
  }

  get minDate() {
    return {
      year: 1950,
      month: 1,
      day: 1,
    };
  }

  constructor(
    private ngbModal: NgbModal,
    private profileEditService: ProfileEditService,
  ) {

  }

  ngOnInit() {
    this.licensedRegionFormGroup = this.profileEditService.createLicensedFormGroup();
  }

  addSubscribers() {
    this.subscribers.addBarAdmissionSubscription = this.addBarAdmissionSubject$.pipe(
      switchMap(() => this.openAddBarAdmissionModal())
    ).subscribe(() => {
      const licensedFormGroup = this.profileEditService.createLicensedFormGroup(this.licensedRegionFormGroup.value);
      this.licensedRegionsFormArray.push(licensedFormGroup);
    });
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  removeLicense(index) {
    this.licensedRegionsFormArray.removeAt(index);
    this.licensedRegionsFormArray.markAsDirty();
  }

  addBarAdmission() {
    this.addBarAdmissionSubject$.next(true);
  }

  openAddBarAdmissionModal() {
    this.licensedRegionFormGroup.reset();
    const modalRef = this.ngbModal.open(AddBarAdmissionModalComponent, {
      centered: true,
      size: 'lg',
    });
    modalRef.componentInstance.licensedRegionFormGroup = this.licensedRegionFormGroup;
    modalRef.componentInstance.maxDate = this.maxDate;
    modalRef.componentInstance.minDate = this.minDate;
    return fromPromise(modalRef.result).pipe(
      tap(() => this.licensedRegionsFormArray.markAsDirty()),
      catchError(() => EMPTY),
    );
  }
}
