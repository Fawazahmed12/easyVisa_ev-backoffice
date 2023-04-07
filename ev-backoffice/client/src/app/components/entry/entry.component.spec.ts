import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientModule } from '@angular/common/http';
import { RouterTestingModule } from '@angular/router/testing';
import { Component } from '@angular/core';

import { TranslateTestingModule } from 'ngx-translate-testing';

import { AuthService, UserService } from '../../core/services';
import { AuthServiceMock } from '../../core/testing/auth-service.mock';
import { UserServiceMock } from '../../core/testing/user-service.mock';

import { EntryComponent } from './entry.component';

@Component({selector: 'app-language-select', template: ''})
class LanguageSelectMockComponent {}

@Component({selector: 'app-select-organization-representative', template: ''})
class SelectOrganizationRepresentativeMockComponent {}

describe('EntryComponent', () => {
  let component: EntryComponent;
  let fixture: ComponentFixture<EntryComponent>;
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
        EntryComponent,
        LanguageSelectMockComponent,
        SelectOrganizationRepresentativeMockComponent
      ],
      providers: [
        {provide: UserService, useClass: UserServiceMock},
        {provide: AuthService, useClass: AuthServiceMock},
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EntryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
