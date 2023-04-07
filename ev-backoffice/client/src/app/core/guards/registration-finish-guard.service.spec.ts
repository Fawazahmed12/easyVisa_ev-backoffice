import { TestBed, inject } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientModule } from '@angular/common/http';

import { RegistrationFinishGuardService } from './registration-finish-guard.service';
import { AuthService, UserService } from '../services';
import { AuthServiceMock } from '../testing/auth-service.mock';
import { UserServiceMock } from '../testing/user-service.mock';

describe('RegistrationFinishGuardService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
      ],
      providers: [
        RegistrationFinishGuardService,
        {provide: AuthService, useValue: AuthServiceMock},
        {provide: UserService, useValue: UserServiceMock},
      ]
    });
  });

  it('should ...', inject([RegistrationFinishGuardService], (service: RegistrationFinishGuardService) => {
    expect(service).toBeTruthy();
  }));
});
