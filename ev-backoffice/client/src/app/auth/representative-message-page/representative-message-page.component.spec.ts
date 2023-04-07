import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserService } from '../../core/services';
import { TranslateTestingModule } from 'ngx-translate-testing';
import { Router } from '@angular/router';

import { RepresentativeMessagePageComponent } from './representative-message-page.component';
import { UserServiceMock } from '../../core/testing/user-service.mock';

describe('RepresentativeMessagePageComponent', () => {
  let component: RepresentativeMessagePageComponent;
  let fixture: ComponentFixture<RepresentativeMessagePageComponent>;

  const ENGLISH_LANGUAGE = 'en';
  const ENGLISH_TRANSLATIONS = require('./../../../assets/i18n/en.json');

  const routerSpy = {
    navigate: jasmine.createSpy('navigate')
  };

  beforeEach((() => {
    TestBed.configureTestingModule({
      imports: [
        TranslateTestingModule.withTranslations(ENGLISH_LANGUAGE, ENGLISH_TRANSLATIONS),
      ],
      declarations: [RepresentativeMessagePageComponent],
      providers: [
        {provide: UserService, useClass: UserServiceMock},
        {provide: Router, useValue: routerSpy},
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RepresentativeMessagePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should route to select-representative-type', () => {
    component.goToSelectRepresentativeType();
    expect(routerSpy.navigate).toHaveBeenCalledWith(
      ['auth', 'select-representative-type']
    );
  });

  it('should route to standard-ev-charges', () => {
    component.goToStandartEvCharges();
    expect(routerSpy.navigate).toHaveBeenCalledWith(
      ['auth', 'standard-ev-charges']
    );
  });
});
