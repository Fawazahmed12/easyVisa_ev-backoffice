import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { Router } from '@angular/router';

import { TranslateTestingModule } from 'ngx-translate-testing';

import {
  ConfigDataService,
  FattService,
  ModalService,
  OrganizationService,
  PaymentService,
  TaxesService,
  UserService
} from '../../core/services';
import {
  ConfigDataServiceMock,
  FattServiceMock, I18nServiceMock,
  ModalServiceMock,
  OrganizationServiceMock, PaymentServiceMock,
  SignUpServiceMock, TaxesServiceMock,
  UserServiceMock
} from '../../core/testing';
import { I18nService } from '../../core/i18n/i18n.service';

import { AddressModule } from '../../components/address/address.module';
import { PhoneFieldModule } from '../../components/phone-field/phone-field.module';
import { AuthFormFieldModule } from '../../components/auth-form-field/auth-form-field.module';
import { CreditCardInfoModule } from '../../components/credit-card-info/credit-card-info.module';

import { noWhitespaceValidator } from '../validators/no-white-space.validator';
import { SignUpService } from '../services';

import { RepresentativeInfoPaymentMethodPageComponent } from './representative-info-payment-method-page.component';
import { CreditCardBillingAddressModule } from '../../components/credit-card-billing-address/credit-card-billing-address.module';

const routerSpy = {
  navigate: jasmine.createSpy('navigate')
};

