import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';

import { Observable } from 'rxjs';
import { filter, startWith } from 'rxjs/operators';
import { DestroySubscribers } from 'ngx-destroy-subscribers';

import { head } from 'lodash-es';

import { OrganizationService } from '../../../../core/services';
import { Organization } from '../../../../core/models/organization.model';

@Component({
  selector: 'app-assign-to',
  templateUrl: './assign-to.component.html',
})
@DestroySubscribers()
export class AssignToComponent implements OnInit, OnDestroy {
  @Input() representativeId: FormControl;
  @Input() isPackageEditType: boolean = null;
  activeOrganization$: Observable<Organization> = this.organizationService.activeOrganization$;

  private subscribers: any = {};

  constructor(
    private organizationService: OrganizationService,
  ) {
  }

  ngOnInit() {
    this.subscribers.activeRouteSubscription = this.representativeId.valueChanges.pipe(
      startWith<number, number>(this.representativeId.value),
      filter((id) => !!id),
    ).subscribe((id) => this.organizationService.getFeeSchedule(id));

    this.subscribers.activeRouteSubscription = this.organizationService.representativesMenu$.pipe(
      filter((representativesMenu) => !!representativesMenu && !this.isPackageEditType),
    ).subscribe((representativesMenu) => {
      if (!this.isPackageEditType) {
        if (representativesMenu.length === 1) {
          this.representativeId.patchValue(head(representativesMenu).id, {emitEvent: false});
          this.representativeId.disable({emitEvent: false});
        } else {
          this.representativeId.patchValue(null, {emitEvent: false});
          this.representativeId.enable({emitEvent: false});
        }
      }
    });
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }
}
