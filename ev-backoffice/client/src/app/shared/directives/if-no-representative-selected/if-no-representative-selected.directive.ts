import { Directive, Input, OnDestroy, OnInit, TemplateRef, ViewContainerRef } from '@angular/core';

import { Observable } from 'rxjs';
import { filter } from 'rxjs/operators';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { OrganizationService } from '../../../core/services';


@DestroySubscribers()
@Directive({selector: '[appIfNoRepresentativeSelected]'})
export class IfNoRepresentativeSelectedDirective implements OnInit, OnDestroy, AddSubscribers {
  @Input() appIfNoRepresentativeSelected;

  private currentRepresentativeId$: Observable<number>;

  private subscribers: any = {};

  constructor(
    private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef,
    private organizationService: OrganizationService,
  ) {
  }

  ngOnInit() {
    this.currentRepresentativeId$ = this.organizationService.currentRepresentativeId$.pipe(
      filter(val => val !== undefined),
    );
  }

  addSubscribers() {
    this.subscribers.activeMembershipSubscription = this.currentRepresentativeId$
    .subscribe((id) => {
        this.viewContainer.clear();
        this.viewContainer.createEmbeddedView(
          !!id ? this.templateRef : this.appIfNoRepresentativeSelected
        );
      }
    );
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }
}
