import { Directive, Input, OnDestroy, OnInit, TemplateRef, ViewContainerRef } from '@angular/core';

import { Observable } from 'rxjs';
import { filter } from 'rxjs/operators';
import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { OrganizationService } from '../../../core/services';
import { EmployeePosition } from '../../../account/permissions/models/employee-position.enum';

@DestroySubscribers()
@Directive({selector: '[appHideByPosition]'})
export class HideByPositionDirective implements OnInit, OnDestroy, AddSubscribers {
  @Input() appHideByPosition: EmployeePosition[];

  private currentPosition$: Observable<EmployeePosition>;
  private subscribers: any = {};

  constructor(
    private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef,
    private organizationService: OrganizationService,
  ) {
  }

  ngOnInit() {
    this.currentPosition$ = this.organizationService.currentPosition$;
  }

  addSubscribers() {
    this.subscribers.currentPositionSubscription = this.currentPosition$.pipe(
      filter((currentPosition) => !!currentPosition),
    ).subscribe((currentPosition) => {
        const hasInvalidPosition = !!this.appHideByPosition.find((position) => position === currentPosition);
        this.viewContainer.clear();
        if (!hasInvalidPosition) {
          this.viewContainer.createEmbeddedView(this.templateRef);
        }
      }
    );
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }
}
