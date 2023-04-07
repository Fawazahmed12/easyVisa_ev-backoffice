import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateTestingModule } from 'ngx-translate-testing';
import { from } from 'rxjs';

import { SharedModule } from '../../shared/shared.module';

import { AuthWrapperModule } from '../components/auth-wrapper/auth-wrapper.module';

import { ShowUsernameComponent } from './show-username.component';


describe('ShowUsernameComponent', () => {
  let component: ShowUsernameComponent;
  let fixture: ComponentFixture<ShowUsernameComponent>;
  const ENGLISH_LANGUAGE = 'en';
  const ENGLISH_TRANSLATIONS = require('./../../../assets/i18n/en.json');

  beforeEach((() => {
    TestBed.configureTestingModule({
      imports: [
        SharedModule,
        RouterTestingModule,
        AuthWrapperModule,
        TranslateTestingModule.withTranslations(ENGLISH_LANGUAGE, ENGLISH_TRANSLATIONS),
      ],
      declarations: [ShowUsernameComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: from([
              {
                showUsername: {
                  message: 'test message',
                  username: 'Jack'
                }
              }
            ]),
          }
        }
      ],
    })
    .compileComponents();
  }));

  beforeEach((() => {
    fixture = TestBed.createComponent(ShowUsernameComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
