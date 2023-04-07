import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { EMPTY, merge, Observable, Subject } from 'rxjs';
import { catchError, debounceTime, filter, map, pluck, switchMap } from 'rxjs/operators';
import { DestroySubscribers } from 'ngx-destroy-subscribers';

import { Attorney } from '../../core/models/attorney.model';
import { Package } from '../../core/models/package/package.model';
import { OrganizationService, PackagesService, UserService } from '../../core/services';
import { RequestState, ResponseStatus } from '../../core/ngrx/utils';
import { Organization } from '../../core/models/organization.model';
import { PackageStatus } from '../../core/models/package/package-status.enum';
import { countries } from '../../core/models/countries';
import { benefitCategories } from '../../core/models/benefit-categories';


@Component({
  selector: 'app-legal-representative',
  templateUrl: './legal-representative.component.html',
})
@DestroySubscribers()
export class LegalRepresentativeComponent implements OnInit, OnDestroy {
  package$: Observable<Package>;
  currentRepresentative$: Observable<Attorney>;
  verifyAttorneyRequestState$: Observable<RequestState<{ representativeId: number; organizations: Organization[] }>>;
  packagesTransferRequestLoading$: Observable<boolean>;
  transfereeOrganizations$: Observable<Organization[]>;
  nonValidPackage$: Observable<boolean>;
  isShowOrganizations$: Observable<boolean>;
  benefitCategory$: Observable<string>;

  formGroup: FormGroup;
  verifyRecipientFormGroup: FormGroup;
  isReadOnlyPackage$: Observable<boolean>;

  private transferCasesSubject$: Subject<boolean> = new Subject();
  private subscribers: any = {};

  countries = countries;
  benefitCategories = benefitCategories;

  get packageIdFormControl() {
    return this.formGroup.get('packageId');
  }

  get organizationIdFormControl() {
    return this.formGroup.get('organizationId');
  }

  get representativeIdFormControl() {
    return this.formGroup.get('representativeId');
  }

  get emailFormControl() {
    return this.verifyRecipientFormGroup.get('email');
  }

  get easyVisaIdFormControl() {
    return this.verifyRecipientFormGroup.get('easyVisaId');
  }

  constructor(
    private userService: UserService,
    private packagesService: PackagesService,
    private organizationService: OrganizationService,
  ) {
    this.createFormGroup();
  }

  ngOnInit() {
    this.packagesTransferRequestLoading$ = this.packagesService.packagesTransferByApplicantPostRequest$.pipe(
      pluck('loading')
    );
    this.verifyAttorneyRequestState$ = this.organizationService.verifyAttorneyRequestState$;
    this.package$ = this.packagesService.package$;
    this.currentRepresentative$ = this.package$.pipe(
      filter((item) => !!item),
      map((item) => item.representative)
    );

    this.isReadOnlyPackage$ = this.package$.pipe(
      filter((currentPackage) => !!currentPackage),
      map((currentPackage) => currentPackage.status == PackageStatus.TRANSFERRED)
    );

    this.nonValidPackage$ = this.package$.pipe(
      filter((item) => !!item),
      map((item) => item.status === PackageStatus.BLOCKED)
    );

    this.verifyRecipientFormGroup = new FormGroup({
      easyVisaId: new FormControl(null, [
        Validators.required,
        Validators.pattern('(^[A-Z]\\d{10}$)'),
      ]),
      email: new FormControl(null, [Validators.required, Validators.email]),
    });

    this.isShowOrganizations$ = merge(
      this.organizationService.verifyAttorneyRequestState$.pipe(
        map(({loaded, status}) => loaded && status === ResponseStatus.success)
      ),
      this.verifyRecipientFormGroup.valueChanges.pipe(
        map((res) => !res)
      ),
    );

    this.transfereeOrganizations$ = this.verifyAttorneyRequestState$.pipe(
      filter(({status, loaded, loading}) => status === ResponseStatus.success && loaded && !loading),
      map(({data}) => data?.organizations),
    );

    this.benefitCategory$ = this.package$.pipe(
      filter(item => !!item),
      map(packages => packages.applicants.reduce((acc, applicant) => applicant.benefitCategory || acc, ''))
    );
  }

  addSubscribers() {
    this.subscribers.packegeIdFormControlSubscription = this.packageIdFormControl.valueChanges
    .subscribe((id) => this.packagesService.selectPackage(id));

    this.subscribers.verifySubscription = this.verifyRecipientFormGroup.valueChanges.pipe(
      debounceTime(2000),
      filter(() => this.verifyRecipientFormGroup.valid),
      switchMap(() =>
        this.organizationService.verifyAttorney(this.verifyRecipientFormGroup.value).pipe(
          catchError(() => {
            this.formGroup.patchValue({representativeId: null});
            this.formGroup.patchValue({organizationId: null});
            return EMPTY;
          })
        )
      ),
    ).subscribe(({representativeId, organizations}) => {
      if(organizations.length === 1) {
        const [{id}] = organizations;
        this.organizationIdFormControl.patchValue(id);
      }
      this.formGroup.patchValue({representativeId});
    });

    this.subscribers.valueChangedSubscription = this.verifyRecipientFormGroup.valueChanges.pipe(
    ).subscribe(() => this.formGroup.patchValue({representativeId: null}));

    this.subscribers.transferCasesSubscription = this.transferCasesSubject$.pipe(
      filter(() => this.formGroup.valid),
      switchMap(() =>
        this.packagesService.packagesTransferByApplicant({
          ...this.formGroup.value,
          packageId: parseInt(this.packageIdFormControl.value, 10)
        }).pipe(catchError(() => EMPTY))
      ),
    ).subscribe(() => {
      this.organizationIdFormControl.reset();
      this.representativeIdFormControl.reset();
      this.verifyRecipientFormGroup.reset();
    });
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  createFormGroup() {
    this.formGroup = new FormGroup({
      packageId: new FormControl(null, Validators.required),
      organizationId: new FormControl(null, Validators.required),
      representativeId: new FormControl(null, Validators.required),
    });
  }

  packagesTransfer() {
    this.transferCasesSubject$.next(true);
  }
}
