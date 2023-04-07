import { Directive, Input, OnDestroy, OnInit, TemplateRef, ViewContainerRef } from '@angular/core';

import { Observable } from 'rxjs';
import { filter } from 'rxjs/operators';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { OrganizationService } from '../../../core/services';
import { Organization } from '../../../core/models/organization.model';

@DestroySubscribers()
@Directive({selector: '[appIfActiveOrganization]'})
export class IfActiveOrganizationDirective implements OnInit, OnDestroy, AddSubscribers {
  @Input() appIfActiveOrganization;

  private activeOrganization$: Observable<Organization>;
  private subscribers: any = {};

  constructor(
    private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef,
    private organizationService: OrganizationService,
  ) {
  }

  ngOnInit() {
    this.activeOrganization$ = this.organizationService.activeOrganization$;
  }

  addSubscribers() {
    this.subscribers.activeOrganizationSubscription = this.activeOrganization$.pipe(
      filter((activeOrganization) => !!activeOrganization),
    ).subscribe((organization) => {
        if (this.appIfActiveOrganization === organization.organizationType) {
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
