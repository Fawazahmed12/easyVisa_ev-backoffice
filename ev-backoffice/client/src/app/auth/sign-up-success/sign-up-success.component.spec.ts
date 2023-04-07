import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';

import { TranslateTestingModule } from 'ngx-translate-testing';

import { SharedModule } from '../../shared/shared.module';
import { SignUpService } from '../services';
import { I18nServiceMock, SignUpServiceMock } from '../../core/testing';
import { I18nService } from '../../core/i18n/i18n.service';

import { AuthWrapperModule } from '../components/auth-wrapper/auth-wrapper.module';
import { SignUpSuccessComponent } from './sign-up-success.component';

const routerSpy = {
  navigate: jasmine.createSpy('navigate')
};

describe('SignUpSuccessComponent', () => {
  let component: SignUpSuccessComponent;
  let fixture: ComponentFixture<SignUpSuccessComponent>;
  const ENGLISH_LANGUAGE = 'en';
  const ENGLISH_TRANSLATIONS = require('./../../../assets/i18n/en.json');

  beforeEach((() => {
    TestBed.configureTestingModule({
      imports: [
        SharedModule,
        RouterTestingModule,
        AuthWrapperModule,
        TranslateTestingModule.withTranslations(ENGLISH_LANGUAGE, ENGLISH_TRANSLATIONS),
      ],
      declarations: [SignUpSuccessComponent],
      providers: [
        {provide: SignUpService, useValue: SignUpServiceMock},
        {provide: I18nService, useValue: I18nServiceMock},
        {provide: Router, useValue: routerSpy},
      ],
    })
    .compileComponents();
  }));

  beforeEach((() => {
    fixture = TestBed.createComponent(SignUpSuccessComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should route to select-representative-type', () => {
    component.redirectToSignUp();
    expect(routerSpy.navigate).toHaveBeenCalledWith(
      ['auth', 'attorney-sign-up']
    );
  });

  it('should route to login', () => {
    component.redirectToLoginPage();
    expect(routerSpy.navigate).toHaveBeenCalledWith(
      ['auth', 'login']
    );
  });
});
