import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { map, pluck } from 'rxjs/operators';

import { Role } from '../core/models/role.enum';
import { UserService } from '../core/services';

@Component({
  selector: 'app-index',
  templateUrl: './index.component.html',
  styleUrls: [ './index.component.scss' ]
})

@DestroySubscribers()
export class IndexComponent implements OnInit, AddSubscribers, OnDestroy {

  private subscribers: any = {};

  constructor(
    private router: Router,
    private userService: UserService,
  ) {}

  ngOnInit() {
    console.log(`${this.constructor.name} Init`);
  }

  addSubscribers() {
    this.subscribers.currentUserSubscription = this.userService.currentUser$.pipe(
      pluck('roles'),
    ).subscribe((roles: Role[]) => {
      if (roles.some((role) => role === Role.ROLE_EMPLOYEE)) {
        this.router.navigate(['redirect-employee']);
      } else if (roles.some((role) => role !== Role.ROLE_USER)) {
        this.router.navigate(['dashboard', 'financial']);
      } else if (roles.some((role) => role === Role.ROLE_USER)) {
        this.router.navigate(['dashboard', 'progress-status']);
      } else {
        this.router.navigate(['account', 'profile']);
      }
    });
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }
}
