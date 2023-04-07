import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AttorneyLandingPageComponent } from './attorney-landing-page.component';
import { ConfigDataService } from '../../core/services';
import { ConfigDataServiceMock } from '../../core/testing/config-data-service.mock';
import { TranslateTestingModule } from 'ngx-translate-testing';
import { ActivatedRoute, Router } from '@angular/router';
import { from } from 'rxjs';
import { ReactiveFormsModule } from '@angular/forms';
import { CardModule } from './card/card.module';

describe('AttorneyLandingPageComponent', () => {
  let component: AttorneyLandingPageComponent;
  let fixture: ComponentFixture<AttorneyLandingPageComponent>;

  const ENGLISH_LANGUAGE = 'en';
  const ENGLISH_TRANSLATIONS = require('./../../../assets/i18n/en.json');

  const routerSpy = {
    navigate: jasmine.createSpy('navigate')
  };

  beforeEach((() => {
    TestBed.configureTestingModule({
      imports: [
        TranslateTestingModule.withTranslations(ENGLISH_LANGUAGE, ENGLISH_TRANSLATIONS),
        ReactiveFormsModule,
        CardModule
      ],
      declarations: [AttorneyLandingPageComponent],
      providers: [
        {provide: ConfigDataService, useClass: ConfigDataServiceMock},
        {provide: Router, useValue: routerSpy},
        {
          provide: ActivatedRoute,
          useValue: {
            data: from([
              {
                referringUser: {
                  message: 'test message'
                }
              }
            ]),
          }
        }
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AttorneyLandingPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should route to attorney-sign-up', () => {
    component.signUpClick();
    expect(routerSpy.navigate).toHaveBeenCalledWith(
      ['auth', 'attorney-sign-up'],
      Object({queryParamsHandling: 'merge'})
    );
  });
});
