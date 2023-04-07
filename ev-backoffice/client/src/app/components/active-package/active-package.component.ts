import { Component, OnInit } from '@angular/core';

import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';

import { Package } from '../../core/models/package/package.model';
import { PackageApplicant } from '../../core/models/package/package-applicant.model';
import { PackagesService, UserService } from '../../core/services';
import { User } from '../../core/models/user.model';
import { getPetitioner } from '../../shared/utils/get-petitioner';

@Component({
  selector: 'app-active-package',
  templateUrl: './active-package.component.html',
  styleUrls: ['./active-package.component.scss']
})
export class ActivePackageComponent implements OnInit {
  activePackage$: Observable<Package>;
  activePackageId$: Observable<number>;
  packageTitle$: Observable<string>;
  currentUser$: Observable<User>;

  constructor(
    private packagesService: PackagesService,
    private userService: UserService,
  ) {
  }

  ngOnInit() {
    this.currentUser$ = this.userService.currentUser$;
    this.activePackageId$ = this.packagesService.activePackageId$;
    this.activePackage$ = this.packagesService.activePackage$;
    this.packageTitle$ = this.activePackage$.pipe(
      filter((activePackage) => !!activePackage),
      map((activePackage) => {
        const [firstApplicant, secondApplicant, ] = activePackage.title.split(` + `);
        return [firstApplicant, secondApplicant].join(!!secondApplicant ? ' + ' : '');
      })
    );
  }
}
