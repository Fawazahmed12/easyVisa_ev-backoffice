import { Observable, of, ReplaySubject } from 'rxjs';

import { EmployeePosition } from '../../account/permissions/models/employee-position.enum';

export class OrganizationServiceMock {
  currentPositionSubject$ = new ReplaySubject<EmployeePosition>(1);

  get currentPosition$(): Observable<EmployeePosition> {
    return this.currentPositionSubject$.asObservable();
  }

  updateAttorney(data?) {
    return of({});
  }

  getMenuOrganizations() {
  }
}
