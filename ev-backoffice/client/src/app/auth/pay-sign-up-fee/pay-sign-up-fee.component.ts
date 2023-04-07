import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { EMPTY, merge, Observable, Subject } from 'rxjs';
import { catchError, filter, map, pluck, switchMap, take } from 'rxjs/operators';

import { isEqual } from 'lodash-es';

import { User } from '../../core/models/user.model';
import { Address, ConfigDataService, FattService, ModalService, TaxesService, TokenizeDataShrink, UserService } from '../../core/services';
import { FeeDetails } from '../../core/models/fee-details.model';
import { RepresentativeType } from '../../core/models/representativeType.enum';
import { ConfirmButtonType } from '../../core/modals/confirm-modal/confirm-modal.component';
import { Attorney } from '../../core/models/attorney.model';
import { RequestState } from '../../core/ngrx/utils';

import { noWhitespaceValidator } from '../validators/no-white-space.validator';
import { SignUpService } from '../services';
import { TaxTypes } from '../../core/models/tax-types.enum';
import { states } from '../../core/models/states';
import { EstimatedTax } from '../../core/models/estimated-tax.model';

@Component({
  selector: 'app-pay-sign-up-fee',
  templateUrl: './pay-sign-up-fee.component.html',
})
@DestroySubscribers()
export class PaySignUpFeeComponent implements AddSubscribers, OnInit, OnDestroy {
  @ViewChild('termsConditionsConfirmationModal', { static: true }) termsConditionsConfirmationModal;
  @ViewChild('termsConditionsModal', { static: true }) termsConditionsModal;
  currentUser$: Observable<User> = this.userService.currentUser$;
  feeDetails$: Observable<FeeDetails> = this.configDataService.feeDetails$;
  fattLoadingStatusSubject$: Observable<boolean>;
  isLoading$: Observable<boolean>;
  completePaymentRequest$: Observable<RequestState<Attorney>>;

  isFeeLoading$: Observable<boolean>;
  fee$: Observable<EstimatedTax>;
  addressSnapshotForTax: Address;
  fattJs: FattJs;

  officeAddressFormGroup: FormGroup = new FormGroup({});
  formGroup: FormGroup = new FormGroup({});

  private buttons = [
    {
      label: 'FORM.BUTTON.OK',
      type: ConfirmButtonType.Dismiss,
      className: 'btn btn-primary mr-2 min-w-100',
    },
  ];
  private formSubmitSubject$: Subject<any> = new Subject<any>();
  private subscribers: any = {};
  private UNITED_STATES = 'UNITED_STATES';

  constructor(
    private configDataService: ConfigDataService,
    private router: Router,
    private userService: UserService,
    private modalService: ModalService,
    private signUpService: SignUpService,
    private fattService: FattService,
    private taxesService: TaxesService,
  ) {
    this.createFormGroup();
    this.createAddressFormGroup();
  }

  get showCountrySelect$() {
    return this.currentUser$.pipe(
      filter((user) => !!user),
      pluck('profile'),
      map((profile: Attorney) => profile.representativeType !== RepresentativeType.ACCREDITED_REPRESENTATIVE),
    );
  }

  get billingAddressFormGroup() {
    return this.formGroup.get('billingAddress');
  }

  get infoFormGroup() {
    return this.formGroup.get('info');
  }

  ngOnInit() {
    this.completePaymentRequest$ = this.signUpService.completePaymentRequest$;
    this.fattLoadingStatusSubject$ = this.fattService.fattLoadingStatusSubject$;

    this.isLoading$ = merge(
      this.completePaymentRequest$.pipe(
        map((request) => request.loading)
      ),
      this.fattLoadingStatusSubject$
    );

    this.isFeeLoading$ = this.taxesService.postFeeWithEstimatedTaxRequestState$.pipe(
      map((request) => request.loading)
    );

    this.fee$ = this.taxesService.sighUpFeeWithTax$;
  }

