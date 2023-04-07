import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { filter, map, take } from 'rxjs/operators';
import { pluck, switchMap } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { Observable } from 'rxjs';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { User } from '../../core/models/user.model';
import { states } from '../../core/models/states';
import { countries } from '../../core/models/countries';
import { OrganizationService, UserService } from '../../core/services';
import { AsyncRequestValidator } from '../../shared/validators/async-request.validator';
import { RepresentativeType } from '../../core/models/representativeType.enum';
import { RegistrationStatus } from '../../core/models/registration-status.enum';
import { SignUpService } from '../services';
import { noWhitespaceValidator } from '../validators/no-white-space.validator';
import { Attorney } from '../../core/models/attorney.model';
import { AttorneyType } from '../../core/models/attorney-type.enum';

@Component({
  selector: 'app-rep-basic-info',
  templateUrl: './rep-basic-info.component.html',
})
@DestroySubscribers()
export class RepBasicInfoComponent implements AddSubscribers, OnInit, OnDestroy {
  currentUser$: Observable<User> = this.userService.currentUser$;
  representativeType$: Observable<string> = new Observable<string>();
  countries = countries;
  states = states;
  selectedUS = 'UNITED_STATES';
  formGroup: FormGroup;

  private formSubmitSubject$: Subject<any> = new Subject<any>();
  private subscribers: any = {};

  constructor(
    private router: Router,
    private userService: UserService,
    private signUpService: SignUpService,
    private organizationService: OrganizationService,
  ) {

  }

  get firstNameControl() {
    return this.formGroup.get('firstName');
  }

  get middleNameControl() {
    return this.formGroup.get('middleName');
  }

  get lastNameControl() {
    return this.formGroup.get('lastName');
  }

  get officeAddressFormGroup() {
    return this.formGroup.get('officeAddress');
  }

  get showCountrySelect$() {
    return this.currentUser$.pipe(
      filter((user) => !!user),
      pluck('profile'),
      map((profile: Attorney) => profile.representativeType !== RepresentativeType.ACCREDITED_REPRESENTATIVE),
    );
  }

  get emailControl() {
    return this.formGroup.get('email');
  }


  ngOnInit() {
    this.representativeType$ = this.currentUser$.pipe(
      filter((user) => !!user),
      pluck('profile'),
      map((user: Attorney) => {
          switch (user.representativeType) {
            case RepresentativeType.ATTORNEY: {
              return 'TEMPLATE.REPRESENTATIVE_TYPES.ATTORNEY';
            }
            case RepresentativeType.ACCREDITED_REPRESENTATIVE: {
              return 'TEMPLATE.REPRESENTATIVE_TYPES.ACCREDITED_REPRESENTATIVE';
            }
            default: {
              return '';
            }
          }
        }
      )
    );
  }

  addSubscribers() {
    this.subscribers.createFormSubscription = this.currentUser$.pipe(
      filter((user) => !!user),
      take(1),
      pluck('profile'),
    )
    .subscribe((profile) => {
        this.createForm(profile);
        this.emailControl.disable();
      }
    );

    this.subscribers.formSubmitSubscription = this.formSubmitSubject$.pipe(
      filter(() => this.formGroup.valid),
      switchMap((data) => this.organizationService.updateAttorney({
          ...data,
          attorneyType: AttorneyType.SOLO_PRACTITIONER
        })
      )
    )
    .subscribe(() => this.router.navigate(['auth', 'attorney-welcome']));

  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  createForm(data?) {
    this.formGroup = new FormGroup({
      id: new FormControl(data.id),
      firstName: new FormControl(data.firstName, [Validators.required, noWhitespaceValidator]),
      email: new FormControl(
        data.email,
        [
          Validators.required,
          Validators.email,
        ],
        AsyncRequestValidator.createValidator(
          (value) => this.signUpService.emailValidateRequest(value),
          data.email,
        ),
      ),
      middleName: new FormControl(data.middleName),
      lastName: new FormControl(data.lastName, [Validators.required, noWhitespaceValidator]),
      officeAddress: new FormGroup({
        country: new FormControl(
          {
            value: data.officeAddress ? data.officeAddress.country : this.selectedUS,
            disabled: data.representativeType === RepresentativeType.ACCREDITED_REPRESENTATIVE
          },
          [Validators.required]),
        line1: new FormControl(
          data.officeAddress && data.officeAddress.line1 ? data.officeAddress.line1 : '',
          [Validators.required, noWhitespaceValidator],
        ),
        line2: new FormControl(data.officeAddress && data.officeAddress.line2 ? data.officeAddress.line2 : ''),
        city: new FormControl(
          data.officeAddress && data.officeAddress.city ? data.officeAddress.city : '',
          [Validators.required, noWhitespaceValidator],
        ),
        state: new FormControl(
          data.officeAddress && data.officeAddress.state ? data.officeAddress.state : '',
          [Validators.required],
        ),
        province: new FormControl(
          data.officeAddress && data.officeAddress.province ? data.officeAddress.province : '',
          [Validators.required, noWhitespaceValidator],
        ),
        zipCode: new FormControl(
          data.officeAddress && data.officeAddress.zipCode ? data.officeAddress.zipCode : '',
          [Validators.required, Validators.pattern('^[0-9]{5}(?:-[0-9]{4})?$')],
        ),
        postalCode: new FormControl(
          data.officeAddress && data.officeAddress.postalCode ? data.officeAddress.postalCode : '',
          [Validators.required, noWhitespaceValidator],
        ),
      }),
      officePhone: new FormControl(data.officePhone),
      mobilePhone: new FormControl(data.mobilePhone),
      faxNumber: new FormControl(data.faxNumber),
      registrationStatus: new FormControl(
        data.registrationStatus !== RegistrationStatus.COMPLETE ? RegistrationStatus.CONTACT_INFO_UPDATED : data.registrationStatus
      ),
    });
  }

  formSubmit() {
    this.formSubmitSubject$.next(this.formGroup.value);
  }

  goToStandardEvCharges() {
    this.router.navigate(['auth', 'standard-ev-charges']);
  }
}
