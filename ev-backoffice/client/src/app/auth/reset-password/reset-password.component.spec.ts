import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { TranslateTestingModule } from 'ngx-translate-testing';
import { from } from 'rxjs';

import { PasswordMeterComponent } from '../../components/password-meter/password-meter.component';
import { AuthWrapperModule } from '../components/auth-wrapper/auth-wrapper.module';
import { ModalService } from '../../core/services';
import { ModalServiceMock } from '../../core/testing';

import { ResetPasswordComponent } from './reset-password.component';
import { ResetPasswordService } from './reset-password.service';
import { ResetPasswordServiceMock } from './reset-password-service.mock';

describe('ResetPasswordComponent', () => {
  let component: ResetPasswordComponent;
  let fixture: ComponentFixture<ResetPasswordComponent>;
  const ENGLISH_LANGUAGE = 'en';
  const ENGLISH_TRANSLATIONS = require('./../../../assets/i18n/en.json');

  const routerSpy = {
    navigate: jasmine.createSpy('navigate')
  };

  beforeEach((() => {
    TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        AuthWrapperModule,
        TranslateTestingModule.withTranslations(ENGLISH_LANGUAGE, ENGLISH_TRANSLATIONS),
      ],
      declarations: [
        ResetPasswordComponent,
        PasswordMeterComponent,
      ],
      providers: [
        {provide: ResetPasswordService, useClass: ResetPasswordServiceMock},
        {provide: ModalService, useClass: ModalServiceMock},
        {provide: Router, useValue: routerSpy},
        {
          provide: ActivatedRoute,
          useValue: {
            queryParams: from([
              {
                token: 'test_token'
              }
            ]),
          }
        },
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ResetPasswordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('address form invalid when empty', () => {
    expect(component.formGroup.valid).toBeFalsy();
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
  });

  it('token field validity', () => {
    let errors = {};
    const token = component.formGroup.controls['token'];
    token.setValue('');
    expect(token.valid).toBeFalsy();

    // token field is required
    errors = token.errors || {};
    expect(errors['required']).toBeTruthy();

    // Set token to something correct
    token.setValue('test_token');
    errors = token.errors || {};

    expect(errors['required']).toBeFalsy();
  });

  it('should route to login page after submit btn clicked', () => {
    component.formGroup.patchValue({
      password: 'Aa56!78%92^00',
      token: 'test_token'
    });
    component.formSubmit();

    expect(routerSpy.navigate).toHaveBeenCalledWith(
      ['auth', 'login']
    );
  });
});
