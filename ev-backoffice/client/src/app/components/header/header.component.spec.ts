import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientModule } from '@angular/common/http';
import { RouterTestingModule } from '@angular/router/testing';
import { Component } from '@angular/core';

import { TranslateTestingModule } from 'ngx-translate-testing';

import { AuthService, UserService } from '../../core/services';
import { AuthServiceMock } from '../../core/testing/auth-service.mock';
import { UserServiceMock } from '../../core/testing/user-service.mock';

import { EntryComponent } from '../entry/entry.component';
import { HeaderComponent } from './header.component';

@Component({selector: 'app-language-select', template: ''})
class LanguageSelectMockComponent {}

@Component({selector: 'app-logo-message', template: ''})
class LogoMessageMockComponent {}

@Component({selector: 'app-active-package', template: ''})
class ActivePackageMockComponent {}

@Component({selector: 'app-select-organization-representative', template: ''})
class SelectOrganizationRepresentativeMockComponent {}

describe('HeaderComponent', () => {
  let component: HeaderComponent;
  let fixture: ComponentFixture<HeaderComponent>;
  const ENGLISH_LANGUAGE = 'en';
  const ENGLISH_TRANSLATIONS = require('./../../../assets/i18n/en.json');

  beforeEach((() => {

    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        TranslateTestingModule.withTranslations(ENGLISH_LANGUAGE, ENGLISH_TRANSLATIONS),
      ],
      declarations: [
        HeaderComponent,
        EntryComponent,
        LanguageSelectMockComponent,
        LogoMessageMockComponent,
        ActivePackageMockComponent,
        SelectOrganizationRepresentativeMockComponent,
      ],
      providers: [
        {provide: AuthService, useClass: AuthServiceMock},
        {provide: UserService, useClass: UserServiceMock},
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