describe('RepresentativeInfoPaymentMethodPageComponent', () => {
  let component: RepresentativeInfoPaymentMethodPageComponent;
  let fixture: ComponentFixture<RepresentativeInfoPaymentMethodPageComponent>;

  const ENGLISH_LANGUAGE = 'en';
  const ENGLISH_TRANSLATIONS = require('./../../../assets/i18n/en.json');

  const formGroupDataMock = {
    info: {
      firstname: 'Jack',
      lastname: 'Smith',
      month: 15,
      year: 2019,
    },
    agreeTerms: false,
    billingAddress: {
      sameOffice: true,
      address_1: '208 Concord Ave,',
      address_2: '208 Concord Test,',
      address_city: 'Cambridge',
      address_state: 'MW',
      address_zip: '55345',
      address_country: 'UNITED_STATES',
      address_province: 'MW',
      address_postalCode: '55345'
    },
  };

  beforeEach((() => {
    TestBed.configureTestingModule({
      imports: [
        TranslateTestingModule.withTranslations(ENGLISH_LANGUAGE, ENGLISH_TRANSLATIONS),
        ReactiveFormsModule,
        AddressModule,
        PhoneFieldModule,
        AuthFormFieldModule,
        CreditCardInfoModule,
        CreditCardBillingAddressModule,
      ],
      declarations: [
        RepresentativeInfoPaymentMethodPageComponent,
      ],
      providers: [
        {provide: UserService, useClass: UserServiceMock},
        {provide: SignUpService, useClass: SignUpServiceMock},
        {provide: OrganizationService, useClass: OrganizationServiceMock},
        {provide: ModalService, useClass: ModalServiceMock},
        {provide: ConfigDataService, useClass: ConfigDataServiceMock},
        {provide: FattService, useClass: FattServiceMock},
        {provide: PaymentService, useClass: PaymentServiceMock},
        {provide: TaxesService, useClass: TaxesServiceMock},
        {provide: I18nService, useClass: I18nServiceMock},
        {provide: TaxesService, useClass: TaxesServiceMock},
        {provide: Router, useValue: routerSpy},
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RepresentativeInfoPaymentMethodPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('address form invalid when empty', () => {
    expect(component.formGroup.valid).toBeFalsy();
  });

  it('country field validity', () => {
    let errors = {};
    const officeAddressFormGroup = component.formGroup.get('officeAddress');
    const country = officeAddressFormGroup.get('country');
    country.setValue('');
    expect(country.valid).toBeFalsy();

    // country field is required
    errors = country.errors || {};
    expect(errors['required']).toBeTruthy();

    // Set country to something correct
    country.setValue('UNITED_STATES');
    errors = country.errors || {};
    expect(errors['required']).toBeFalsy();
  });

  it('line1 field validity', () => {
    let errors = {};
    const officeAddressFormGroup = component.formGroup.get('officeAddress');
    const line1 = officeAddressFormGroup.get('line1');
    expect(line1.valid).toBeFalsy();

    // country line1 is required
    errors = line1.errors || {};
    expect(errors['required']).toBeTruthy();

    // Set zipCode to something
    line1.setValue('     ');
    errors = noWhitespaceValidator(line1);
    expect(errors).toEqual({ required: true });

    // Set line1 to something correct
    line1.setValue('Concord Ave');
    errors = line1.errors || {};
    expect(errors['required']).toBeFalsy();
  });

  it('city field validity', () => {
    let errors = {};
    const officeAddressFormGroup = component.formGroup.get('officeAddress');
    const city = officeAddressFormGroup.get('city');
    expect(city.valid).toBeFalsy();

    // country city is required
    errors = city.errors || {};
    expect(errors['required']).toBeTruthy();

    // Set city to something
    city.setValue('     ');
    errors = noWhitespaceValidator(city);
    expect(errors).toEqual({ required: true });

    // Set city to something correct
    city.setValue('New York');
    errors = city.errors || {};
    expect(errors['required']).toBeFalsy();
  });

  it('state field validity', () => {
    let errors = {};
    const officeAddressFormGroup = component.formGroup.get('officeAddress');
    const state = officeAddressFormGroup.get('state');
    expect(state.valid).toBeFalsy();

    // country state is required
    errors = state.errors || {};
    expect(errors['required']).toBeTruthy();

    // Set state to something correct
    state.setValue('MW');
    errors = state.errors || {};
    expect(errors['required']).toBeFalsy();
  });

  it('province field validity', () => {
    let errors = {};
    const officeAddressFormGroup = component.formGroup.get('officeAddress');
    const province = officeAddressFormGroup.get('province');
    expect(province.valid).toBeFalsy();

    // country state is required
    errors = province.errors || {};
    expect(errors['required']).toBeTruthy();

    // Set province to something
    province.setValue('     ');
    errors = noWhitespaceValidator(province);
    expect(errors).toEqual({ required: true });

    // Set province to something correct
    province.setValue('Eastern Cape');
    errors = province.errors || {};
    expect(errors['required']).toBeFalsy();
  });

  it('zipCode field validity', () => {
    let errors = {};
    const officeAddressFormGroup = component.formGroup.get('officeAddress');
    const zipCode = officeAddressFormGroup.get('zipCode');
    expect(zipCode.valid).toBeFalsy();

    // zipCode is required
    errors = zipCode.errors || {};
    expect(errors['required']).toBeTruthy();

    // Set zipCode to something
    zipCode.setValue('Test');
    errors = zipCode.errors || {};
    expect(errors['pattern']).toBeTruthy();

    // Set province to something correct
    zipCode.setValue('55432');
    errors = zipCode.errors || {};
    expect(errors['required']).toBeFalsy();
  });

  it('postalCode field validity', () => {
    let errors = {};
    const officeAddressFormGroup = component.formGroup.get('officeAddress');
    const postalCode = officeAddressFormGroup.get('postalCode');
    expect(postalCode.valid).toBeFalsy();

    // zipCode is required
    errors = postalCode.errors || {};
    expect(errors['required']).toBeTruthy();

    // Set zipCode to something
    postalCode.setValue('     ');
    errors = noWhitespaceValidator(postalCode);
    expect(errors).toEqual({ required: true });

    // Set province to something correct
    postalCode.setValue('55432');
    errors = postalCode.errors || {};
    expect(errors['required']).toBeFalsy();
  });

  it('paymentMethodFormGroup invalid when empty', () => {
    expect(component.paymentMethodFormGroup.valid).toBeFalsy();
  });

  it('paymentMethodFormGroup validity', () => {
    const formGroup = component.paymentMethodFormGroup;

    // Set to something correct
    formGroup.setValue(formGroupDataMock);
    expect(formGroup.valid).toBeTruthy();
  });

  it('should router to profile after click btn', () => {
    component.goToProfile();

    expect(routerSpy.navigate).toHaveBeenCalledWith(
      ['account', 'profile'],
    );
  });
});
