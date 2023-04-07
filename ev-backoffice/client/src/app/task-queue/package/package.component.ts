import { Component, OnDestroy, OnInit } from '@angular/core';

import { ActivePackageComponent } from '../../components/active-package/active-package.component';
import { NotificationsService } from '../../core/services';

@Component({
  selector: 'app-package',
  templateUrl: './package.component.html',
})
export class PackageComponent implements OnInit, OnDestroy {

  constructor(
    private notificationsService: NotificationsService,
  ) {
  }

  ngOnInit() {
    this.notificationsService.showComponent$.next(ActivePackageComponent);
  }

  ngOnDestroy() {
    this.notificationsService.showComponent$.next(null);
  }
}
