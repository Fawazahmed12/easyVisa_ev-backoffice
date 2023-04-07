import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { TranslateTestingModule } from 'ngx-translate-testing';
import { from } from 'rxjs';

import { ConfigDataService, FattService, ModalService, TaxesService, UserService } from '../../core/services';

import { I18nService } from '../../core/i18n/i18n.service';

import { SignUpService } from '../services';


import { noWhitespaceValidator } from '../validators/no-white-space.validator';

import { PaySignUpFeeComponent } from './pay-sign-up-fee.component';
import { AddressModule } from '../../components/address/address.module';
import {
  CreditCardBillingAddressModule
} from '../../components/credit-card-billing-address/credit-card-billing-address.module';
import {
  ConfigDataServiceMock,
  FattServiceMock,
  I18nServiceMock,
  SignUpServiceMock,
  TaxesServiceMock,
  UserServiceMock
} from '../../core/testing';
import { SpinnerModule } from '../../components/spinner/spinner.module';

describe('PaySignUpFeeComponent', () => {
  let component: PaySignUpFeeComponent;
  let fixture: ComponentFixture<PaySignUpFeeComponent>;

  const ENGLISH_LANGUAGE = 'en';
  const ENGLISH_TRANSLATIONS = require('./../../../assets/i18n/en.json');

  const routerSpy = {
    navigate: jasmine.createSpy('navigate')
  };

  const modalSpy = {
    openConfirmModal: jasmine.createSpy('openConfirmModal')
  };


  const formGroupDataMock = {
    info: {
      firstname: 'Jack',
      lastname: 'Smith',
      month: 15,
      year: 25,
    },
    agreeTerms: true,
    billingAddress: {
      sameOffice: false,
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
        FormsModule,
        ReactiveFormsModule,
        AddressModule,
        CreditCardBillingAddressModule,
        SpinnerModule
      ],
      declarations: [
        PaySignUpFeeComponent,
      ],
      providers: [
        { provide: ConfigDataService, useClass: ConfigDataServiceMock },
        { provide: UserService, useClass: UserServiceMock },
        { provide: SignUpService, useClass: SignUpServiceMock },
        { provide: ModalService, useValue: modalSpy },
        { provide: FattService, useClass: FattServiceMock },
        { provide: TaxesService, useClass: TaxesServiceMock },
        { provide: I18nService, useClass: I18nServiceMock },
        { provide: Router, useValue: routerSpy },
        { provide: noWhitespaceValidator, useValue: noWhitespaceValidator },
        {
          provide: ActivatedRoute,
          useValue: {
            data: from([
              {
                referringUser: {
                  message: 'test message'
                }
              }
            ]),
          }
        }
      ],
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PaySignUpFeeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('address form invalid when empty', () => {
    expect(component.officeAddressFormGroup.valid).toBeFalsy();
  });

  it('country field validity', () => {
    let errors = {};
    const country = component.officeAddressFormGroup.controls[ 'country' ];
    country.setValue('');
    expect(country.valid).toBeFalsy();

    // country field is required
    errors = country.errors || {};
    expect(errors[ 'required' ]).toBeTruthy();

    // Set country to something correct
    country.setValue('UNITED_STATES');
    errors = country.errors || {};
    expect(errors[ 'required' ]).toBeFalsy();
  });

  it('line1 field validity', () => {
    let errors = {};
    const line1 = component.officeAddressFormGroup.controls[ 'line1' ];
    expect(line1.valid).toBeFalsy();

    // country line1 is required
    errors = line1.errors || {};
    expect(errors[ 'required' ]).toBeTruthy();

    // Set zipCode to something
    line1.setValue('     ');
    errors = noWhitespaceValidator(line1);
    expect(errors).toEqual({ required: true });

    // Set line1 to something correct
    line1.setValue('Concord Ave');
    errors = line1.errors || {};
    expect(errors[ 'required' ]).toBeFalsy();
  });

  it('city field validity', () => {
    let errors = {};
    const city = component.officeAddressFormGroup.controls[ 'city' ];
    expect(city.valid).toBeFalsy();

    // country city is required
    errors = city.errors || {};
    expect(errors[ 'required' ]).toBeTruthy();

    // Set city to something
    city.setValue('     ');
    errors = noWhitespaceValidator(city);
    expect(errors).toEqual({ required: true });

    // Set city to something correct
    city.setValue('New York');
    errors = city.errors || {};
    expect(errors[ 'required' ]).toBeFalsy();
  });

  it('state field validity', () => {
    let errors = {};
    const state = component.officeAddressFormGroup.controls[ 'state' ];
    expect(state.valid).toBeFalsy();

    // country state is required
    errors = state.errors || {};
    expect(errors[ 'required' ]).toBeTruthy();

    // Set state to something correct
    state.setValue('MW');
    errors = state.errors || {};
    expect(errors[ 'required' ]).toBeFalsy();
  });

  it('province field validity', () => {
    let errors = {};
    const province = component.officeAddressFormGroup.controls[ 'province' ];
    expect(province.valid).toBeFalsy();

    // country state is required
    errors = province.errors || {};
    expect(errors[ 'required' ]).toBeTruthy();

    // Set province to something
    province.setValue('     ');
    errors = noWhitespaceValidator(province);
    expect(errors).toEqual({ required: true });

    // Set province to something correct
    province.setValue('Eastern Cape');
    errors = province.errors || {};
    expect(errors[ 'required' ]).toBeFalsy();
  });

  it('zipCode field validity', () => {
    let errors = {};
    const zipCode = component.officeAddressFormGroup.controls[ 'zipCode' ];
    expect(zipCode.valid).toBeFalsy();

    // zipCode is required
    errors = zipCode.errors || {};
    expect(errors[ 'required' ]).toBeTruthy();

    // Set zipCode to something
    zipCode.setValue('Test');
    errors = zipCode.errors || {};
    expect(errors[ 'pattern' ]).toBeTruthy();

    // Set province to something correct
    zipCode.setValue('55432');
    errors = zipCode.errors || {};
    expect(errors[ 'required' ]).toBeFalsy();
  });

  it('postalCode field validity', () => {
    let errors = {};
    const postalCode = component.officeAddressFormGroup.controls[ 'postalCode' ];
    expect(postalCode.valid).toBeFalsy();

    // zipCode is required
    errors = postalCode.errors || {};
    expect(errors[ 'required' ]).toBeTruthy();

    // Set zipCode to something
    postalCode.setValue('     ');
    errors = noWhitespaceValidator(postalCode);
    expect(errors).toEqual({ required: true });

    // Set province to something correct
    postalCode.setValue('55432');
    errors = postalCode.errors || {};
    expect(errors[ 'required' ]).toBeFalsy();
  });

  it('form group invalid when empty', () => {
    expect(component.formGroup.valid).toBeFalsy();
  });

  it('form group validity', () => {
    const formGroup = component.formGroup;

    // Set to something correct
    formGroup.setValue(formGroupDataMock);
    expect(formGroup.valid).toBeTruthy();
  });

  it('should route to rep-basic-info-page', () => {
    component.goToRepBasicInfo();
    expect(routerSpy.navigate).toHaveBeenCalledWith(
      ['auth', 'rep-basic-info'],
    );
  });

  it('should route to attorney welcome after complete payment method', () => {
    component.formGroup.patchValue(formGroupDataMock);
    component.formSubmit();

    expect(routerSpy.navigate).toHaveBeenCalledWith(
      ['auth', 'attorney-welcome'],
    );
  });

  it('should open pop up in case unchecked agreeTerms', () => {
    component.formGroup.patchValue({ agreeTerms: false });
    component.formSubmit();

    expect(modalSpy.openConfirmModal).toHaveBeenCalled();
  });
});
