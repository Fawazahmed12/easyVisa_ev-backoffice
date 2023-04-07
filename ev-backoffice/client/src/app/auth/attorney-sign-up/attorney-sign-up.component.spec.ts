import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { TranslateTestingModule } from 'ngx-translate-testing';
import { from } from 'rxjs';

import { ConfigDataService, ModalService } from '../../core/services';
import { ConfigDataServiceMock } from '../../core/testing/config-data-service.mock';
import { SignUpServiceMock } from '../../core/testing/sign-up-service.mock';
import { I18nService } from '../../core/i18n/i18n.service';
import { I18nServiceMock } from '../../core/testing/i18n-service.mock';

import { noWhitespaceValidator } from '../validators/no-white-space.validator';
import { SignUpService } from '../services';

import { NameEmailModule } from '../components/name-email/name-email.module';
import { UsernamePasswordModule } from '../components/username-password/username-password.module';

import { AttorneySignUpComponent } from './attorney-sign-up.component';
import { ModalServiceMock } from '../../core/testing';

describe('AttorneySignUpComponent', () => {
  let component: AttorneySignUpComponent;
  let fixture: ComponentFixture<AttorneySignUpComponent>;

  const ENGLISH_LANGUAGE = 'en';
  const ENGLISH_TRANSLATIONS = require('./../../../assets/i18n/en.json');

  const routerSpy = {
    navigate: jasmine.createSpy('navigate')
  };

  beforeEach((() => {
    TestBed.configureTestingModule({
      imports: [
        TranslateTestingModule.withTranslations(ENGLISH_LANGUAGE, ENGLISH_TRANSLATIONS),
        FormsModule,
        ReactiveFormsModule,
        NameEmailModule,
        UsernamePasswordModule,
      ],
      declarations: [
        AttorneySignUpComponent,
      ],
      providers: [
        {provide: ConfigDataService, useClass: ConfigDataServiceMock},
        {provide: SignUpService, useClass: SignUpServiceMock},
        {provide: I18nService, useClass: I18nServiceMock},
        {provide: ModalService, useClass: ModalServiceMock},
        {provide: Router, useValue: routerSpy},
        {provide: noWhitespaceValidator, useValue: noWhitespaceValidator},
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
    fixture = TestBed.createComponent(AttorneySignUpComponent);
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
    let errors = {};
    const email = component.formGroup.controls['email'];
    expect(email.valid).toBeFalsy();

    // Email field is required
    errors = email.errors || {};
    expect(errors['required']).toBeTruthy();

    // Set email to something
    email.setValue('test');
    errors = email.errors || {};
    expect(errors['required']).toBeFalsy();
    expect(errors['email']).toBeTruthy();

    // Set email to something correct
    email.setValue('test@example.com');
    errors = email.errors || {};
    expect(errors['required']).toBeFalsy();
    expect(errors['email']).toBeFalsy();
  });

  it('password/repeat password fields validity', () => {
    let passwordErrors = {};
    let repeatPasswordErrors = {};
    let formGroupErrors = {};

    const password = component.formGroup.controls['password'];
    const repeatPassword = component.formGroup.controls['repeatPassword'];

    password.setValue('abC@!12301111111111');
    repeatPassword.setValue('abC@!12301111111111');

    passwordErrors = password.errors || {};
    repeatPasswordErrors = password.errors || {};
    formGroupErrors = component.formGroup.errors || {};

    expect(passwordErrors['required']).toBeFalsy();
    expect(passwordErrors['minLength']).toBeFalsy();
    expect(repeatPasswordErrors['required']).toBeFalsy();
    expect(formGroupErrors['mismatch']).toBeFalsy();
  });

  it('should route to registration page', () => {
    component.goRegistrationPage();
    expect(routerSpy.navigate).toHaveBeenCalledWith(
      ['auth', 'registration'],
      Object({queryParamsHandling: 'merge'})
    );
  });
});
