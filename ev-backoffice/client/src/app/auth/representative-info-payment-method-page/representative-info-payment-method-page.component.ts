import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';

import { catchError, filter, map, switchMap, take } from 'rxjs/operators';
import { pluck } from 'rxjs/operators';
import { EMPTY, Observable } from 'rxjs';
import { Subject } from 'rxjs';
import { merge, ReplaySubject } from 'rxjs';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { User } from '../../core/models/user.model';
import {
  Address,
  ConfigDataService,
  FattService,
  ModalService,
  OrganizationService,
  PaymentService, TaxesService,
  TokenizeDataShrink,
  UserService
} from '../../core/services';
import { AsyncRequestValidator } from '../../shared/validators/async-request.validator';
import { RepresentativeType } from '../../core/models/representativeType.enum';
import { RegistrationStatus } from '../../core/models/registration-status.enum';
import { states } from '../../core/models/states';
import { countries } from '../../core/models/countries';
import { Attorney } from '../../core/models/attorney.model';
import { FeeDetails } from '../../core/models/fee-details.model';
import { PaymentMethodDetails } from '../../core/models/payment-method-details.model';
import { RequestState } from '../../core/ngrx/utils';
import { EstimatedTax } from '../../core/models/estimated-tax.model';
import { TaxTypes } from '../../core/models/tax-types.enum';

import { SignUpService } from '../services';
import { noWhitespaceValidator } from '../validators/no-white-space.validator';

@Component({
  selector: 'app-representative-info-payment-method-page',
  templateUrl: './representative-info-payment-method-page.component.html',
  exportAs: 'ngForm'
})
@DestroySubscribers()
export class RepresentativeInfoPaymentMethodPageComponent implements AddSubscribers, OnInit, OnDestroy {
  currentUser$: Observable<User> = this.userService.currentUser$;
  paymentMethod$: Observable<PaymentMethodDetails>;
  paymentMethodPutRequest$: Observable<RequestState<PaymentMethodDetails>>;
  feeDetails$: Observable<FeeDetails> = this.configDataService.feeDetails$;
  convertToAttorneyRequest$: Observable<RequestState<any>>;
  fattLoadingStatusSubject$: ReplaySubject<boolean> = new ReplaySubject<boolean>(1);
  representativeType$: Observable<string> = new Observable<string>();
  isLoading$: Observable<any>;
  goToProfileSubject$: Subject<void> = new Subject<void>();

  isFeeLoading$: Observable<boolean>;
  fee$: Observable<EstimatedTax>;
  addressSnapshotForTax: Address;
  fattJs: FattJs;

  private formSubmitSubject$: Subject<any> = new Subject<any>();

  private UNITED_STATES = 'UNITED_STATES';
  countries = countries;
  states = states;
  formGroup: FormGroup = new FormGroup({});
  paymentMethodFormGroup: FormGroup = new FormGroup({});

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

  get mobilePhoneControl() {
    return this.formGroup.get('mobilePhone');
  }

  get officePhoneControl() {
    return this.formGroup.get('officePhone');
  }

  get faxNumberControl() {
    return this.formGroup.get('faxNumber');
  }

  get billingAddressFormGroup() {
    return this.paymentMethodFormGroup.get('billingAddress');
  }

  get infoFormGroup() {
    return this.paymentMethodFormGroup.get('info');
  }

  get agreeTermsControl() {
    return this.paymentMethodFormGroup.get('agreeTerms');
  }

  private subscribers: any = {};

  constructor(
    private router: Router,
    private userService: UserService,
    private signUpService: SignUpService,
    private organizationService: OrganizationService,
    private fattService: FattService,
    private configDataService: ConfigDataService,
    private paymentService: PaymentService,
    private modalService: ModalService,
    private taxesService: TaxesService,
  ) {
    this.createPaymentMethodFormGroup();
    this.createForm();
  }

