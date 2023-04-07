import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { TranslateTestingModule } from 'ngx-translate-testing';
import { from } from 'rxjs';

import { AuthFormFieldModule } from '../../components/auth-form-field/auth-form-field.module';
import { AddressModule } from '../../components/address/address.module';
import { PhoneFieldModule } from '../../components/phone-field/phone-field.module';
import { OrganizationService, UserService } from '../../core/services';
import {
  I18nServiceMock,
  OrganizationServiceMock,
  SignUpServiceMock,
  UserServiceMock
} from '../../core/testing';

import { noWhitespaceValidator } from '../validators/no-white-space.validator';
import { SignUpService } from '../services';

import { RepBasicInfoComponent } from './rep-basic-info.component';
import { I18nService } from '../../core/i18n/i18n.service';


describe('RepBasicInfoComponent', () => {
  let component: RepBasicInfoComponent;
  let fixture: ComponentFixture<RepBasicInfoComponent>;

  const ENGLISH_LANGUAGE = 'en';
  const ENGLISH_TRANSLATIONS = require('./../../../assets/i18n/en.json');

  const routerSpy = {
    navigate: jasmine.createSpy('navigate')
  };

  const addressMock = {
      line1: 'test_data',
      line2: null,
      city: 'test_data',
      province: 'test_data',
      country: 'UNITED_STATES',
      zipCode: 99545,
      postalCode: '99545',
      state: 'MW',
  };

  beforeEach((() => {
    TestBed.configureTestingModule({
      imports: [
        TranslateTestingModule.withTranslations(ENGLISH_LANGUAGE, ENGLISH_TRANSLATIONS),
        FormsModule,
        ReactiveFormsModule,
        AuthFormFieldModule,
        AddressModule,
        PhoneFieldModule,
      ],
      declarations: [
        RepBasicInfoComponent,
      ],
      providers: [
        {provide: UserService, useClass: UserServiceMock},
        {provide: OrganizationService, useClass: OrganizationServiceMock},
        {provide: SignUpService, useClass: SignUpServiceMock},
        {provide: I18nService, useClass: I18nServiceMock},
        {provide: Router, useValue: routerSpy},
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
    fixture = TestBed.createComponent(RepBasicInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    fixture.whenStable().then(() => {
      fixture.detectChanges();
    });
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('form invalid when empty', () => {
    expect(component.formGroup.valid).toBeFalsy();
  });

  it('firstName field validity', () => {
    let errors = {};
    const firstName = component.formGroup.controls['firstName'];
    firstName.setValue('');
    expect(firstName.valid).toBeFalsy();

    // firstName field is required
    errors = firstName.errors || {};
    expect(errors['required']).toBeTruthy();

    // Set firstName to something
    firstName.setValue('J ack');
    errors = firstName.errors || {};
    expect(errors['required']).toBeFalsy();
    expect(noWhitespaceValidator(firstName)).toEqual(null);

    // Set firstName to something correct
    firstName.setValue('Jack');
    errors = firstName.errors || {};
    expect(errors['required']).toBeFalsy();
    expect(noWhitespaceValidator(firstName)).toBeNull();
  });

  it('lastName field validity', () => {
    let errors = {};
    const lastName = component.formGroup.controls['lastName'];
    lastName.setValue('');
    expect(lastName.valid).toBeFalsy();

    // lastName field is required
    errors = lastName.errors || {};
    expect(errors['required']).toBeTruthy();

    // Set lastName to something
    lastName.setValue('S mith');
    errors = lastName.errors || {};
    expect(errors['required']).toBeFalsy();
    expect(noWhitespaceValidator(lastName)).toEqual(null);

    // Set lastName to something correct
    lastName.setValue('Smith');
    errors = lastName.errors || {};
    expect(errors['required']).toBeFalsy();
    expect(noWhitespaceValidator(lastName)).toBeNull();
  });

  it('email field validity', () => {
    const email = component.formGroup.controls['email'];
    expect(email.disabled).toBeTruthy();
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
    line1.setValue('');
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
    city.setValue('');
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
    state.setValue('');
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
    province.setValue('');
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
    zipCode.setValue('');
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
    postalCode.setValue('');
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

  it('should route to standard-ev-charges', () => {
    component.goToStandardEvCharges();
    expect(routerSpy.navigate).toHaveBeenCalledWith(
      ['auth', 'standard-ev-charges']
    );
  });

  it('should route to attorney-welcome in case form submit', () => {
    const officeAddressFormGroup = component.formGroup.get('officeAddress');
    officeAddressFormGroup.patchValue(addressMock);
    component.formSubmit();
    expect(routerSpy.navigate).toHaveBeenCalledWith(
      ['auth', 'attorney-welcome']
    );
  });
});
