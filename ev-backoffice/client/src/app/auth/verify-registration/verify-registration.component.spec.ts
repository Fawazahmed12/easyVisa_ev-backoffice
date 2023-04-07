import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { TranslateTestingModule } from 'ngx-translate-testing';
import { from } from 'rxjs';

import { SharedModule } from '../../shared/shared.module';
import { ModalService, UserService } from '../../core/services';
import { SpinnerModule } from '../../components/spinner/spinner.module';
import { ModalServiceMock, SignUpServiceMock, UserServiceMock } from '../../core/testing';

import { SignUpService } from '../services';

import { VerifyRegistrationComponent } from './verify-registration.component';


const routerSpy = {
  navigate: jasmine.createSpy('navigate')
};

describe('VerifyRegistrationComponent', () => {
  let component: VerifyRegistrationComponent;
  let fixture: ComponentFixture<VerifyRegistrationComponent>;
  const ENGLISH_LANGUAGE = 'en';
  const ENGLISH_TRANSLATIONS = require('./../../../assets/i18n/en.json');

  beforeEach((() => {
    TestBed.configureTestingModule({
      imports: [
        SharedModule,
        SpinnerModule,
        TranslateTestingModule.withTranslations(ENGLISH_LANGUAGE, ENGLISH_TRANSLATIONS),
      ],
      declarations: [VerifyRegistrationComponent],
      providers: [
        {provide: UserService, useClass: UserServiceMock},
        {provide: Router, useValue: routerSpy},
        {provide: SignUpService, useClass: SignUpServiceMock},
        {provide: ModalService, useClass: ModalServiceMock},
        {
          provide: ActivatedRoute,
          useValue: {
            queryParams: from([
              {
                token: 'test_token'
              }
            ]),
          }
        }
      ],
    })
    .compileComponents();
  }));

  beforeEach((() => {
    fixture = TestBed.createComponent(VerifyRegistrationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should route to login page', () => {
    expect(routerSpy.navigate).toHaveBeenCalledWith(
      ['auth', 'login']
    );
  });
});
