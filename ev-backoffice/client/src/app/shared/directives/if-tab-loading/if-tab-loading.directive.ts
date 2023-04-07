import { Directive, Input, OnDestroy, OnInit, TemplateRef, ViewContainerRef } from '@angular/core';
import { NavigationCancel, NavigationEnd, NavigationStart, Router } from '@angular/router';

import { Observable } from 'rxjs';
import { debounceTime, filter } from 'rxjs/operators';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { OrganizationService } from '../../../core/services';


@DestroySubscribers()
@Directive({selector: '[appIfTabLoading]'})
export class IfTabLoadingDirective implements OnInit, OnDestroy, AddSubscribers {
  @Input() appIfTabLoading;

  private currentRepresentativeId$: Observable<number>;

  private subscribers: any = {};

  constructor(
    private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef,
    private organizationService: OrganizationService,
    private router: Router,
  ) {
  }

  ngOnInit() {
    this.currentRepresentativeId$ = this.organizationService.currentRepresentativeId$.pipe(
      filter(val => val !== undefined),
    );
  }

  addSubscribers() {
    this.subscribers.activeMembershipSubscription = this.router.events.pipe(
      filter(e => (e instanceof NavigationStart) || (e instanceof NavigationEnd) || (e instanceof NavigationCancel)),
      debounceTime(100),
    )
    .subscribe(e => {
        if (e instanceof NavigationStart) {
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
