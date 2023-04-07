import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';
import { catchError, filter, map, switchMap, withLatestFrom } from 'rxjs/operators';
import { EMPTY, merge, Observable, Subject } from 'rxjs';

import { FattService, ModalService, OrganizationService, PaymentService, TokenizeDataShrink, UserService } from '../../../core/services';
import { User } from '../../../core/models/user.model';
import { PaymentMethodDetails } from '../../../core/models/payment-method-details.model';
import { RequestState } from '../../../core/ngrx/utils';

@Component({
  selector: 'app-payment-method',
  templateUrl: './payment-method.component.html',
})

@DestroySubscribers()
export class PaymentMethodComponent implements OnInit, AddSubscribers, OnDestroy {
  currentUser$: Observable<User>;
  paymentMethod$: Observable<PaymentMethodDetails>;
  paymentMethodPutRequest$: Observable<RequestState<PaymentMethodDetails>>;
  fattLoadingStatusSubject$: Observable<boolean>;
  fattIsValidSubject$: Observable<boolean>;
  isLoading$: Observable<any>;
  formSubmitSubject$: Subject<boolean> = new Subject();
  editCardSubject$: Subject<boolean> = new Subject();
  validCvvSubject$: Subject<boolean> = new Subject();
  validNumberSubject$: Subject<boolean> = new Subject();
  fattJs: FattJs;

  formGroup: FormGroup;

  private subscribers: any = {};
  private UNITED_STATES = 'UNITED_STATES';

  constructor(
    private fattService: FattService,
    private paymentService: PaymentService,
    private modalService: ModalService,
    private userService: UserService,
    private organizationService: OrganizationService,
  ) {
    this.createFromGroup();
  }

  get billingAddressFormGroup() {
    return this.formGroup.get('billingAddress');
  }

  ngOnInit() {
    this.fattLoadingStatusSubject$ = this.fattService.fattLoadingStatusSubject$;
    this.fattIsValidSubject$ = this.fattService.fattIsValidSubject$;
    this.paymentMethod$ = this.paymentService.paymentMethod$;
    this.paymentMethodPutRequest$ = this.paymentService.paymentMethodPutRequest$;

    this.isLoading$ = merge(
      this.paymentMethodPutRequest$.pipe(
        map((request) => request.loading)
      ),
      this.fattLoadingStatusSubject$
    );
  }

  addSubscribers() {
    this.subscribers.formGroupSubscription = this.formSubmitSubject$.pipe(
      withLatestFrom(this.paymentMethod$.pipe(
        filter((paymentMethod) => !!paymentMethod),
        map((paymentMethod) => paymentMethod.customerId)
      )),
      switchMap(([, customerId]) => {
        this.fattJs = this.fattService.fattJs;
        if (!this.fattJs.validCvv && this.fattJs.validNumber) {
          this.modalService.showErrorModal('TEMPLATE.CREDIT_CARD_INFO_FORM.INVALID_CVV');
          return EMPTY;
        } else {
          return this.fattService.tokenizeMethod(
            {
              ...this.infoFormGroup.value,
              ...this.billingAddressFormGroup.value,
              customer_id: customerId
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
      withLatestFrom(this.organizationService.currentRepresentativeUserId$.pipe(
        filter((UserId) => !!UserId),
      )),
      switchMap(([paymentMethod, userId]) => this.paymentService.putPaymentMethod({
        fmPaymentMethod: this.transformTokenizeData(paymentMethod),
        userId,
      }).pipe(
        catchError(() => EMPTY),
      ))
    ).subscribe(() => {
      this.editCardSubject$.next(null);
      this.formGroup.reset();
    });

    this.subscribers.curRepUserIdSubscription = this.organizationService.currentRepresentativeUserId$.pipe(
      withLatestFrom(this.organizationService.isAdmin$),
      filter(([userId, isAdmin]) => !!userId && isAdmin)
    )
    .subscribe(([userId,]) => {
      this.editCardSubject$.next(false);
      this.paymentService.getPaymentMethod(userId);
    });
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Init`);
  }

  createFromGroup() {
    this.formGroup = new FormGroup({
      info: new FormGroup({
        repType: new FormControl(''),
        firstname: new FormControl('', Validators.required),
        lastname: new FormControl('', Validators.required),
        month: new FormControl('', Validators.required),
        year: new FormControl('', Validators.required),
      }),
      billingAddress: new FormGroup({
        sameOffice: new FormControl(''),
        address_1: new FormControl('', Validators.required),
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

  get infoFormGroup() {
    return this.formGroup.get('info');
  }

  formSubmit() {
    this.formSubmitSubject$.next(true);
  }

  editCard() {
    this.editCardSubject$.next(true);
  }

  cancelCard() {
    this.formGroup.reset();
    this.editCardSubject$.next(null);
  }
}
