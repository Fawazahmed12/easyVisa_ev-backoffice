import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { TranslateTestingModule } from 'ngx-translate-testing';
import { of } from 'rxjs';

import { SharedModule } from '../../shared/shared.module';
import { SignUpComponent } from './sign-up.component';
import { SignUpService } from '../services';
import { I18nServiceMock, ModalServiceMock, SignUpServiceMock } from '../../core/testing';
import { I18nService } from '../../core/i18n/i18n.service';
import { ModalService } from '../../core/services';
import { UsernamePasswordModule } from '../components/username-password/username-password.module';
import { NameEmailModule } from '../components/name-email/name-email.module';
import { noWhitespaceValidator } from '../validators/no-white-space.validator';

const routerSpy = {
  navigate: jasmine.createSpy('navigate')
};

describe('SignUpComponent', () => {
  let component: SignUpComponent;
  let fixture: ComponentFixture<SignUpComponent>;
  const ENGLISH_LANGUAGE = 'en';
  const ENGLISH_TRANSLATIONS = require('./../../../assets/i18n/en.json');

  beforeEach((() => {
    TestBed.configureTestingModule({
      imports: [
        SharedModule,
        RouterTestingModule,
        UsernamePasswordModule,
        NameEmailModule,
        TranslateTestingModule.withTranslations(ENGLISH_LANGUAGE, ENGLISH_TRANSLATIONS),
      ],
      declarations: [SignUpComponent],
      providers: [
        {provide: SignUpService, useClass: SignUpServiceMock},
        {provide: I18nService, useClass: I18nServiceMock},
        {provide: ModalService, useValue: ModalServiceMock},
        {provide: Router, useValue: routerSpy},
        {
          provide: ActivatedRoute,
          useValue: {
                queryParams: of({
                  token: 'test token',
                })
              },
          }
      ],
    })
    .compileComponents();
  }));

  beforeEach((() => {
    fixture = TestBed.createComponent(SignUpComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('address form invalid when empty', () => {
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

  it('password field validity', () => {
    let errors = {};
    const password = component.formGroup.controls['password'];
    password.setValue('');
    expect(password.valid).toBeFalsy();

    // password field is required
    errors = password.errors || {};
    expect(errors['required']).toBeTruthy();

    // Set password to something correct
    password.setValue('Aa56!78%92^00');
    errors = password.errors || {};

    expect(errors['required']).toBeFalsy();
    expect(errors['minLength']).toBeFalsy();
    expect(errors['notStrength']).toBeFalsy();
    expect(errors['pattern']).toBeFalsy();
  });

  it('repeatPassword field validity', () => {
    let errors = {};
    const repeatPassword = component.formGroup.controls['repeatPassword'];
    expect(repeatPassword.valid).toBeFalsy();

    // password field is required
    errors = repeatPassword.errors || {};
    expect(errors['required']).toBeTruthy();

    // Set password to something correct
    repeatPassword.setValue('Aa56!78%92^00');
    errors = repeatPassword.errors || {};

    expect(errors['required']).toBeFalsy();
  });

  it('password/repeat password should be the same', () => {
    let formGroupErrors = {};

    const password = component.formGroup.controls['password'];
    const repeatPassword = component.formGroup.controls['repeatPassword'];

    password.setValue('abC@!1230111111111');
    repeatPassword.setValue('abC@!1230111111111');

    formGroupErrors = component.formGroup.errors || {};

    expect(formGroupErrors['mismatch']).toBeFalsy();
  });
});