  addSubscribers() {
    this.subscribers.currentUserSubscription = this.currentUser$.pipe(
      filter((user) => !!user),
      take(1),
      pluck('profile')
    )
      .subscribe((user) => {
        this.createFormGroup();
        this.createAddressFormGroup(user);
      });

    this.subscribers.formSubmitSubscription = this.formSubmitSubject$.pipe(
      filter(() => this.formGroup.valid),
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
      switchMap((paymentMethod) => this.signUpService.completePayment(
        {
          ...this.transformTokenizeData(paymentMethod),
        }
      ).pipe(
        catchError((error: HttpErrorResponse) => {
          if (error.status !== 401) {
            this.modalService.showErrorModal(error.error.errors || [error.error]);
          }
          return EMPTY;
        }),
      ))
    ).subscribe(() => {
      this.router.navigate(['auth', 'attorney-welcome']);
    });
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  createAddressFormGroup(data?) {
    this.officeAddressFormGroup = new FormGroup({
      country: new FormControl(
        {
          value: data && data.officeAddress.country ? data.officeAddress.country : this.UNITED_STATES,
          disabled: data && data.representativeType === RepresentativeType.ACCREDITED_REPRESENTATIVE
        },
        [Validators.required]),
      line1: new FormControl(
        data && data.officeAddress.line1 ? data.officeAddress.line1 : '',
        [Validators.required, noWhitespaceValidator],
      ),
      line2: new FormControl(
        data && data.officeAddress.line2 ? data.officeAddress.line2 : '',
      ),
      city: new FormControl(
        data && data.officeAddress.city ? data.officeAddress.city : '',
        [Validators.required, noWhitespaceValidator],
      ),
      state: new FormControl(
        data && data.officeAddress.state ? data.officeAddress.state : '',
        [Validators.required],
      ),
      province: new FormControl(
        data && data.officeAddress.province ? data.officeAddress.province : '',
        [Validators.required, noWhitespaceValidator],
      ),
      zipCode: new FormControl(
        data && data.officeAddress.zipCode ? data.officeAddress.zipCode : '',
        [Validators.required, Validators.pattern('^[0-9]{5}(?:-[0-9]{4})?$')],
      ),
      postalCode: new FormControl(
        data && data.officeAddress.postalCode ? data.officeAddress.postalCode : '',
        [Validators.required, noWhitespaceValidator],
      ),
    });
  }

  createFormGroup() {
    this.formGroup = new FormGroup({
      info: new FormGroup({
        firstname: new FormControl('', Validators.required),
        lastname: new FormControl('', Validators.required),
        month: new FormControl('', Validators.required),
        year: new FormControl('', Validators.required)
      }),
      agreeTerms: new FormControl(false, Validators.required),
      billingAddress: new FormGroup({
        sameOffice: new FormControl(''),
        address_1: new FormControl('', Validators.required),
        address_2: new FormControl(''),
        address_city: new FormControl('', Validators.required),
        address_state: new FormControl('', this.requiredIfValidator(() => this.billingAddressFormGroup.get('address_country').value == this.UNITED_STATES)),
        address_zip: new FormControl('', this.requiredIfValidator(() => this.billingAddressFormGroup.get('address_country').value == this.UNITED_STATES)),
        address_country: new FormControl('', Validators.required),
        address_province: new FormControl('', this.requiredIfValidator(() => this.billingAddressFormGroup.get('address_country').value != this.UNITED_STATES)),
        address_postalCode: new FormControl('', this.requiredIfValidator(() => this.billingAddressFormGroup.get('address_country').value != this.UNITED_STATES))
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

  goToRepBasicInfo() {
    this.router.navigate(['auth', 'rep-basic-info']);
  }

  formSubmit() {
    if (!this.formGroup.get('agreeTerms').value) {
      this.openTermsConditionsConfirmationModal();
    } else {
      this.formSubmitSubject$.next(this.formGroup.value);
    }
  }

  openTermsConditionsConfirmationModal() {
    return this.modalService.openConfirmModal({
      header: 'TEMPLATE.AUTH.PAY_SIGN_UP_FEE.TERMS_CONDITIONALS_CONFIRMATION_MODAL.TITLE',
      body: this.termsConditionsConfirmationModal,
      buttons: this.buttons,
      centered: true,
    });
  }

  openTermsConditionsModal() {
    return this.modalService.openConfirmModal({
      header: 'TEMPLATE.AUTH.TERMS_CONDITIONALS_MODAL.TITLE',
      body: this.termsConditionsModal,
      buttons: this.buttons,
      centered: true,
      size: 'lg',
      windowClass: 'custom-modal-lg'
    });
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

  getTaxes() {
    const address = this.transformToOfficeAddress(this.billingAddressFormGroup.value);
    this.addressSnapshotForTax = this.billingAddressFormGroup.value;
    this.taxesService.postEstimatedTax({ type: TaxTypes.SIGNUP_FEE, address });
  }

  isBillingAddressEqualToTaxAddress() {
    return isEqual(this.addressSnapshotForTax, this.billingAddressFormGroup.value);
  }
}
