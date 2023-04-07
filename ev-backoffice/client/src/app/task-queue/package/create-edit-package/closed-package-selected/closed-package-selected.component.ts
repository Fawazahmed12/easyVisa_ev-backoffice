import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { Subject } from 'rxjs';
import { ConfigDataService, ModalService, OrganizationService, PackagesService } from '../../../../core/services';

import { CreateApplicantFormGroupService } from '../../services';


@Component({
  selector: 'app-closed-package-selected',
  templateUrl: './closed-package-selected.component.html',
})
@DestroySubscribers()
export class ClosedPackageSelectedComponent implements OnInit, OnDestroy, AddSubscribers {
  @Input() packageTypeFormControl;

  private cancelModificationSubject$: Subject<boolean> = new Subject<boolean>();
  private goToClientsTabSubject$: Subject<boolean> = new Subject<boolean>();
  private subscribers: any = {};

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private modalService: ModalService,
    private configDataService: ConfigDataService,
    private packagesService: PackagesService,
    private organizationService: OrganizationService,
    private createApplicantFormGroupService: CreateApplicantFormGroupService,
  ) {
  }

  ngOnInit() {
  }

  addSubscribers() {
    this.subscribers.cancelModificationsSubscription = this.cancelModificationSubject$
    .subscribe(() => {
      this.createApplicantFormGroupService.resetFormGroup();
      this.packageTypeFormControl.enable();
      this.packagesService.removePackage();
      this.packagesService.removePackages();
      this.router.navigate(['task-queue', 'package', 'create']);
    });

    this.subscribers.cancelModificationsSubscription = this.goToClientsTabSubject$
    .subscribe(() => {
      this.router.navigate(['task-queue', 'clients']);
    });
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  cancelModification() {
    this.cancelModificationSubject$.next(true);
  }

  goToClientsTab() {
    this.goToClientsTabSubject$.next(true);
  }
}
