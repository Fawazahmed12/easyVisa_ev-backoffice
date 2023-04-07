import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TranslateTestingModule } from 'ngx-translate-testing';
import { Router } from '@angular/router';

import { ReactiveFormsModule } from '@angular/forms';
import { ClientWelcomeComponent } from './client-welcome.component';

describe('ClientWelcomeComponent', () => {
  let component: ClientWelcomeComponent;
  let fixture: ComponentFixture<ClientWelcomeComponent>;

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
      declarations: [ClientWelcomeComponent],
      providers: [
        {provide: Router, useValue: routerSpy},
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ClientWelcomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should route to attorney-sign-up', () => {
    component.goToProfile();
    expect(routerSpy.navigate).toHaveBeenCalledWith(
      ['dashboard', 'progress-status']
    );
  });
});
