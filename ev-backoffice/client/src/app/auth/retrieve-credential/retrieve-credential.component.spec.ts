import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';

import { TranslateTestingModule } from 'ngx-translate-testing';

import { RetrieveCredentialServiceMock } from '../../core/testing';

import { AuthWrapperModule } from '../components/auth-wrapper/auth-wrapper.module';

import { RetrieveCredentialComponent } from './retrieve-credential.component';
import { RetrieveCredentialService } from './retrieve-credential.service';
import { Router } from '@angular/router';

const routerSpy = {
  navigate: jasmine.createSpy('navigate').and.returnValue(true)
};

describe('RetrieveCredentialComponent', () => {
  let component: RetrieveCredentialComponent;
  let fixture: ComponentFixture<RetrieveCredentialComponent>;
  const ENGLISH_LANGUAGE = 'en';
  const ENGLISH_TRANSLATIONS = require('./../../../assets/i18n/en.json');

  beforeEach((() => {
    TestBed.configureTestingModule({
      imports: [
        ReactiveFormsModule,
        AuthWrapperModule,
        RouterTestingModule,
        TranslateTestingModule.withTranslations(ENGLISH_LANGUAGE, ENGLISH_TRANSLATIONS),
      ],
      declarations: [RetrieveCredentialComponent],
      providers: [
        {provide: RetrieveCredentialService, useClass: RetrieveCredentialServiceMock},
        {provide: Router, useValue: routerSpy},

      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RetrieveCredentialComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should router to retrieve-credential-success page after click btn in case forgot username',  () => {
    const retrieveCredentialGroup = component.retrieveCredentialGroup;
    retrieveCredentialGroup.setValue({
      credential: 'username',
      email: 'testmail@gmail.com'
    });
    component.onSubmit();
    expect(routerSpy.navigate).toHaveBeenCalledWith(
      ['auth', 'retrieve-credential-success']
    );
  });

  it('should router to retrieve-credential-success page after click btn in case forgot password',  () => {
    const retrieveCredentialGroup = component.retrieveCredentialGroup;
    retrieveCredentialGroup.setValue({
      credential: 'password',
      email: 'testmail@gmail.com'
    });
    component.onSubmit();
    expect(routerSpy.navigate).toHaveBeenCalledWith(
      ['auth', 'retrieve-credential-success']
    );
  });
});
