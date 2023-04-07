import { Component, Input, OnInit,ViewEncapsulation } from '@angular/core';
import { FormGroup } from '@angular/forms';

import { combineLatest, Observable, of } from 'rxjs';
import { DestroySubscribers } from 'ngx-destroy-subscribers';
import { filter, map, startWith, switchMap, withLatestFrom } from 'rxjs/operators';

import { isEqualWith,  isEqual, reduce} from 'lodash-es';

import { countries } from '../../core/models/countries';
import { states } from '../../core/models/states';
import { ModalService, UserService } from '../../core/services';
import { RegistrationStatus } from '../../core/models/registration-status.enum';



@Component({
  selector: 'app-credit-card-billing-address',
  templateUrl: './credit-card-billing-address.component.html'
})

@DestroySubscribers()
export class CreditCardBillingAddressComponent implements OnInit {
  currentUser$: Observable<any>;
  isConvertToAttorney$: Observable<boolean>;

  private subscribers: any = {};

  @Input() checkBoxLabel = '';
  @Input() formGroup: FormGroup;
  @Input() submitted = false;
  @Input() attorneyPayment = true;
  @Input() col3Label = false;
  @Input() col4Label = false;
  @Input() officeAddressFormGroup = new FormGroup({});



  countries = countries;
  states = states;
  UNITED_STATES = 'UNITED_STATES';


  get sameOfficeControl() {
    return this.formGroup.get('sameOffice');
  }

  get billingAddressControl() {
    return this.formGroup.get('address_1');
  }

  get billingAddress2Control() {
    return this.formGroup.get('address_2');
  }

  get billingCityControl() {
    return this.formGroup.get('address_city');
  }

  get billingStateControl() {
    return this.formGroup.get('address_state');
  }

  get billingZipCodeControl() {
    return this.formGroup.get('address_zip');
  }

  get billingProvinceControl() {
    return this.formGroup.get('address_province');
  }

  get billingPostalCodeControl() {
    return this.formGroup.get('address_postalCode');
  }

  get countryControl() {
    return this.formGroup.get('address_country');
  }

  constructor(
    private modalService: ModalService,
    private userService: UserService,

  ) {
    }


  ngOnInit() {
    this.currentUser$ = this.userService.currentUser$;
    this.isConvertToAttorney$ = this.userService.registrationRepresentativeType$.pipe(
      map((registrationRepresentativeType) => registrationRepresentativeType === RegistrationStatus.CONVERT_TO_ATTORNEY));
  }

  addSubscribers() {
    this.subscribers.sameOfficeControlSubscription = this.sameOfficeControl.valueChanges.pipe(
      withLatestFrom(this.isConvertToAttorney$),
      switchMap(([check, isConvertToAttorney]) =>
        this.currentUser$.pipe(
          filter((currentUser) => !!currentUser),
          map((currentUser) => [check, currentUser.profile.officeAddress, isConvertToAttorney]),
        )
      )
    )
    .subscribe(([check, officeAddress, isConvertToAttorney]) => this.setFormGroup(
      check ? isConvertToAttorney ? this.officeAddressFormGroup.value : officeAddress : {}
      )
    );

    this.subscribers.sameOfficeControlSubscription = combineLatest([
      this.formGroup.valueChanges,
      this.officeAddressFormGroup.valueChanges.pipe(startWith(null))
    ]).pipe(
      withLatestFrom(this.isConvertToAttorney$),
      switchMap(([, isConvertToAttorney]) => !isConvertToAttorney ?
            this.currentUser$.pipe(
              filter((currentUser) => !!currentUser),
              map((currentUser) => [currentUser.profile.officeAddress, isConvertToAttorney]))
            : of(this.officeAddressFormGroup.value).pipe(
              map((officeAddress) => [officeAddress, isConvertToAttorney])
            )
      )
    )
    .subscribe(([officeAddress, isConvertToAttorney]) => {
      let isOfficeSame = false;
      const formGroupValue = this.formGroup.getRawValue();
      isConvertToAttorney ?
        isOfficeSame = isEqualWith(
          officeAddress,
          this.transformToBillingAddressForConvert(formGroupValue),
          (initialOfficeAddress, billingOfficeAddress) => reduce(initialOfficeAddress, (acc, value, key) => {
            if (!acc) {
              return false;
            // condition for catch empty string or null
            } else if (!value && !billingOfficeAddress[key]) {
              return !!value === !!billingOfficeAddress[key];
            } else {
              return value === billingOfficeAddress[key];
            }
          }, true))
        : isOfficeSame = isEqual(officeAddress, this.transformToOfficeAddress(formGroupValue));
      this.sameOfficeControl.patchValue(isOfficeSame, {emitEvent: false});
    });
  }

  setFormGroup(data) {
    this.formGroup.patchValue(this.transformToBillingAddress(data), {emitEvent: false});
  }







  transformToBillingAddress(
    data = {
      line1: '',
      line2: '',
      city: '',
      country: '',
      state: '',
      zipCode: '',
      province:'',
      postalCode:''
    }
  ) {
    return {
      address_1: data.line1,
      address_2: data.line2,
      address_city: data.city,
      address_country: data.country,
      address_state: this.getState(data.state),
      address_zip: data.zipCode,
      address_province:data.province,
      address_postalCode:data.postalCode
    };
  }

  transformToBillingAddressForConvert(data) {
    if (data.address_country === this.UNITED_STATES) {
      return {
        line1: data.address_1,
        line2: data.address_2 ? data.address_2 : null,
        city: data.address_city ? data.address_city : null,
        country: data.address_country,
        state: this.getStateCode(data.address_state),
        zipCode: data.address_zip,
      };
    } else {
      return {
        line1: data.address_1,
        line2: data.address_2 ? data.address_2 : null,
        city: data.address_city ? data.address_city : null,
        country: data.address_country,
        province: data.province ? data.province : null,
        postalCode: data.postalCode ? data.postalCode : null,
      };
    }
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

  getState(item) {
    const foundedState = states.find((state) => state.value === item);
    return foundedState ? foundedState.code : '';
  }

  getStateCode(item) {
    const foundedState = states.find((state) => state.code === item);
    return foundedState ? foundedState.value : '';
  }
}
