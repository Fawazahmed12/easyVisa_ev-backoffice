import { Component, forwardRef, Input, OnDestroy, OnInit } from '@angular/core';
import { ControlValueAccessor, FormControl, NG_VALUE_ACCESSOR } from '@angular/forms';

import { Observable } from 'rxjs';
import { filter, map, switchMapTo } from 'rxjs/operators';

import { DestroySubscribers } from 'ngx-destroy-subscribers';

import { Organization } from '../../../core/models/organization.model';
import { OrganizationType } from '../../../core/models/organization-type.enum';
import { ModalService, OrganizationService, UserService } from '../../../core/services';


@Component({
  selector: 'app-select-affiliation',
  templateUrl: './select-affiliation.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => SelectAffiliationComponent),
      multi: true
    }
  ]
})
@DestroySubscribers()
export class SelectAffiliationComponent implements OnInit, OnDestroy, ControlValueAccessor {
  @Input() toolTipTemplate;
  private subscribers: any = {};

  affiliatedOrganizations$: Observable<Organization[]>;
  isRecognizedOrganization$: Observable<boolean>;

  formControl = new FormControl();

  onChange: (value: string) => void;
  onTouched: () => void;

  constructor(
    private userService: UserService,
    private modalService: ModalService,
    private organizationService: OrganizationService,
  ) {
  }

  ngOnInit() {
    this.affiliatedOrganizations$ = this.organizationService.affiliatedOrganizations$;
    this.isRecognizedOrganization$ = this.organizationService.activeOrganization$.pipe(
      filter((activeOrganization) => !!activeOrganization),
      map((activeOrganization) => activeOrganization.organizationType === OrganizationType.RECOGNIZED_ORGANIZATION),
    );
  }

  addSubscribers() {
    this.subscribers.organizationIdSubscription = this.formControl.valueChanges.pipe(
      filter(() => !!this.onChange),
    ).subscribe((value) => this.onChange(value));

    this.subscribers.representativeIdSubscription = this.organizationService.currentRepIdOrgId$.pipe(
      switchMapTo(this.userService.isCurrentRepresentativeMe$.pipe(
        filter(isMe => typeof isMe === 'boolean'),
      )),
    ).subscribe((isMe) => {
      if (isMe) {
        this.formControl.enable({emitEvent: false});
      } else {
        this.formControl.disable({emitEvent: false});
      }
    });
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  writeValue(value) {
    this.formControl.patchValue(value, {emitEvent: false});
  }

  registerOnChange(fn) {
    this.onChange = fn;
  }

  registerOnTouched(fn) {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean) {
    isDisabled ?
      this.formControl.disable({emitEvent: false})
      :
      this.formControl.enable({emitEvent: false});
    // TODO fix ExpressionChangedAfterItHasBeenCheckedError need return to solve issue
    this.formControl.markAsTouched();
  }
}
