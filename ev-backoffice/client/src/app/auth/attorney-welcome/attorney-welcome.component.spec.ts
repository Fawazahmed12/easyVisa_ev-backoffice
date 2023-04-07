import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';

import { TranslateTestingModule } from 'ngx-translate-testing';

import { ConfigDataService, OrganizationService } from '../../core/services';
import { ConfigDataServiceMock } from '../../core/testing';
import { OrganizationServiceMock } from '../../core/testing';

import { AttorneyWelcomeComponent } from './attorney-welcome.component';

describe('AttorneyWelcomeComponent', () => {
  let component: AttorneyWelcomeComponent;
  let fixture: ComponentFixture<AttorneyWelcomeComponent>;

  const ENGLISH_LANGUAGE = 'en';
  const ENGLISH_TRANSLATIONS = require('./../../../assets/i18n/en.json');

  const routerSpy = {
    navigate: jasmine.createSpy('navigate')
  };

  beforeEach((() => {
    TestBed.configureTestingModule({
      imports: [
        TranslateTestingModule.withTranslations(ENGLISH_LANGUAGE, ENGLISH_TRANSLATIONS),
        ReactiveFormsModule
      ],
      declarations: [AttorneyWelcomeComponent],
      providers: [
        {provide: ConfigDataService, useClass: ConfigDataServiceMock},
        {provide: OrganizationService, useClass: OrganizationServiceMock},
        {provide: Router, useValue: routerSpy},
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AttorneyWelcomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should route to attorney-sign-up', () => {
    component.goToProfile();
    expect(routerSpy.navigate).toHaveBeenCalledWith(
      ['account', 'profile']
    );
  });
});
