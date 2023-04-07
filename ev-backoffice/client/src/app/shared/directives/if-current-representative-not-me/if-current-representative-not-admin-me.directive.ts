import { Directive, Input, OnDestroy, OnInit, TemplateRef, ViewContainerRef } from '@angular/core';

import { Observable } from 'rxjs';
import { withLatestFrom } from 'rxjs/operators';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { OrganizationService, UserService } from '../../../core/services';


@DestroySubscribers()
@Directive({selector: '[appIfCRTRepNotMeOrAdmin]'})
export class IfCurrentRepresentativeNotAdminMeDirective implements OnInit, OnDestroy, AddSubscribers {
  @Input() appIfCRTRepNotMeOrAdmin = false;
  private activeMembership$: Observable<boolean>;

  private subscribers: any = {};

  constructor(
    private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef,
    private userService: UserService,
    private organizationService: OrganizationService,
  ) {
  }

  ngOnInit() {
    this.activeMembership$ = this.userService.activeMembership$;
  }

  addSubscribers() {
    this.subscribers.activeMembershipSubscription = this.userService.isCurrentRepresentativeMe$.pipe(
      withLatestFrom(this.organizationService.isAdmin$)
    ).subscribe(([isCurrentRepresentativeMe, isAdmin]) => {
        this.viewContainer.clear();
        if (isCurrentRepresentativeMe || isAdmin && this.appIfCRTRepNotMeOrAdmin) {
          this.viewContainer.createEmbeddedView(this.templateRef);
        }
      }
    );
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }
}
