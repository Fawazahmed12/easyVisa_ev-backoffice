import { Observable, ReplaySubject } from 'rxjs';

export class PackagesServiceMock {

  activePackageIdSubject$ = new ReplaySubject<number>(1);

  get activePackageId$(): Observable<number> {
    return this.activePackageIdSubject$.asObservable();
  }
}
