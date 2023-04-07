import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';
import { filter } from 'rxjs/operators';

import { OrganizationService } from '../core/services';


@Component({
  selector: 'app-redirect-employee',
  templateUrl: './redirect-employee.component.html',
})
@DestroySubscribers()
export class RedirectEmployeeComponent implements AddSubscribers, OnDestroy, OnInit {

  private subscribers: any = {};

  constructor(
    private router: Router,
    private organizationService: OrganizationService,
  ) {
  }

  ngOnInit() {
    console.log(`${this.constructor.name} Initialized`);
  }

  addSubscribers() {
    this.subscribers.organizationsSubscription = this.organizationService.organizationsGetRequest$.pipe(
      filter(res => res.loaded),
    ).subscribe(() => this.router.navigate(['task-queue', 'alerts']));
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }
}
