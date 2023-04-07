import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

import { TranslateTestingModule } from 'ngx-translate-testing';

import { SharedModule } from '../../shared/shared.module';
import { ConfigDataService, FeeScheduleService, ModalService, UserService } from '../../core/services';
import { ConfigDataServiceMock, ModalServiceMock, SignUpServiceMock, UserServiceMock } from '../../core/testing';

import { AuthWrapperModule } from '../components/auth-wrapper/auth-wrapper.module';

import { StandardEvChargesComponent } from './standard-ev-charges.component';
import { SignUpService } from '../services';
import { FeeScheduleSettingsServiceMock } from '../../core/testing/fee-schedule-settings-service.mock';


const routerSpy = {
  navigate: jasmine.createSpy('navigate')
};

describe('StandardEvChargesComponent', () => {
  let component: StandardEvChargesComponent;
  let fixture: ComponentFixture<StandardEvChargesComponent>;
  const ENGLISH_LANGUAGE = 'en';
  const ENGLISH_TRANSLATIONS = require('./../../../assets/i18n/en.json');

  beforeEach((() => {
    TestBed.configureTestingModule({
      imports: [
        SharedModule,
        CommonModule,
        RouterTestingModule,
        AuthWrapperModule,
        TranslateTestingModule.withTranslations(ENGLISH_LANGUAGE, ENGLISH_TRANSLATIONS),
      ],
      declarations: [StandardEvChargesComponent],
      providers: [
        {provide: UserService, useClass: UserServiceMock},
        {provide: Router, useValue: routerSpy},
        {provide: ConfigDataService, useClass: ConfigDataServiceMock} ,
        {provide: SignUpService, useClass: SignUpServiceMock} ,
        {provide: FeeScheduleService, useClass: FeeScheduleSettingsServiceMock} ,
        {provide: ModalService, useClass: ModalServiceMock} ,
      ],
    })
    .compileComponents();
  }));

  beforeEach((() => {
    fixture = TestBed.createComponent(StandardEvChargesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should route after press previous button to representative-message-page for Member of Law Firm', () => {
    component.goToSelectRepresentativeType();
    expect(routerSpy.navigate).toHaveBeenCalledWith(
      ['auth', 'representative-message-page']
    );
  });

  it('should route after I Agree button to rep-basic-info for Member of Law Firm', () => {
    component.goToRepBasicInfo();
    expect(routerSpy.navigate).toHaveBeenCalledWith(
      ['auth', 'rep-basic-info']
    );
  });
});
