import { Directive, Input, OnDestroy, OnInit, TemplateRef, ViewContainerRef } from '@angular/core';

import { Observable } from 'rxjs';
import { filter } from 'rxjs/operators';
import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { OrganizationService, UserService } from '../../../core/services';
import { User } from '../../../core/models/user.model';

import { rolesHasAccess } from '../../utils/roles-has-access';

@DestroySubscribers()
@Directive({selector: '[appHasRole]'})
export class HasRoleDirective implements OnInit, OnDestroy, AddSubscribers {
  @Input() appHasRole;

  private currentUser$: Observable<User>;
  private subscribers: any = {};

  constructor(
    private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef,
    private organizationService: OrganizationService,
    private userService: UserService,
  ) {
  }

  ngOnInit() {
    this.currentUser$ = this.userService.currentUser$;
  }

  addSubscribers() {
    this.subscribers.currentUserSubscription = this.currentUser$.pipe(
      filter((currentUser) => !!currentUser),
    ).subscribe((currentUser) => {
      const hasValidRole = rolesHasAccess(currentUser.roles, this.appHasRole);
      if (hasValidRole) {
          this.viewContainer.createEmbeddedView(this.templateRef);
        } else {
          this.viewContainer.clear();
        }
      }
    );
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }
}
