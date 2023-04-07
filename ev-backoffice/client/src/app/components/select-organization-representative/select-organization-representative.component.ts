import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';
import { filter } from 'rxjs/operators';

import { OrganizationService } from '../../core/services';

@Component({
  selector: 'app-select-organization-representative',
  templateUrl: './select-organization-representative.component.html',
})
@DestroySubscribers()
export class SelectOrganizationRepresentativeComponent implements OnInit, OnDestroy, AddSubscribers {

  public organizationIdFormControl: FormControl;
  public representativeIdFormControl: FormControl;

  private subscribers: any = {};


  constructor(
    private organizationService: OrganizationService,
  ) {
  }

  ngOnInit() {
    this.organizationIdFormControl = this.organizationService.organizationIdControl;
    this.representativeIdFormControl = this.organizationService.representativeIdControl;
  }

  addSubscribers() {
    this.subscribers.organizationIdSubscription = this.organizationIdFormControl.valueChanges.pipe(
      filter((value) => value !== undefined)
    )
    .subscribe((id) => {
      this.organizationService.changeActiveOrganizationAction(id);
    });
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
    this.organizationService.organizationIdControl.patchValue(null, {emitEvent: false});
  }
}