  ngOnInit() {
    this.paymentMethod$ = this.paymentService.paymentMethod$;
    this.paymentMethodPutRequest$ = this.paymentService.paymentMethodPutRequest$;
    this.convertToAttorneyRequest$ = this.userService.convertToAttorneyRequest$;
    this.fattLoadingStatusSubject$ = this.fattService.fattLoadingStatusSubject$;

    this.isFeeLoading$ = this.taxesService.postFeeWithEstimatedTaxRequestState$.pipe(
      map((request) => request.loading)
    );

    this.fee$ = this.taxesService.sighUpFeeWithTax$;

    this.isLoading$ = merge(
      this.convertToAttorneyRequest$.pipe(
        map((request) => request.loading)
      ),
      this.fattLoadingStatusSubject$
    );

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
    .subscribe((profile) => this.createForm(profile));

    this.subscribers.formSubmitSubscription = this.formSubmitSubject$.pipe(
      switchMap(() => {
        this.fattJs = this.fattService.fattJs;
        if (!this.fattJs.validCvv && this.fattJs.validNumber) {
          this.modalService.showErrorModal('TEMPLATE.CREDIT_CARD_INFO_FORM.INVALID_CVV');
          return EMPTY;
        } else {
          return this.fattService.tokenizeMethod(
            {
              ...this.infoFormGroup.value,
              ...this.billingAddressFormGroup.value,
            }).pipe(
            catchError((error: HttpErrorResponse) => {
              if (error.status !== 401) {
                this.modalService.showErrorModal(error.message || error.error.errors || [error.error]);
              }
              return EMPTY;
            }),
          );
        }
      }),
    )
    .subscribe((paymentMethod) => this.userService.convertToAttorney({
      profile: this.formGroup.value,
      paymentMethod: this.transformTokenizeData(paymentMethod),
    }));

    this.subscribers.goToProfileSubscription = this.goToProfileSubject$
    .subscribe(() => {
        this.organizationService.getMenuOrganizations();
        this.router.navigate(['account', 'profile']);
      }
    );
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  createForm(data?) {
    this.formGroup = new FormGroup({
      id: new FormControl(data?.id),
      firstName: new FormControl(data?.firstName, [Validators.required, noWhitespaceValidator]),
      email: new FormControl(
        data?.email,
        [
          Validators.required,
          Validators.email,
        ],
        AsyncRequestValidator.createValidator(
          (value) => this.signUpService.emailValidateRequest(value),
          data?.email,
        )
      ),
      middleName: new FormControl(data?.middleName),
      lastName: new FormControl(data?.lastName, [Validators.required, noWhitespaceValidator]),
      officeAddress: new FormGroup({
        country: new FormControl(
          {
            value: data?.officeAddress ? data.officeAddress.country : this.UNITED_STATES,
            disabled: data?.representativeType === RepresentativeType.ACCREDITED_REPRESENTATIVE
          },
          [Validators.required]),
        line1: new FormControl(
          data?.officeAddress && data.officeAddress.line1 ? data.officeAddress.line1 : '',
          [Validators.required, noWhitespaceValidator],
        ),
        line2: new FormControl(data?.officeAddress && data.officeAddress.line2 ? data.officeAddress.line2 : ''),
        city: new FormControl(
          data?.officeAddress && data.officeAddress.city ? data.officeAddress.city : '',
          [Validators.required, noWhitespaceValidator],
        ),
        state: new FormControl(
          data?.officeAddress && data.officeAddress.state ? data.officeAddress.state : '',
          [Validators.required],
        ),
        province: new FormControl(
          data?.officeAddress && data.officeAddress.province ? data.officeAddress.province : '',
          [Validators.required, noWhitespaceValidator],
        ),
        zipCode: new FormControl(
          data?.officeAddress && data.officeAddress.zipCode ? data.officeAddress.zipCode : '',
          [Validators.required, Validators.pattern('^[0-9]{5}(?:-[0-9]{4})?$')],
        ),
        postalCode: new FormControl(
          data?.officeAddress && data.officeAddress.postalCode ? data.officeAddress.postalCode : '',
          [Validators.required, noWhitespaceValidator],
        ),
      }),
      officePhone: new FormControl(data?.officePhone),
      mobilePhone: new FormControl(data?.mobilePhone),
      faxNumber: new FormControl(data?.faxNumber),
      registrationStatus: new FormControl(
        data?.registrationStatus !== RegistrationStatus.COMPLETE ? RegistrationStatus.CONTACT_INFO_UPDATED : data.registrationStatus
      ),
    });
  }

  createPaymentMethodFormGroup() {
    this.paymentMethodFormGroup = new FormGroup({
      info: new FormGroup({
        firstname: new FormControl('', Validators.required),
        lastname: new FormControl('', Validators.required),
        month: new FormControl('', Validators.required),
        year: new FormControl('', Validators.required)
      }),
      agreeTerms: new FormControl(false, Validators.required),
      billingAddress: new FormGroup({
        address_1: new FormControl('', Validators.required),
        sameOffice: new FormControl(''),
        address_2: new FormControl(''),
        address_city: new FormControl('', Validators.required),
        address_state: new FormControl('', this.requiredIfValidator(()=>this.billingAddressFormGroup.get('address_country').value == this.UNITED_STATES)),
        address_zip: new FormControl('', this.requiredIfValidator(()=>this.billingAddressFormGroup.get('address_country').value == this.UNITED_STATES)),
        address_country: new FormControl('', Validators.required),
        address_province: new FormControl('', this.requiredIfValidator(()=>this.billingAddressFormGroup.get('address_country').value != this.UNITED_STATES)),
        address_postalCode: new FormControl('', this.requiredIfValidator(()=>this.billingAddressFormGroup.get('address_country').value != this.UNITED_STATES))
      }),
    });
  }

  private requiredIfValidator(predicate) {
    return (formControl => {
      if (!formControl.parent) {
        return null;
      }
      if (predicate()) {
        return Validators.required(formControl);
      }
      return null;
    });
  }

  transformTokenizeData(
    data: TokenizeDataShrink = {
      address_1: '',
      address_2: '',
      address_city: '',
      address_country: '',
      address_state: '',
      address_zip: '',
      card_exp: '',
      person_name: '',
      card_last_four: '',
      card_type: '',
      id: '',
      customer_id: '',
    }
  ) {
    return {
      address1: data.address_1,
      address2: data.address_2,
      addressCity: data.address_city,
      addressCountry: data.address_country,
      addressState: data.address_state,
      addressZip: data.address_zip,
      cardExpiration: data.card_exp,
      cardHolder: data.person_name,
      cardLastFour: data.card_last_four,
      cardType: data.card_type,
      customerId: data.customer_id,
      fmPaymentMethodId: data.id,
    };
  }

  transformToOfficeAddress(data) {
    return {
      line1: data.address_1,
      line2: data.address_2 ? data.address_2 : null,
      city: data.address_city ? data.address_city : null,
      country: data.address_country,
      state: this.getStateCode(data.address_state),
      zipCode: data.address_zip,
      province: null,
      postalCode: null,
    };
  }

  getStateCode(item) {
    const foundedState = states.find((state) => state.code === item);
    return foundedState ? foundedState.value : '';
  }

  formSubmit() {
    this.formSubmitSubject$.next(this.formGroup.value);
  }

  goToProfile() {
    this.goToProfileSubject$.next();
  }

  getTaxes() {
    const address = this.transformToOfficeAddress(this.billingAddressFormGroup.value);
    this.addressSnapshotForTax = this.billingAddressFormGroup.value;
    this.taxesService.postEstimatedTax({type: TaxTypes.SIGNUP_FEE, address });
  }
}
