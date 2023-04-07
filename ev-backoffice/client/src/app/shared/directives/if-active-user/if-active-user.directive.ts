import { Directive, Input, OnDestroy, OnInit, TemplateRef, ViewContainerRef } from '@angular/core';

import { Observable } from 'rxjs';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { UserService } from '../../../core/services';


@DestroySubscribers()
@Directive({selector: '[appIfActiveUser]'})
export class IfActiveUserDirective implements OnInit, OnDestroy, AddSubscribers {
  @Input() appIfActiveUser;

  private activeMembership$: Observable<boolean>;

  private subscribers: any = {};

  constructor(
    private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef,
    private userService: UserService,
  ) {
  }

  ngOnInit() {
    this.activeMembership$ = this.userService.activeMembership$;
  }

  addSubscribers() {
    this.subscribers.activeMembershipSubscription = this.activeMembership$.pipe(
    ).subscribe((activeMembership) => {
        if (this.appIfActiveUser === activeMembership) {
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
