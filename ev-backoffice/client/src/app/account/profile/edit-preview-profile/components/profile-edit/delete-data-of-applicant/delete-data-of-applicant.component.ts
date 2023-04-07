import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';

import { fromPromise } from 'rxjs/internal-compatibility';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { catchError, filter, map, mapTo, switchMap, withLatestFrom } from 'rxjs/operators';
import { EMPTY, Observable, of, Subject } from 'rxjs';
import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { PackageStatus } from '../../../../../../core/models/package/package-status.enum';
import { PackagesService, UserService } from '../../../../../../core/services';
import { Package } from '../../../../../../core/models/package/package.model';
import { PackageApplicant } from '../../../../../../core/models/package/package-applicant.model';
import { RequestState } from '../../../../../../core/ngrx/utils';

import { EditPreviewProfileService } from '../../../edit-preview-profile.service';

import { DeleteDataOfApplicantModalComponent } from './delete-data-of-applicant-modal/delete-data-of-applicant-modal.component';


@Component({
  selector: 'app-delete-data-of-applicant',
  templateUrl: './delete-data-of-applicant.component.html',
})
@DestroySubscribers()
export class DeleteDataOfApplicantComponent implements OnInit, OnDestroy, AddSubscribers {
  allowedPackages$: Observable<Package[]>;
  packagesRequestState$: Observable<RequestState<Package[]>>;
  nonRegisteredApplicants$: Observable<PackageApplicant[]>;
  openModalSubject$: Subject<boolean> = new Subject<boolean>();

  formGroup: FormGroup;

  get packageFormControl() {
    return this.formGroup.get('package');
  }

  get applicantIdFormControl() {
    return this.formGroup.get('applicantId');
  }

  private subscribers: any = {};

  constructor(
    private userService: UserService,
    private packagesService: PackagesService,
    private editPreviewProfileService: EditPreviewProfileService,
    private ngbModal: NgbModal,
  ) {
    this.createFormGroup();
  }

  ngOnInit() {
    this.userService.currentUserEasyVisaId$.pipe(
      withLatestFrom(this.packagesService.packages$),
      switchMap(([easyVisaId, packages]) => packages.length ? of(packages)
        : this.packagesService.getPackages({search: easyVisaId})
      ),
    ).subscribe();

    const excludedPackageStatusList: PackageStatus[] = [
      PackageStatus.LEAD,
      PackageStatus.TRANSFERRED,
      PackageStatus.DELETED
    ];
    this.allowedPackages$ = this.packagesService.packages$.pipe(
      map( items => items.filter(item => !excludedPackageStatusList.includes(item.status)))
    );
    this.packagesRequestState$ = this.packagesService.getPackagesRequest$;
    this.nonRegisteredApplicants$ = this.editPreviewProfileService.nonRegisteredApplicants$;
  }

  addSubscribers() {
    this.subscribers.packageFormControlSubscription = this.packageFormControl.valueChanges
    .subscribe((id) => {
        if (!!id) {
          this.editPreviewProfileService.getNonRegisteredApplicants(id);
          this.applicantIdFormControl.reset(null);
          this.applicantIdFormControl.enable({emitEvent: false});
        } else {
          this.editPreviewProfileService.resetNonRegisteredApplicants();
          this.applicantIdFormControl.reset(null);
          this.applicantIdFormControl.disable({emitEvent: false});
        }
      }
    );

    this.subscribers.openModalClickSubscription = this.openModalSubject$.pipe(
      filter(() => this.applicantIdFormControl.value),
      switchMap(() => this.openModal().pipe(mapTo(this.applicantIdFormControl.value))),
    ).subscribe((id) => {
        this.editPreviewProfileService.deleteNonRegisteredApplicant(id);
        this.applicantIdFormControl.patchValue(null, {emit: false});
      }
    );
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroy`);
  }

  openModalClick() {
    this.openModalSubject$.next(true);
  }

  openModal() {
    const modalRef = this.ngbModal.open(DeleteDataOfApplicantModalComponent, {
      centered: true,
      windowClass: 'custom-modal-lg',
    });
    return fromPromise(modalRef.result).pipe(
      catchError(() => EMPTY)
    );
  }

  createFormGroup() {
    this.formGroup = new FormGroup({
      package: new FormControl(null),
      applicantId: new FormControl({value: null, disabled: true}),
    });
  }
}
