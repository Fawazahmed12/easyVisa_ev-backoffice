import { Injectable } from '@angular/core';

import { Observable, ReplaySubject } from 'rxjs';
import { map } from 'rxjs/operators';

import { RequestState } from '../ngrx/utils';

import { User } from '../models/user.model';
import { Role } from '../models/role.enum';
import { AttorneyType } from '../models/attorney-type.enum';


const userMock = {

  profile: {
    profilePhoto: 'https://dev-api.easyvisa.com/api/public/users/137/profile-picture/1605719678364',
    twitterUrl: 'http://twitter.com',
    websiteUrl: 'test.easyvisa.com',
    youtubeUrl: 'http://youtube.com',
    attorneyType: 'MEMBER_OF_A_LAW_FIRM',
    balance: -401.1,
    creditBalance: null,
    easyVisaId: 'A0000010028',
    email: 'awesomeattorney@easyvisa.com',
    facebookUrl: 'http://facebook.com',
    faxNumber: '310-555-5678',
    firstName: 'Jack',
    feeSchedule: [
      {id: 348053, benefitCategory: 'NATURALIZATION', amount: 0},
      {id: 348054, benefitCategory: 'DISABILITY', amount: 0},
      {id: 348055, benefitCategory: 'SIX01', amount: 2343990},
      {id: 348056, benefitCategory: 'SIX01A', amount: 2228980},
      {id: 348057, benefitCategory: 'EAD', amount: 0},
      {id: 348058, benefitCategory: 'IR1', amount: 4980},
      {id: 348059, benefitCategory: 'IR2', amount: 42441808},
      {id: 348060, benefitCategory: 'IR5', amount: 4241906},
      {id: 348061, benefitCategory: 'F1_B', amount: 8970},
      {id: 348062, benefitCategory: 'F2_B', amount: 990},
      {id: 348063, benefitCategory: 'F3_B', amount: 8780},
      {id: 348064, benefitCategory: 'F4_B', amount: 9980},
      {id: 348065, benefitCategory: 'LPRSPOUSE', amount: 0},
      {id: 348066, benefitCategory: 'LPRCHILD', amount: 0},
      {id: 348068, benefitCategory: 'F1_A', amount: 0},
      {id: 348069, benefitCategory: 'F2_A', amount: 7780},
      {id: 348070, benefitCategory: 'F3_A', amount: 75670},
      {id: 348071, benefitCategory: 'F4_A', amount: 0},
      {id: 348072, benefitCategory: 'K1K3', amount: 13434230},
      {id: 348073, benefitCategory: 'K2K4', amount: 32340},
      {id: 348067, benefitCategory: 'REMOVECOND', amount: 88720}],
    id: 135,
    lastName: 'Smith',
    linkedinUrl: 'http://linkedin.com',
    middleName: null,
    mobilePhone: '(995 44)310-555-5679',
    officeAddress: {
      line1: '',
      line2: null,
      city: '',
      country: 'UNITED_STATES'
    },
    officeEmail: 'awesomeattorney@easyvisa.com',
    officePhone: '(7 840)310-555-1277',
  },
  roles: ['ROLE_ATTORNEY']
};

@Injectable()
export class UserServiceMock {

  currentUserRolesSubject$ = new ReplaySubject<User>(1);
  currentUserSubject$ = new ReplaySubject<any>(1);
  registrationRepresentativeTypeSubject$ = new ReplaySubject<AttorneyType>(1);
  convertToAttorneyRequestSubject$ = new ReplaySubject<RequestState<any>>(1);

  get currentUserRoles$(): Observable<User> {
    return this.currentUserRolesSubject$.asObservable();
  }

  get currentUser$(): Observable<any> {
    this.currentUserSubject$.next(userMock);

    return this.currentUserSubject$.asObservable();
  }

  get convertToAttorneyRequest$(): Observable<RequestState<any>> {
    this.convertToAttorneyRequestSubject$.next({
      loading: true,
      loaded: false,
      status: '',
      data: null
    });
    return this.convertToAttorneyRequestSubject$.asObservable();
  }

  get registrationRepresentativeType$(): Observable<AttorneyType> {
    this.registrationRepresentativeTypeSubject$.next(AttorneyType.MEMBER_OF_A_LAW_FIRM);
    return this.registrationRepresentativeTypeSubject$.asObservable();
  }

  hasAccess(role: Role) {
    return this.currentUserRoles$.pipe(
      map((user) => user.roles.some((userRole: Role) => userRole === role))
    );
  }
}
