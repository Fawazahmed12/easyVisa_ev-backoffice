import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { RouterTestingModule } from '@angular/router/testing';

import { TranslateTestingModule } from 'ngx-translate-testing';

import { AuthService, XsrfAppLoadService } from '../../core/services';
import { AuthServiceMock, XsrfAppLoadServiceMock } from '../../core/testing';

import { AuthWrapperModule } from '../components/auth-wrapper/auth-wrapper.module';

import { LoginComponent } from './login.component';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  const ENGLISH_LANGUAGE = 'en';
  const ENGLISH_TRANSLATIONS = require('./../../../assets/i18n/en.json');

  beforeEach((() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        ReactiveFormsModule,
        RouterTestingModule,
        TranslateTestingModule.withTranslations(ENGLISH_LANGUAGE, ENGLISH_TRANSLATIONS),
        AuthWrapperModule,
      ],
      declarations: [LoginComponent],
      providers: [
        {provide: AuthService, useClass: AuthServiceMock},
        {provide: XsrfAppLoadService, useClass: XsrfAppLoadServiceMock},
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });


  it('form invalid when empty', () => {
    expect(component.loginGroup.valid).toBeFalsy();
  });

  it('username field validity', () => {
    let errors = {};
    const username = component.loginGroup.controls['username'];
    expect(username.valid).toBeFalsy();

    errors = username.errors || {};
    expect(errors['required']).toBeTruthy();
  });

  it('password field validity', () => {
    let errors = {};
    const password = component.loginGroup.controls['password'];
    expect(password.valid).toBeFalsy();

    errors = password.errors || {};
    expect(errors['required']).toBeTruthy();
  });
});
