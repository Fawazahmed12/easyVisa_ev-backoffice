/* eslint-disable no-unused-vars, @typescript-eslint/no-unused-vars */

import { TestBed, waitForAsync } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { TranslateTestingModule } from 'ngx-translate-testing';

import { AuthService, NotificationsService, OrganizationService, PackagesService, UserService } from '../../core/services';
import { UserServiceMock } from '../../core/testing/user-service.mock';
import { NotificationsServiceMock } from '../../core/testing/notifications-service.mock';

import { NavComponent } from './nav.component';
import { PackagesServiceMock } from '../../core/testing/packages-service.mock';
import { OrganizationServiceMock } from '../../core/testing/organization-service.mock';
import { AuthServiceMock } from '../../core/testing';

describe('Component: Nav', () => {

  let component: NavComponent;
  const ENGLISH_LANGUAGE = 'en';
  const ENGLISH_TRANSLATIONS = require('./../../../assets/i18n/en.json');

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        NgbModule,
        RouterTestingModule,
        TranslateTestingModule.withTranslations(ENGLISH_LANGUAGE, ENGLISH_TRANSLATIONS),
      ],
      declarations: [
        NavComponent
      ],
      providers: [
        { provide: UserService, useClass: UserServiceMock },
        { provide: NotificationsService, useClass: NotificationsServiceMock },
        { provide: PackagesService, useClass: PackagesServiceMock },
        { provide: OrganizationService, useClass: OrganizationServiceMock },
        { provide: AuthService, useClass: AuthServiceMock },
      ],
    });

    const fixture = TestBed.createComponent(NavComponent);
    component = fixture.debugElement.componentInstance;
  });

  it('should create the app', waitForAsync(() => {
    expect(component).toBeTruthy();
  }));

});
